package com.iarchives.swf.flow.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.simpleworkflow.flow.ActivityExecutionContext;
import com.amazonaws.services.simpleworkflow.flow.ActivityExecutionContextProvider;
import com.amazonaws.services.simpleworkflow.flow.ActivityExecutionContextProviderImpl;

public class ImportZipActivityImpl implements ImportZipActivity {

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

	public static ActivityExecutionContext getActivityExecutionContext() {
		ActivityExecutionContextProvider provider = new ActivityExecutionContextProviderImpl();
		return provider.getActivityExecutionContext();
	}

	@Override
	public String processZip(String bucket, String zipFileKey)
			throws IOException, AmazonServiceException, AmazonClientException,
			InterruptedException {

		ActivityExecutionContext context = getActivityExecutionContext();

		// TODO: Download the zip file
		File dir = createTempDirectory();
		String zipFilePath = dir.getAbsolutePath() + "/temp.zip";
		AmazonS3 s3 = new AmazonS3Client();
		TransferManager tm = new TransferManager(s3);
		tm.download(bucket, zipFileKey, new File(zipFilePath)).waitForCompletion();
		
		File unzipDir = new File(dir.getAbsolutePath() + "/unzip");

		// TODO: Use the name of the zip file to create a "container" in the
		// test project (using iArchives API). The API can be called 2 ways.
		// If this code is running in the same context as the API app then we
		// can use Spring @Autowired to give this class the instance of the
		// ContainerRepository class. Then we can directly invoke methods.
		// Otherwise, we can called the REST API and parse the json response.

		// Use java.util.zip to extract the PDF files
		List<File> pdfFiles = unZipIt(new File(zipFilePath), unzipDir, ".pdf");

		// For each file...
		String prefix = "jobs/" + context.getWorkflowExecution().getRunId().replace('/', '_') + "/raw/";
		for (File pdf : pdfFiles) {
			// TODO: Create an "Image" object using the iArchives API
			//String imageKey = 
			
			// TODO: Upload the PDF file to S3 (use the key from the REST API)
			//tm.upload(bucket, imageKey, pdf);
		}

		// TODO: Cleanup local directory
		//deleteRecursive(dir);

		return null;
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
	private static List<File> unZipIt(File zipFile, File folder, String fileExtFilter)
			throws IOException {

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
			boolean skipFile = (fileExtFilter != null && !fileName.endsWith(fileExtFilter));
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
