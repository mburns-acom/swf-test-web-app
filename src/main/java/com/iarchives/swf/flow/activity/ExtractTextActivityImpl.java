package com.iarchives.swf.flow.activity;

import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;

public class ExtractTextActivityImpl implements ExtractTextActivity {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String processContainer(String containerId, String object)
			throws IOException, AmazonServiceException, AmazonClientException,
			InterruptedException {
		
		// TODO: Call the REST service to get container and image info
		
		// TODO: For each image...
		
			// TODO: Use iTextPdf to extract the text
			// TODO: Save the resulting text file to S3
		
		// TODO: Respond with success or failure
		
		return null;
	}

}
