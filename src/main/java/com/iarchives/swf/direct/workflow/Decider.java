package com.iarchives.swf.direct.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.simpleworkflow.*;
import com.amazonaws.services.simpleworkflow.model.*;
import com.iarchives.swf.direct.config.WorkflowConfig;
import com.iarchives.swf.dto.RestClient;

@Component
public class Decider implements Runnable {

	private AmazonSimpleWorkflowClient swfClient;

	@Autowired
	private WorkflowConfig workflowConfig;
	
	@Autowired
	private RestClient restClient;
	
	@PostConstruct
	public void init() {
		swfClient = workflowConfig.getAmazonSimpleWorkflowClient();
		
		Thread deciderWorker = new Thread(this, "decider-worker");
		deciderWorker.start();
	}

	public Map<String, Object> getLatestExecutionContext(DecisionTask task, AmazonSimpleWorkflow swf) {
		DescribeWorkflowExecutionRequest request = new DescribeWorkflowExecutionRequest();
		request.setDomain(workflowConfig.getDomain());
		request.setExecution(task.getWorkflowExecution());
		WorkflowExecutionDetail details = swf.describeWorkflowExecution(request);
		return JsonUtils.fromString(details.getLatestExecutionContext());
	}

	public HistoryEvent getLastActivityBeforeDecisionTaskScheduled(List<HistoryEvent> events) {
		HistoryEvent event = null;
		for (int i = events.size() - 1; event == null && i > 0; i--)
		{
			HistoryEvent testEvent = events.get(i);
			if (testEvent.getEventType().compareTo("DecisionTaskScheduled") == 0) {
				event = events.get(i - 1);
			}
		}
		return event;
	}
	
	public List<HistoryEvent> getActivityEvents(List<HistoryEvent> events) {
		// Filter for activity events
		List<HistoryEvent> activityEvents = new ArrayList<HistoryEvent>();
		for (HistoryEvent e : events) {
			if (e.getEventType().startsWith("Activity")) {
				activityEvents.add(e);
			}
		}
		
		return activityEvents;
	}
	
	/**
	 * Test whether a task is ready for import. Since import is the first step
	 * in the workflow it is easy to determine whether the task is ready.
	 * 
	 * @param task
	 * @param trigger
	 * @param events
	 * @param context
	 * @return
	 */
	public boolean isReadyToImport(DecisionTask task, HistoryEvent trigger,
			List<HistoryEvent> events, Map<String, Object> context) {
		return trigger.getEventType().compareTo("WorkflowExecutionStarted") == 0;
	}
	
	/**
	 * Test whether the task is ready to perform the image processing task.
	 * The import task needs to have completed successfully.
	 * 
	 * @param task
	 * @param trigger
	 * @param events
	 * @param context
	 * @return
	 */
	public boolean isReadyToProcessImages(DecisionTask task,
			HistoryEvent trigger, List<HistoryEvent> events,
			Map<String, Object> context) {
		
		boolean ready = false;
		
		ActivityTaskCompletedEventAttributes attribs = trigger
				.getActivityTaskCompletedEventAttributes();
		if (attribs != null) {
			HistoryEvent scheduled = events.get(NumberUtils.safeLongToInt(attribs
					.getScheduledEventId() - 1));
			if (scheduled != null) {
				ActivityTaskScheduledEventAttributes scheduledAttribs = scheduled
						.getActivityTaskScheduledEventAttributes();
				ready = scheduledAttribs != null && scheduledAttribs.getActivityType().getName().compareTo(WorkflowConfig.ACTIVITY_IMPORT_ZIP) == 0;
			}
		}
		return ready;
	}
	
