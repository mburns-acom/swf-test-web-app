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

	public Map<String, Object> getLatestExecutionContext(DecisionTask task, AmazonSimpleWorkflow swf)
	{
		DescribeWorkflowExecutionRequest request = new DescribeWorkflowExecutionRequest();
		request.setDomain(workflowConfig.getDomain());
		request.setExecution(task.getWorkflowExecution());
		WorkflowExecutionDetail details = swf.describeWorkflowExecution(request);
		return JsonUtils.fromString(details.getLatestExecutionContext());
	}

	public int safeLongToInt(long l) {
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
	        throw new IllegalArgumentException
	            (l + " cannot be cast to int without changing its value.");
	    }
	    return (int) l;
	}
	
	public HistoryEvent getLastActivityBeforeDecisionTaskScheduled(List<HistoryEvent> events)
	{
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
	
	public List<HistoryEvent> getActivityEvents(List<HistoryEvent> events)
	{
		List<HistoryEvent> activityEvents = new ArrayList<HistoryEvent>(events);
		// TODO: convert to Java 7 code
//		activityEvents.removeIf(e -> !e.getEventType().startsWith("Activity"));
		return activityEvents;
	}
	
	public boolean isReadyToProcessZip(DecisionTask task, HistoryEvent trigger, List<HistoryEvent> events, Map<String, Object> context)
	{
		return trigger.getEventType().compareTo("WorkflowExecutionStarted") == 0;
	}
	
	public boolean isReadyToProcessImages(DecisionTask task, HistoryEvent trigger, List<HistoryEvent> events, Map<String, Object> context)
	{
		boolean ready = false;
		ActivityTaskCompletedEventAttributes attribs = trigger.getActivityTaskCompletedEventAttributes();
		if (attribs != null)
		{
			HistoryEvent scheduled = events.get(safeLongToInt(attribs.getScheduledEventId() - 1));
			if (scheduled != null)
			{
				ActivityTaskScheduledEventAttributes scheduledAttribs = scheduled.getActivityTaskScheduledEventAttributes();
				ready = scheduledAttribs != null && scheduledAttribs.getActivityType().getName().compareTo(workflowConfig.getActivities().get(WorkflowConfig.ACTIVITY_IMPORT_ZIP)) == 0;
			}
		}
		return ready;
	}
	
	public boolean isReadyForApproval(DecisionTask task, HistoryEvent trigger, List<HistoryEvent> events, Map<String, Object> context)
	{
		return true; //((String)context.getOrDefault("stage", "")).compareTo("processing") == 0 &&
//				((double)context.getOrDefault("pdfCount", 0d)) == ((double)context.getOrDefault("pdfDone", -1d)) &&
//				((double)context.getOrDefault("xmlCount", 0d)) == ((double)context.getOrDefault("xmlDone", -1d));
	}
	
	public String getApprovalOutcome(DecisionTask task, HistoryEvent trigger, List<HistoryEvent> events, Map<String, Object> context)
	{
		String outcome = null;

		if (trigger != null && trigger.getEventType().compareTo("ActivityTaskCompleted") == 0)
		{
			ActivityTaskCompletedEventAttributes attribs = trigger.getActivityTaskCompletedEventAttributes();
			if (attribs != null)
			{
				HistoryEvent scheduled = events.get(safeLongToInt(attribs.getScheduledEventId() - 1));
				if (scheduled != null)
				{
					ActivityTaskScheduledEventAttributes scheduledAttribs = scheduled.getActivityTaskScheduledEventAttributes();
					if (scheduledAttribs != null)
					{
//						if (scheduledAttribs.getActivityType().getName().compareTo(workflowConfig.getApproveBatchType()) == 0)
//						{
//							Map<String, Object> result = JsonUtils.fromString(attribs.getResult());
//							outcome = (String)result.get("outcome");
//						}
					}
				}
			}
		}

		return outcome;
	}
	
	public Map<String, Object> getEventInput(HistoryEvent event)
	{
		return JsonUtils.fromString(event.getWorkflowExecutionStartedEventAttributes().getInput());
	}
	
	public static RespondDecisionTaskCompletedRequest getDecisionTaskResponse(List<DecisionTask> tasks, AmazonSimpleWorkflow swf)
	{
		RespondDecisionTaskCompletedRequest response = null;
//		if (tasks.size() > 0)
//		{
//			DecisionTask task = tasks.get(0);
//			String taskToken = task.getTaskToken();
//			if (task != null && taskToken != null)
//			{
//				
//				Map<String, Object> context = getLatestExecutionContext(task, swf);
//
//				List<HistoryEvent> events = new ArrayList<HistoryEvent>();
//				for (DecisionTask t : tasks)
//					events.addAll(t.getEvents());
//			
//				response = new RespondDecisionTaskCompletedRequest();
//				response.setTaskToken(taskToken);
//
//				Map<String, Object> workflowInput = getEventInput(events.get(0));
//				HistoryEvent trigger = getLastActivityBeforeDecisionTaskScheduled(events);
//				
//				// increment counters for finished imaged processing
//				if (trigger != null && trigger.getEventType().compareTo("ActivityTaskCompleted") == 0)
//				{
//					ActivityTaskCompletedEventAttributes attribs = trigger.getActivityTaskCompletedEventAttributes();
//					if (attribs != null)
//					{
//						HistoryEvent scheduled = events.get(safeLongToInt(attribs.getScheduledEventId() - 1));
//						if (scheduled != null)
//						{
//							ActivityTaskScheduledEventAttributes scheduledAttribs = scheduled.getActivityTaskScheduledEventAttributes();
//							if (scheduledAttribs != null)
//							{
//								/*
//								if (scheduledAttribs.getActivityType().getName().compareTo(Settings.getProcessImageActivityType()) == 0)
//									context.put("pdfDone", 1d + (double)context.getOrDefault("pdfDone", 0d));
//								else if (scheduledAttribs.getActivityType().getName().compareTo(Settings.getProcessXmlActivityType()) == 0)
//									context.put("xmlDone", 1d + (double)context.getOrDefault("xmlDone", 0d));
//									*/
//							}
//						}
//					}
//				}
//				
//				List<Decision> decisions = new ArrayList<Decision>();
//
//				if (isReadyToProcessZip(task, trigger, events, context)) // no previous activity-related events, process zip
//				{
//					
//					context.put("stage", "initializing");
//					
//					Decision decision = new Decision();
//					decision.setDecisionType(DecisionType.ScheduleActivityTask);
//					ScheduleActivityTaskDecisionAttributes attributes = new ScheduleActivityTaskDecisionAttributes();
//
//					String priority = events.get(0).getWorkflowExecutionStartedEventAttributes().getTaskPriority();
//					
//					Map<String, Object> inputMap = new HashMap<String, Object>();
//					inputMap.put("bucket", (String)workflowInput.get("bucket"));
//					inputMap.put("object", (String)workflowInput.get("object"));
//					
//					String inputJson = JsonUtils.toString(inputMap);
//					
//					ActivityType activityType = new ActivityType();
//					activityType.setName(workflowConfig.getProcessZipActivityType());
//					activityType.setVersion(workflowConfig.getProcessZipActivityVersion());
//					
//					attributes.setActivityType(activityType);
//					attributes.setInput(inputJson);
//					
//					TaskList taskList = new TaskList();
//					taskList.setName(workflowConfig.getProcessZipTaskList());
//
//					attributes.setTaskList(taskList);
//					attributes.setTaskPriority(priority);
//
//					String uuid = java.util.UUID.randomUUID().toString();
//					attributes.setActivityId(uuid);
//
//					decision.setScheduleActivityTaskDecisionAttributes(attributes);
//					decisions.add(decision);
//				}
//				else if (isReadyToProcessImages(task, trigger, events, context)) // previous event is completed process zip activity
//				{
//					addProcessImagesDecisions(decisions, context, trigger, workflowInput, events);
//				}
//				else if (isReadyForApproval(task, trigger, events, context)) // all process image tasks are closed (complete, failed, cancelled)
//				{
//					context.put("stage", "approval pending");
//					Decision decision = new Decision();
//					decision.setDecisionType(DecisionType.ScheduleActivityTask);
//					ScheduleActivityTaskDecisionAttributes attributes = new ScheduleActivityTaskDecisionAttributes();
//
//					String priority = events.get(0).getWorkflowExecutionStartedEventAttributes().getTaskPriority();
//					
//					Map<String, Object> inputMap = new HashMap<String, Object>();
//					inputMap.put("priority", priority);
//					
//					String inputJson = JsonUtils.toString(inputMap);
//					
//					ActivityType activityType = new ActivityType();
//					activityType.setName(workflowConfig.getApproveBatchType());
//					activityType.setVersion(workflowConfig.getApproveBatchVersion());
//					
//					attributes.setActivityType(activityType);
//					attributes.setInput(inputJson);
//					
//					TaskList taskList = new TaskList();
//					taskList.setName(Settings.getApproveBatchTaskList());
//
//					attributes.setTaskList(taskList);
//					attributes.setTaskPriority(priority);
//
//					String uuid = java.util.UUID.randomUUID().toString();
//					attributes.setActivityId(uuid);
//
//					decision.setScheduleActivityTaskDecisionAttributes(attributes);
//					decisions.add(decision);
//
//				}
//				else
//				{
//					String outcome = getApprovalOutcome(task, trigger, events, context);
//					if (outcome != null)
//					{
//						switch (outcome)
//						{
//						case "approved":
//							addWorkflowCompleteDecisions(decisions);
//							break;
//						case "not-approved":
//							addProcessImagesDecisions(decisions, context, trigger, workflowInput, events);
//							break;
//						}	
//					}
//				}
//				response.setDecisions(decisions);
//				response.setExecutionContext(JsonUtils.toString(context));
//			}
//		}
		return response;
	}
	
	public void addProcessImagesDecisions(List<Decision> decisions, Map<String, Object> context, HistoryEvent trigger, Map<String, Object> workflowInput, List<HistoryEvent> events)
	{
		context.put("stage", "processing");

		double pdfCount = 0d;
		double xmlCount = 0d;
		
		HistoryEvent originalTrigger = null;
		if (trigger.getEventType().compareTo("ActivityTaskCompleted") == 0)
		{
			ActivityTaskCompletedEventAttributes attribs = trigger.getActivityTaskCompletedEventAttributes();
			if (attribs != null)
			{
				HistoryEvent scheduled = events.get(safeLongToInt(attribs.getScheduledEventId() - 1));
				if (scheduled != null)
				{
					ActivityTaskScheduledEventAttributes scheduledAttribs = scheduled.getActivityTaskScheduledEventAttributes();
					if (scheduledAttribs != null)
					{
						if (scheduledAttribs.getActivityType().getName().compareTo(workflowConfig.getActivities().get(WorkflowConfig.ACTIVITY_IMPORT_ZIP)) == 0)
						{
							originalTrigger = trigger;
							context.put("zipCompletedEventId", trigger.getEventId());
						}
						else
						{
							int id = (int)(double)context.get("zipCompletedEventId");
							originalTrigger = events.get(id - 1);
						}
					}
				}
			}
			
		}
		
		Map<String, Object> result = JsonUtils.fromString(originalTrigger.getActivityTaskCompletedEventAttributes().getResult());
		for (Object o : (List<Object>)result.get("files"))
		{

			String taskType = null;
			String taskVersion = null;
			String taskListName = null;
			
			String path = (String)o;
			if (path.endsWith(".pdf.zip"))
			{
				pdfCount += 1d;
//				taskType = workflowConfig.getProcessImageActivityType();
//				taskVersion = workflowConfig.getProcessImageActivityVersion();
//				taskListName = workflowConfig.getProcessImageTaskList();
			}
			else if (path.endsWith(".xml.zip"))
			{
				xmlCount += 1d;
//				taskType = workflowConfig.getProcessXmlActivityType();
//				taskVersion = workflowConfig.getProcessXmlActivityVersion();
//				taskListName = workflowConfig.getProcessXmlTaskList();
			}

			if (taskType != null)
			{
				Decision decision = new Decision();
				decision.setDecisionType(DecisionType.ScheduleActivityTask);
				ScheduleActivityTaskDecisionAttributes attributes = new ScheduleActivityTaskDecisionAttributes();

				String priority = events.get(0).getWorkflowExecutionStartedEventAttributes().getTaskPriority();
				
				Map<String, Object> inputMap = new HashMap<String, Object>();
				inputMap.put("bucket", (String)workflowInput.get("bucket"));
				inputMap.put("object", path);

				String inputJson = JsonUtils.toString(inputMap);
				
				ActivityType activityType = new ActivityType();
				activityType.setName(taskType);
				activityType.setVersion(taskVersion);
				
				attributes.setActivityType(activityType);
				attributes.setInput(inputJson);
				
				TaskList taskList = new TaskList();
				taskList.setName(taskListName);

				attributes.setTaskList(taskList);
				attributes.setTaskPriority(priority);

				String uuid = java.util.UUID.randomUUID().toString();
				attributes.setActivityId(uuid);

				decision.setScheduleActivityTaskDecisionAttributes(attributes);
				decisions.add(decision);
			}

		}
		
		context.put("pdfCount", pdfCount);
		context.put("xmlCount", xmlCount);
		context.remove("pdfDone");
		context.remove("xmlDone");
		
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
