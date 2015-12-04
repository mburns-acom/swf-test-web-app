package com.iarchives.swf.flow.activity;

import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.simpleworkflow.flow.annotations.Activities;
import com.amazonaws.services.simpleworkflow.flow.annotations.ActivityRegistrationOptions;

@ActivityRegistrationOptions(defaultTaskScheduleToStartTimeoutSeconds = -1,
defaultTaskStartToCloseTimeoutSeconds = -1)
@Activities(version="1.0")
public interface ImportZipActivity {

	/**
	 * This method "imports" a zip file of PDF files into the system. A 
	 * "container" is created in the database and an "image" object is created
	 * for each pdf file. The pdf files are inserted into S3. Any other files
	 * in the zip file (such as xml files) are ignored. 
	 * 
	 * @param bucket - bucket name
	 * @param zipFileKey - the S3 key to the zip file.
	 * @return
	 * @throws IOException
	 * @throws AmazonServiceException
	 * @throws AmazonClientException
	 * @throws InterruptedException
	 */
	public String processZip(String bucket, String zipFileKey) throws IOException, AmazonServiceException, AmazonClientException, InterruptedException;

}
