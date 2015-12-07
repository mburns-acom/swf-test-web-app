package com.iarchives.swf.direct.workflow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.amazonaws.services.simpleworkflow.model.ActivityTask;
import com.amazonaws.services.simpleworkflow.model.PollForActivityTaskRequest;
import com.amazonaws.services.simpleworkflow.model.RespondActivityTaskCompletedRequest;
import com.amazonaws.services.simpleworkflow.model.RespondActivityTaskFailedRequest;
import com.amazonaws.services.simpleworkflow.model.TaskList;
import com.iarchives.swf.direct.config.WorkflowConfig;
import com.iarchives.swf.dto.Container;
import com.iarchives.swf.dto.Image;
import com.iarchives.swf.dto.Project;
import com.iarchives.swf.dto.RestClient;

@Component
public class ActivityWorker implements Runnable {

	private AmazonSimpleWorkflowClient swfClient;

	@Autowired
	private WorkflowConfig workflowConfig;

	@Autowired
	private RestClient restClient;
	
	@PostConstruct
	public void init() {
		swfClient = workflowConfig.getAmazonSimpleWorkflowClient();
		
		Thread activityWorker = new Thread(this, "activity-worker");
		activityWorker.start();
	}

	public static void deleteRecursive(File f) throws FileNotFoundException {
		if (f.isDirectory()) {
			for (File c : f.listFiles()) {
				deleteRecursive(c);
			}
		}
		
		if (!f.delete()) {
			throw new FileNotFoundException("Delete failed: " + f);
		}
	}

	public static File createTempDirectory() throws IOException {
		File temp;

		temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

		if (!(temp.delete())) {
			throw new IOException("Could not delete temp file: "
					+ temp.getAbsolutePath());
		}

		temp = new File(temp.getAbsolutePath() + ".d");
		if (!(temp.mkdir())) {
			throw new IOException("Could not create temp directory: "
					+ temp.getAbsolutePath());
		}

		return (temp);
	}

	@Override
	public void run() {

		TaskList defaultTaskList = new TaskList();
		defaultTaskList.setName(workflowConfig
				.getActivityWorkerTaskListToPoll());

		while (true) {

			PollForActivityTaskRequest request = new PollForActivityTaskRequest();
			request.setDomain(workflowConfig.getDomain());
			request.setTaskList(defaultTaskList);

			ActivityTask task = swfClient.pollForActivityTask(request);
			if (task != null && task.getTaskToken() != null) {

				Map<String, Object> output = null;

				boolean success = true;
				boolean respondOnSuccess = true; // set to false for async
													// manual tasks like
													// approvals

				try {
					String activityTypeName = task.getActivityType().getName();
					System.out.println("ACTIVITY: " + activityTypeName);
					switch (activityTypeName) {
					case WorkflowConfig.ACTIVITY_IMPORT_ZIP:
						output = processZip(task);
						break;
					case WorkflowConfig.ACTIVITY_GEN_THUMB:
						output = processThumbnails(task);
						break;
					case WorkflowConfig.ACTIVITY_EXTRACT_TEXT:
						output = processOcr(task);
						break;
					case WorkflowConfig.ACTIVITY_APPROVE_CONTAINER:
						respondOnSuccess = false;
						output = queueApproval(task);
						break;
					default:
						output = new HashMap<String, Object>();
						output.put("reason", "activity type unrecognized: "
								+ task.getActivityType().getName());
						success = false;
					}
				} catch (Exception e) {
					output = new HashMap<String, Object>();
					output.put("reason", "ERROR: " + e.toString());
					success = false;
				}

				if (success) {
					if (respondOnSuccess) {
						RespondActivityTaskCompletedRequest response = new RespondActivityTaskCompletedRequest();
						response.setTaskToken(task.getTaskToken());
						if (output != null && output.size() > 0)
							response.setResult(JsonUtils.toString(output));
						swfClient.respondActivityTaskCompleted(response);
					}
				} else {
					RespondActivityTaskFailedRequest response = new RespondActivityTaskFailedRequest();
					response.setTaskToken(task.getTaskToken());
					if (output != null && output.size() > 0)
						response.setDetails(JsonUtils.toString(output));
					swfClient.respondActivityTaskFailed(response);
				}
			}
		}
	}

