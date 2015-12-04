package com.iarchives.swf.flow.workflow;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.simpleworkflow.flow.annotations.Execute;
import com.amazonaws.services.simpleworkflow.flow.annotations.Workflow;
import com.amazonaws.services.simpleworkflow.flow.annotations.WorkflowRegistrationOptions;

/**
 * Contract for PDF Container processing workflow
 */
@Workflow
@WorkflowRegistrationOptions(
		defaultExecutionStartToCloseTimeoutSeconds = 600, 
        defaultTaskStartToCloseTimeoutSeconds = 300)
public interface SimplePdfWorkflow {
	
//	@Autowired
//	ExtractTextActivityClient extaClient;
	
	/**
	 * This method is the "Start" method for the workflow
	 * 
	 * @param containerId
	 * @throws IOException
	 */
	@Execute(name = "ProcessContainer", version = "1.0")
    public void processContainer(String containerId) throws IOException;

}