	/**
	 * Test whether the task is ready for the approval (QA) process. It must
	 * have successfully completed the creation of thumbnails and the text
	 * extraction activities.
	 * 
	 * @param task
	 * @param trigger
	 * @param events
	 * @param context
	 * @return
	 */
	public boolean isReadyForApproval(DecisionTask task, HistoryEvent trigger,
			List<HistoryEvent> events, Map<String, Object> context) {
		
		if (context.get("stage") != null
				&& context.get("stage").equals("processing")) {
			if (context.get(WorkflowConfig.ACTIVITY_GEN_THUMB) != null
					&& context.get(WorkflowConfig.ACTIVITY_GEN_THUMB).equals(
							WorkflowConfig.ACTIVITY_DONE_GEN_THUMB)
					&& context.get(WorkflowConfig.ACTIVITY_EXTRACT_TEXT) != null
					&& context.get(WorkflowConfig.ACTIVITY_EXTRACT_TEXT)
							.equals(WorkflowConfig.ACTIVITY_DONE_EXTRACT_TEXT)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Get the outcome of the approval process.
	 * 
	 * @param task
	 * @param trigger
	 * @param events
	 * @param context
	 * @return
	 */
	public String getApprovalOutcome(DecisionTask task, HistoryEvent trigger,
			List<HistoryEvent> events, Map<String, Object> context) {

		String outcome = null;

		if (trigger != null && trigger.getEventType().compareTo("ActivityTaskCompleted") == 0) {
			ActivityTaskCompletedEventAttributes attribs = trigger.getActivityTaskCompletedEventAttributes();
			if (attribs != null) {
				HistoryEvent scheduled = events.get(NumberUtils.safeLongToInt(attribs.getScheduledEventId() - 1));
				if (scheduled != null) {
					ActivityTaskScheduledEventAttributes scheduledAttribs = scheduled.getActivityTaskScheduledEventAttributes();
					if (scheduledAttribs != null) {
						if (scheduledAttribs.getActivityType().getName().compareTo(WorkflowConfig.ACTIVITY_APPROVE_CONTAINER) == 0) {
							String result = attribs.getResult();
							if (result.charAt(0) == '"') {
								result = result.replaceAll("[\"]", "");
							}
							outcome = result;
						}
					}
				}
			}
		}

		return outcome;
	}
	
	/**
	 * Get the event input data into a map.
	 * 
	 * @param event
	 * @return
	 */
	public Map<String, Object> getEventInput(HistoryEvent event) {
		return JsonUtils.fromString(event.getWorkflowExecutionStartedEventAttributes().getInput());
	}
	
	public RespondDecisionTaskCompletedRequest getDecisionTaskResponse(List<DecisionTask> tasks, AmazonSimpleWorkflow swf)
	{
		RespondDecisionTaskCompletedRequest response = null;
		
		if (tasks.size() > 0) {
			DecisionTask task = tasks.get(0);
			String taskToken = task.getTaskToken();
			
			if (task != null && taskToken != null) {
				
				Map<String, Object> context = getLatestExecutionContext(task, swf);

				List<HistoryEvent> events = new ArrayList<HistoryEvent>();
				for (DecisionTask t : tasks) {
					events.addAll(t.getEvents());
				}
			
				response = new RespondDecisionTaskCompletedRequest();
				response.setTaskToken(taskToken);

				Map<String, Object> workflowInput = getEventInput(events.get(0));
				HistoryEvent trigger = getLastActivityBeforeDecisionTaskScheduled(events);
				
				// Add the "Done" context for finished image processing
				if (trigger != null && trigger.getEventType().compareTo("ActivityTaskCompleted") == 0) {
					ActivityTaskCompletedEventAttributes attribs = trigger.getActivityTaskCompletedEventAttributes();
					if (attribs != null) {
						HistoryEvent scheduled = events.get(NumberUtils.safeLongToInt(attribs.getScheduledEventId() - 1));
						if (scheduled != null) {
							ActivityTaskScheduledEventAttributes scheduledAttribs = scheduled.getActivityTaskScheduledEventAttributes();
							if (scheduledAttribs != null) {
								if (scheduledAttribs.getActivityType().getName().compareTo(WorkflowConfig.ACTIVITY_GEN_THUMB) == 0) {
									context.put(WorkflowConfig.ACTIVITY_GEN_THUMB, WorkflowConfig.ACTIVITY_DONE_GEN_THUMB);
								} else if (scheduledAttribs.getActivityType().getName().compareTo(WorkflowConfig.ACTIVITY_EXTRACT_TEXT) == 0) {
									context.put(WorkflowConfig.ACTIVITY_EXTRACT_TEXT, WorkflowConfig.ACTIVITY_DONE_EXTRACT_TEXT);
								}
							}
						}
					}
				}
				
				List<Decision> decisions = new ArrayList<Decision>();

				if (isReadyToImport(task, trigger, events, context)) // no previous activity-related events, process zip
				{
					context.put("stage", "initializing");
					
					Decision decision = new Decision();
					decision.setDecisionType(DecisionType.ScheduleActivityTask);
					ScheduleActivityTaskDecisionAttributes attributes = new ScheduleActivityTaskDecisionAttributes();

					String priority = events.get(0).getWorkflowExecutionStartedEventAttributes().getTaskPriority();
					
					Map<String, Object> inputMap = new HashMap<String, Object>();
					inputMap.put("bucket", (String)workflowInput.get("bucket"));
					inputMap.put("object", (String)workflowInput.get("object"));
					
					String inputJson = JsonUtils.toString(inputMap);
					
					ActivityType activityType = new ActivityType();
					activityType.setName(WorkflowConfig.ACTIVITY_IMPORT_ZIP);
					activityType.setVersion(workflowConfig.getActivitiesVersion());
					
					attributes.setActivityType(activityType);
					attributes.setInput(inputJson);
					
					TaskList taskList = new TaskList();
					taskList.setName(workflowConfig.getActivityWorkerTaskListToPoll());

					attributes.setTaskList(taskList);
					attributes.setTaskPriority(priority);

					attributes.setActivityId(java.util.UUID.randomUUID().toString());

					decision.setScheduleActivityTaskDecisionAttributes(attributes);
					decisions.add(decision);
				}
				else if (isReadyToProcessImages(task, trigger, events, context)) // previous event is completed process zip activity
				{
					addProcessImagesDecisions(decisions, context, trigger, workflowInput, events);
				}
				else if (isReadyForApproval(task, trigger, events, context)) // all process image tasks are closed (complete, failed, cancelled)
				{
					context.put("stage", "approval pending");
					Decision decision = new Decision();
					decision.setDecisionType(DecisionType.ScheduleActivityTask);
					ScheduleActivityTaskDecisionAttributes attributes = new ScheduleActivityTaskDecisionAttributes();

					String priority = events.get(0).getWorkflowExecutionStartedEventAttributes().getTaskPriority();
					
					Map<String, Object> inputMap = new HashMap<String, Object>();
					inputMap.put("priority", priority);
					inputMap.put("containerId", NumberUtils.safeLong(context.get("containerId")));
					
					String inputJson = JsonUtils.toString(inputMap);
					
					ActivityType activityType = new ActivityType();
					activityType.setName(WorkflowConfig.ACTIVITY_APPROVE_CONTAINER);
					activityType.setVersion(workflowConfig.getActivitiesVersion());
					
					attributes.setActivityType(activityType);
					attributes.setInput(inputJson);
					
					TaskList taskList = new TaskList();
					taskList.setName(workflowConfig.getActivityWorkerTaskListToPoll());

					attributes.setTaskList(taskList);
					attributes.setTaskPriority(priority);

					attributes.setActivityId(java.util.UUID.randomUUID().toString());

					decision.setScheduleActivityTaskDecisionAttributes(attributes);
					decisions.add(decision);

				}
				else
				{
					String outcome = getApprovalOutcome(task, trigger, events, context);
					if (outcome != null)
					{
						switch (outcome)
						{
						case WorkflowConfig.ACTIVITY_QA_APPROVED:
							addWorkflowCompleteDecisions(decisions);
							break;
						case WorkflowConfig.ACTIVITY_QA_FAILED:
							addProcessImagesDecisions(decisions, context, trigger, workflowInput, events);
							break;
						}	
					}
				}
				response.setDecisions(decisions);
				response.setExecutionContext(JsonUtils.toString(context));
			}
		}
		return response;
	}
	
	/**
	 * Execute the thumbnail and text activities in parallel.
	 * 
	 * @param decisions
	 * @param context
	 * @param trigger
	 * @param workflowInput
	 * @param events
	 */
	public void addProcessImagesDecisions(List<Decision> decisions, Map<String, Object> context, HistoryEvent trigger, Map<String, Object> workflowInput, List<HistoryEvent> events)
	{
		context.put("stage", "processing");

		HistoryEvent originalTrigger = null;
		if (trigger.getEventType().compareTo("ActivityTaskCompleted") == 0)
		{
			ActivityTaskCompletedEventAttributes attribs = trigger.getActivityTaskCompletedEventAttributes();
			if (attribs != null)
			{
				HistoryEvent scheduled = events.get(NumberUtils.safeLongToInt(attribs.getScheduledEventId() - 1));
				if (scheduled != null)
				{
					ActivityTaskScheduledEventAttributes scheduledAttribs = scheduled.getActivityTaskScheduledEventAttributes();
					if (scheduledAttribs != null)
					{
						if (scheduledAttribs.getActivityType().getName().compareTo(WorkflowConfig.ACTIVITY_IMPORT_ZIP) == 0)
						{
							originalTrigger = trigger;
							context.put("importCompletedEventId", trigger.getEventId());
							context.putAll(JsonUtils.fromString(attribs.getResult()));
						}
						else
						{
							int id = (int)(double)context.get("importCompletedEventId");
							originalTrigger = events.get(id - 1);
						}
					}
				}
			}
		}
		
		// Schedule both WorkflowConfig.ACTIVITY_GEN_THUMB and WorkflowConfig.ACTIVITY_EXTRACT_TEXT
		// These will run on an entire "Container" rather than on individual images.

		// Create the ACTIVITY_GEN_THUMB activity
		Decision decision = new Decision();
		decision.setDecisionType(DecisionType.ScheduleActivityTask);
		ScheduleActivityTaskDecisionAttributes attributes = new ScheduleActivityTaskDecisionAttributes();
		String priority = events.get(0).getWorkflowExecutionStartedEventAttributes().getTaskPriority();
		
		Map<String, Object> inputMap = new HashMap<String, Object>();
		inputMap.put("bucket", (String) workflowInput.get("bucket"));
		inputMap.put("prefix", (String) context.get("prefix"));
		inputMap.put("containerId", NumberUtils.safeLong(context.get("containerId")));

		ActivityType activityType = new ActivityType();
		activityType.setName(WorkflowConfig.ACTIVITY_GEN_THUMB);
		activityType.setVersion(workflowConfig.getActivitiesVersion());
		
		attributes.setActivityType(activityType);
		attributes.setInput(JsonUtils.toString(inputMap));
		
		TaskList taskList = new TaskList();
		taskList.setName(workflowConfig.getActivityWorkerTaskListToPoll());

		attributes.setTaskList(taskList);
		attributes.setTaskPriority(priority);
		attributes.setActivityId(java.util.UUID.randomUUID().toString());

		decision.setScheduleActivityTaskDecisionAttributes(attributes);
		decisions.add(decision);

		// Create the ACTIVITY_EXTRACT_TEXT activity
		decision = new Decision();
		decision.setDecisionType(DecisionType.ScheduleActivityTask);
		attributes = new ScheduleActivityTaskDecisionAttributes();
		priority = events.get(0).getWorkflowExecutionStartedEventAttributes().getTaskPriority();
		
		inputMap = new HashMap<String, Object>();
		inputMap.put("bucket", (String) workflowInput.get("bucket"));
		inputMap.put("prefix", (String) context.get("prefix"));
		inputMap.put("containerId", NumberUtils.safeLong(context.get("containerId")));

		activityType = new ActivityType();
		activityType.setName(WorkflowConfig.ACTIVITY_EXTRACT_TEXT);
		activityType.setVersion(workflowConfig.getActivitiesVersion());
		
		attributes.setActivityType(activityType);
		attributes.setInput(JsonUtils.toString(inputMap));
		
		taskList = new TaskList();
		taskList.setName(workflowConfig.getActivityWorkerTaskListToPoll());

		attributes.setTaskList(taskList);
		attributes.setTaskPriority(priority);
		attributes.setActivityId(java.util.UUID.randomUUID().toString());

		decision.setScheduleActivityTaskDecisionAttributes(attributes);
		decisions.add(decision);
		
	}
	
	public void addWorkflowCompleteDecisions(List<Decision> decisions)
	{
		
		Decision decision = new Decision();
		decision.setDecisionType(DecisionType.CompleteWorkflowExecution);
		CompleteWorkflowExecutionDecisionAttributes attributes = new CompleteWorkflowExecutionDecisionAttributes();

		Map<String, Object> resultMap = new HashMap<String, Object>();
		String resultJson = JsonUtils.toString(resultMap);
		
		attributes.setResult(resultJson);
		decision.setCompleteWorkflowExecutionDecisionAttributes(attributes);
		decisions.add(decision);

	}

	@Override
	public void run() {
		
		TaskList defaultTaskList = new TaskList();
		defaultTaskList.setName(workflowConfig.getDeciderTaskList());
		
		while (true)
		{
			
			List<DecisionTask> tasks = new ArrayList<DecisionTask>();
			String nextPageToken = null;
			do
			{
				PollForDecisionTaskRequest request = new PollForDecisionTaskRequest();
				request.setDomain(workflowConfig.getDomain());
				request.setTaskList(defaultTaskList);
				request.setNextPageToken(nextPageToken);
				DecisionTask task = swfClient.pollForDecisionTask(request);
				tasks.add(task);
				nextPageToken = task.getNextPageToken();
			} while (nextPageToken != null);

			RespondDecisionTaskCompletedRequest response = getDecisionTaskResponse(tasks, swfClient);

			if (response != null)
				swfClient.respondDecisionTaskCompleted(response);
			
		}
	}

}