	public Map<String, Object> processZip(ActivityTask task)
			throws IOException, InterruptedException {

		Map<String, Object> input = JsonUtils.fromString(task.getInput());

		Map<String, Object> result = new HashMap<String, Object>();
		String bucket = (String) input.get("bucket");
		String zipFileKey = (String) input.get("object");

		// TODO: Download the zip file
		File dir = createTempDirectory();
		String zipFilePath = dir.getAbsolutePath() + "/temp.zip";
		AmazonS3 s3 = new AmazonS3Client();
		TransferManager tm = new TransferManager(s3);
		tm.download(bucket, zipFileKey, new File(zipFilePath))
				.waitForCompletion();

		File unzipDir = new File(dir.getAbsolutePath() + "/unzip");

		// TODO: Use the name of the zip file to create a "container" in the
		// test project (using iArchives API). The API can be called 2 ways.
		// If this code is running in the same context as the API app then we
		// can use Spring @Autowired to give this class the instance of the
		// ContainerRepository class. Then we can directly invoke methods.
		// Otherwise, we can called the REST API and parse the json response.
		
		Project project = restClient.getProjectByGuid(workflowConfig.getPdfTestProjectGuid());
		Container root = restClient.getProjectRoot(project.getId());
		Calendar now = Calendar.getInstance();
		if (root == null) {
			root = new Container(null, new Long(-1L), project.getId(), "root",
					(short) 0, null, "", null, null);
			root = restClient.createContainer(root);
		}
		
		// Create the container for our zip files
		String contName = zipFileKey + "_" + System.currentTimeMillis();
		Container container = new Container(null, root.getId(),
				project.getId(), contName, (short) 1, null, "", null, null);
		container = restClient.createContainer(container);
		String prefix = "jobs/"
				+ task.getWorkflowExecution().getRunId().replace('/', '_')
				+ "/" + container.getGuid()
				+ "/raw/";
		
		result.put("prefix", prefix);
		result.put("containerId", container.getId());

		// Use java.util.zip to extract the PDF files
		List<File> pdfFiles = unZipIt(new File(zipFilePath), unzipDir, ".pdf");

		// For each file...
		for (File pdf : pdfFiles) {
			// Decide what to put here
			String s3Path = prefix;
			
			// Create an "Image" object using the iArchives API
			Image image = restClient.createImage(new Image(null, container
					.getId(), pdf.getName(), null, s3Path, null, null));
			String imageKey = prefix + "/" + image.getGuid();

			// Upload the PDF file to S3 (use the key from the REST API)
			tm.upload(bucket, imageKey, pdf);
		}

		// Cleanup local directory
		deleteRecursive(dir);

		return result;
	}

	private Map<String, Object> processThumbnails(ActivityTask task) throws IOException {
		Map<String, Object> input = JsonUtils.fromString(task.getInput());

		Map<String, Object> result = new HashMap<String, Object>();
		File dir = createTempDirectory();

		String bucket = (String) input.get("bucket");
		String prefix = (String) input.get("prefix");
		Long containerId = Long.parseLong((String) input.get("containerId"));

		// Get the images from the database
		List<Image> images = restClient.getImagesForContainer(containerId);
		
		for (Image image : images) {
			// TODO: Download each image
			
			// TODO: Generate a thumbnail
			
		}

		// Cleanup the directory
		deleteRecursive(dir);
		
		// TODO: Add any failures to the result map

		return result;
	}

	private Map<String, Object> processOcr(ActivityTask task) throws IOException {
		Map<String, Object> input = JsonUtils.fromString(task.getInput());

		Map<String, Object> result = new HashMap<String, Object>();
		File dir = createTempDirectory();

		String bucket = (String) input.get("bucket");
		String prefix = (String) input.get("prefix");
		Long containerId = Long.parseLong((String) input.get("containerId"));

		// Get the images from the database
		List<Image> images = restClient.getImagesForContainer(containerId);
		
		for (Image image : images) {
			// TODO: Download each image
			
			// TODO: Extract text
			
		}

		// Cleanup the directory
		deleteRecursive(dir);
		
		// TODO: Add any failures to the result map

		return result;
	}

	public Map<String, Object> queueApproval(ActivityTask task) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		String taskToken = task.getTaskToken();
		String runId = task.getWorkflowExecution().getRunId();
		Map<String, Object> input = JsonUtils.fromString(task.getInput());
		String priority = (String) input.get("priority");

		// insert details into database (but for now, just write to console)
		System.out.println("APPROVE: " + taskToken);

		return result;
	}

	public void completeApproval(String taskToken, String outcome) {

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("outcome", outcome);

		RespondActivityTaskCompletedRequest request = new RespondActivityTaskCompletedRequest();
		request.setTaskToken(taskToken);
		request.setResult(JsonUtils.toString(result));

		swfClient.respondActivityTaskCompleted(request);

	}

	/**
	 * Unzip files into a destination directory.
	 *
	 * @param zipFile - input zip file.
	 * @param folder - zip file output folder.
	 * @param fileExtFilter - if not null then only save files with this extension.
	 * @return returns a list of File objects for each saved file.
	 * @throws IOException - may throw an IOException if a file operation fails.
	 */
	private static List<File> unZipIt(File zipFile, File folder,
			String fileExtFilter) throws IOException {

		List<File> files = new ArrayList<File>();
		byte[] buffer = new byte[1024];

		// create output directory is not exists
		if (!folder.exists()) {
			folder.mkdirs();
		}

		// get the zip file content
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
		// get the zipped file list entry
		ZipEntry ze = zis.getNextEntry();

		while (ze != null) {
			String fileName = ze.getName();
			boolean skipFile = (fileExtFilter != null
					&& !fileName.endsWith(fileExtFilter));
			if (ze.isDirectory() || skipFile) {
				ze = zis.getNextEntry();
				continue;
			}
			fileName = new File(fileName).getName();

			// Save the file to the output location
			File newFile = new File(folder, fileName);
			FileOutputStream fos = new FileOutputStream(newFile);

			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			files.add(newFile);

			fos.close();
			ze = zis.getNextEntry();
		}

		zis.closeEntry();
		zis.close();

		return files;
	}

}
