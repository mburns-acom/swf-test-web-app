package com.iarchives.swf.direct.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.amazonaws.services.simpleworkflow.flow.spring.WorkflowScope;

@Configuration
//@Component
@PropertySource({"classpath:application.properties"})
@Qualifier
@Scope("workflow")
public class WorkflowConfig {
	
	public static final String ACTIVITY_IMPORT_ZIP = "ImportZipActivity";
	public static final String ACTIVITY_GEN_THUMB = "GenerateThumbnailActivity";
	public static final String ACTIVITY_EXTRACT_TEXT = "ExtractTextActivity";
	public static final String ACTIVITY_APPROVE_CONTAINER = "ApproveContainerActivity";
	
	@Value("${swf.domain}")
	private String domain;
	
	@Value("${swf.workflow.type}")
	private String workflowType;
	
	@Value("${swf.workflow.version}")
	private String workflowVersion;
	
	@Value("${swf.activityworker.task.list}")
	private String activityWorkerTaskListToPoll;
			
	@Value("${swf.decider.task.list}")
	private String deciderTaskList;
		
	@Value("${aws.access.id}")
	private String swfAccessId;
	
	@Value("${aws.secret.key}")
	private String swfSecretKey;
	
	@Value("${s3.access.id}")
	private String s3AccessId;
	
	@Value("${s3.secret.key}")
	private String s3SecretKey;
	
	@Value("${aws.client.socket.timeout}")
	private int socketTimeout;
	
	@Value("${iarchives.rest.api.url}")
	private String iarchivesRestApiUrl;
	
	@Value("${pdf.test.project.guid}")
	private String pdfTestProjectGuid;
	
	@Bean
	public CustomScopeConfigurer customScope () {
	    CustomScopeConfigurer configurer = new CustomScopeConfigurer();
	    Map<String, Object> workflowScope = new HashMap<String, Object>();
	    workflowScope.put("workflow", new WorkflowScope());
	    configurer.setScopes(workflowScope);

	    return configurer;
	}
	
	@Bean(name = {"accesskeys"})
	public BasicAWSCredentials getBasicAWSCredentials() {
		return new BasicAWSCredentials(swfAccessId, swfSecretKey);
	}
	
	@Bean(name = {"clientConfiguration"})
	public ClientConfiguration getClientConfiguration() {
		ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.setSocketTimeout(socketTimeout);
		return clientConfig;
	}
	
	@Bean(name = {"swfClient"})
	public AmazonSimpleWorkflowClient getAmazonSimpleWorkflowClient() {
		AmazonSimpleWorkflowClient swfClient = new AmazonSimpleWorkflowClient(getBasicAWSCredentials(), getClientConfiguration());
		//swfClient.setEndpoint(endpoint);
		return swfClient;
	}
	
	/*
   <!--  activities client -->
   <bean id="activitiesClient" class="aws.flow.sample.MyActivitiesClientImpl" scope="workflow">
   </bean>

   <!-- workflow implementation -->
   <bean id="workflowImpl" class="aws.flow.sample.MyWorkflowImpl" scope="workflow">
      <property name="client" ref="activitiesClient"/>
      <aop:scoped-proxy proxy-target-class="false" />
   </bean>

   <!--  workflow worker -->
   <bean id="workflowWorker"
      class="com.amazonaws.services.simpleworkflow.flow.spring.SpringWorkflowWorker">
      <constructor-arg ref="swfClient" />
      <constructor-arg value="domain1" />
      <constructor-arg value="tasklist1" />
      <property name="registerDomain" value="true" />
      <property name="domainRetentionPeriodInDays" value="1" />
      <property name="workflowImplementations">
         <list>
            <ref bean="workflowImpl" />
         </list>
      </property>
   </bean>

	 */
	
	
	/**
	 * This is a simple way to manage the list of activities. In production we
	 * will want to make it more dynamic.
	 * 
	 * @return
	 */
	public Map<String, String> getActivities() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("ImportZipActivity", "1.0");
		map.put("GenerateThumbnailActivity", "1.0");
		map.put("ExtractTextActivity", "1.0");
		map.put("ApproveContainerActivity", "1.0");
		return null;
	}
	

	public String getDomain() {
		// TODO: For testing we can dynamically create a domain
		if (domain == null)
			domain = java.util.UUID.randomUUID().toString();
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getWorkflowType() {
		return workflowType;
	}

	public void setWorkflowType(String workflowType) {
		this.workflowType = workflowType;
	}

	public String getWorkflowVersion() {
		return workflowVersion;
	}

	public void setWorkflowVersion(String workflowVersion) {
		this.workflowVersion = workflowVersion;
	}

	public String getActivityWorkerTaskListToPoll() {
		return activityWorkerTaskListToPoll;
	}

	public void setActivityWorkerTaskListToPoll(String activityWorkerTaskListToPoll) {
		this.activityWorkerTaskListToPoll = activityWorkerTaskListToPoll;
	}

	public String getDeciderTaskList() {
		return deciderTaskList;
	}

	public void setDeciderTaskList(String deciderTaskList) {
		this.deciderTaskList = deciderTaskList;
	}

	public String getSwfAccessId() {
		return swfAccessId;
	}

	public void setSwfAccessId(String swfAccessId) {
		this.swfAccessId = swfAccessId;
	}

	public String getSwfSecretKey() {
		return swfSecretKey;
	}

	public void setSwfSecretKey(String swfSecretKey) {
		this.swfSecretKey = swfSecretKey;
	}

	public String getS3AccessId() {
		return s3AccessId;
	}

	public void setS3AccessId(String s3AccessId) {
		this.s3AccessId = s3AccessId;
	}

	public String getS3SecretKey() {
		return s3SecretKey;
	}

	public void setS3SecretKey(String s3SecretKey) {
		this.s3SecretKey = s3SecretKey;
	}

	public String getIarchivesRestApiUrl() {
		return iarchivesRestApiUrl;
	}

	public void setIarchivesRestApiUrl(String iarchivesRestApiUrl) {
		this.iarchivesRestApiUrl = iarchivesRestApiUrl;
	}

	public String getPdfTestProjectGuid() {
		return pdfTestProjectGuid;
	}

	public void setPdfTestProjectGuid(String pdfTestProjectGuid) {
		this.pdfTestProjectGuid = pdfTestProjectGuid;
	}

}
