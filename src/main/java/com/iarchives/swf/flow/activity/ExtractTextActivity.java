package com.iarchives.swf.flow.activity;

import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.simpleworkflow.flow.annotations.Activities;
import com.amazonaws.services.simpleworkflow.flow.annotations.ActivityRegistrationOptions;

@ActivityRegistrationOptions(defaultTaskScheduleToStartTimeoutSeconds = -1,
defaultTaskStartToCloseTimeoutSeconds = -1)
@Activities(version="1.0")
public interface ExtractTextActivity {

	/**
	 * This method processes a group of images called a container. For each
	 * image a text extraction will be performed.
	 * 
	 * @param containerId - the id for the database object that contains images
	 * @param object
	 * @return
	 * @throws IOException
	 * @throws AmazonServiceException
	 * @throws AmazonClientException
	 * @throws InterruptedException
	 */
	public String processContainer(String containerId, String object) throws IOException, AmazonServiceException, AmazonClientException, InterruptedException;

}
