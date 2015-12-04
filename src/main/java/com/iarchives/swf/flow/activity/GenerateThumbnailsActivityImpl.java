package com.iarchives.swf.flow.activity;

import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;

public class GenerateThumbnailsActivityImpl implements GenerateThumbnailsActivity {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String processContainer(String containerId, String object)
			throws IOException, AmazonServiceException, AmazonClientException,
			InterruptedException {
		
		// TODO: Call the REST service to get container and image info
		
		// TODO: For each image...
		
			// TODO: Use ghostscript to generate a thumbnail
			// TODO: Save the resulting thumbnail file to S3
		
		// TODO: Respond with success or failure
		
		return null;
	}

}
