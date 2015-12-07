package com.iarchives.swf.direct.workflow;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
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

}
