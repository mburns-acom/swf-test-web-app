package com.iarchives.swf.direct.workflow;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.amazonaws.services.simpleworkflow.flow.ManualActivityCompletionClient;
import com.amazonaws.services.simpleworkflow.flow.ManualActivityCompletionClientFactory;
import com.amazonaws.services.simpleworkflow.flow.ManualActivityCompletionClientFactoryImpl;
import com.amazonaws.services.simpleworkflow.model.DefaultUndefinedException;
import com.amazonaws.services.simpleworkflow.model.DomainAlreadyExistsException;
import com.amazonaws.services.simpleworkflow.model.RegisterActivityTypeRequest;
import com.amazonaws.services.simpleworkflow.model.RegisterDomainRequest;
import com.amazonaws.services.simpleworkflow.model.RegisterWorkflowTypeRequest;
import com.amazonaws.services.simpleworkflow.model.Run;
import com.amazonaws.services.simpleworkflow.model.StartWorkflowExecutionRequest;
import com.amazonaws.services.simpleworkflow.model.TaskList;
import com.amazonaws.services.simpleworkflow.model.TypeAlreadyExistsException;
import com.amazonaws.services.simpleworkflow.model.WorkflowType;
import com.iarchives.swf.direct.config.WorkflowConfig;

@Component
public class SwfUtils {

	private AmazonSimpleWorkflowClient swfClient;
	
	@Autowired
	private WorkflowConfig workflowConfig;

	@PostConstruct
	public void init() {
		swfClient = workflowConfig.getAmazonSimpleWorkflowClient();
	}
	
	public void registerDomain() {

		RegisterDomainRequest domainRequest = new RegisterDomainRequest();

		domainRequest.setWorkflowExecutionRetentionPeriodInDays("30");
		domainRequest.setName(workflowConfig.getDomain());
		try {
			swfClient.registerDomain(domainRequest);
		} catch (DomainAlreadyExistsException e) {
			// No need to do anything if the domain already exists
		}
	}

	public void registerTypes() {

		registerDomain();

		RegisterActivityTypeRequest activityRequest = new RegisterActivityTypeRequest();
		RegisterWorkflowTypeRequest workflowRequest = new RegisterWorkflowTypeRequest();

		TaskList taskList = new TaskList();

		workflowRequest.setDefaultTaskList(taskList);
		workflowRequest.setDomain(workflowConfig.getDomain());
		workflowRequest.setDefaultTaskStartToCloseTimeout("NONE");
		workflowRequest.setDefaultExecutionStartToCloseTimeout("604800");
		workflowRequest.setDefaultChildPolicy("REQUEST_CANCEL");
		workflowRequest.setName(workflowConfig.getWorkflowType());
		workflowRequest.setVersion(workflowConfig.getWorkflowVersion());
		taskList.setName(workflowConfig.getDeciderTaskList());
		try {
			swfClient.registerWorkflowType(workflowRequest);
		} catch (TypeAlreadyExistsException e) {
			// Ignore if already exists
		}

		// Activities
		activityRequest.setDefaultTaskList(taskList);
		activityRequest.setDomain(workflowConfig.getDomain());
		activityRequest.setDefaultTaskStartToCloseTimeout("NONE");
		activityRequest.setDefaultTaskScheduleToStartTimeout("NONE");
		activityRequest.setDefaultTaskScheduleToCloseTimeout("NONE");
		activityRequest.setDefaultTaskHeartbeatTimeout("NONE");
		
		Map<String, String> activities = workflowConfig.getActivities();
		for (String name : activities.keySet()) {
			activityRequest.setName(name);
			activityRequest.setVersion(activities.get(name));
			taskList.setName("default");
			try {
				swfClient.registerActivityType(activityRequest);
			} catch (TypeAlreadyExistsException e) {
			}
		}
		
	}

	public String startWorkflow(String bucket, String object,
			String priority) {

		String runId = null;

		Map<String, Object> inputMap = new HashMap<String, Object>();
		inputMap.put("bucket", bucket);
		inputMap.put("object", object);

		String input = "";
		try {
			input = JsonUtils.toString(inputMap);
		} catch (Exception ex) {
			// TODO Improve handling
			ex.printStackTrace();
		}

		TaskList defaultTaskList = new TaskList();
		defaultTaskList.setName(workflowConfig.getDeciderTaskList());

		StartWorkflowExecutionRequest request = new StartWorkflowExecutionRequest();

		request.setDomain(workflowConfig.getDomain());
		request.setInput(input);
		request.setTaskPriority(priority);
		String uuid = java.util.UUID.randomUUID().toString();
		request.setWorkflowId(uuid);

		request.setTaskList(defaultTaskList);

		WorkflowType workflowType = new WorkflowType();
		workflowType.setName(workflowConfig.getWorkflowType());
		workflowType.setVersion(workflowConfig.getWorkflowVersion());
		request.setWorkflowType(workflowType);

		try {
			Run run = swfClient.startWorkflowExecution(request);
			runId = run.getRunId();
		} catch (DefaultUndefinedException e) {
			// TODO: Do we need to do anything here?
		}
		
		return runId;
	}
	
	public void completeWorkflow(String taskToken, String result) {
        ManualActivityCompletionClientFactory manualCompletionClientFactory = new ManualActivityCompletionClientFactoryImpl(swfClient);
        ManualActivityCompletionClient manualCompletionClient = manualCompletionClientFactory.getClient(taskToken);
        manualCompletionClient.complete(result);
	}

    public String generateImageUrl(String objectKey, String bucketName) {
    	
		try {
			AmazonS3 s3client = new AmazonS3Client(workflowConfig.getBasicAWSCredentials());
			
			System.out.println("Generating pre-signed URL.");
			java.util.Date expiration = new java.util.Date();
			long milliSeconds = expiration.getTime();
			milliSeconds += 1000 * 60 * 60; // Add 1 hour.
			expiration.setTime(milliSeconds);

			GeneratePresignedUrlRequest generatePresignedUrlRequest = 
				    new GeneratePresignedUrlRequest(bucketName, objectKey);
			generatePresignedUrlRequest.setMethod(HttpMethod.GET); 
			generatePresignedUrlRequest.setExpiration(expiration);

			URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest); 

			System.out.println("Pre-Signed URL = " + url.toString());
			
			return url.toString();
		} catch (AmazonServiceException exception) {
			System.out.println("Caught an AmazonServiceException, " +
					"which means your request made it " +
					"to Amazon S3, but was rejected with an error response " +
			"for some reason.");
			System.out.println("Error Message: " + exception.getMessage());
			System.out.println("HTTP  Code: "    + exception.getStatusCode());
			System.out.println("AWS Error Code:" + exception.getErrorCode());
			System.out.println("Error Type:    " + exception.getErrorType());
			System.out.println("Request ID:    " + exception.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, " +
					"which means the client encountered " +
					"an internal error while trying to communicate" +
					" with S3, " +
			"such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
    	
    	return null;
    }

}
