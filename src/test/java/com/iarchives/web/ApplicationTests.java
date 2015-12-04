package com.iarchives.web;

import java.util.Calendar;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.iarchives.swf.service.Container;
import com.iarchives.swf.service.Image;
import com.iarchives.swf.service.Project;
import com.iarchives.swf.service.RestClient;
import com.iarchives.web.repository.ProjectRepository;
import com.iarchives.web.service.UserService;

/**
 * For testing to work as-is, out of the box, I had to introduce a configuration class
 * that replaces the Apache Tiles configurer with one that locates the configuration file
 * as a file system resource relative to the project root directory. Other than that, this
 * is the test created by Spring Source Tool Suite.
 * 
 * @author Mark Burns
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, TestConfigurtaion.class, RestClient.class})
//@WebAppConfiguration
@WebIntegrationTest
public class ApplicationTests {

	@Autowired
	UserService userService;

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	private RestClient restClient;

	@Test
	public void contextLoads() {
	}
	
//	@Test
//	public void testTheIssue() throws Exception {
//	    
//	    User user = userService.loadUserById(1L);
//	    int i = user.getNumberOfVisits();
//	    
//	    User userUpdated = userService.registerVisit(user);
//	    int j = userUpdated.getNumberOfVisits();
//	    
//	    Assert.assertEquals(i + 1,  j);
//	}
	
	@Test
	public void testRestClient() throws Exception {
		
		String guid = "ed3f7081-bfb4-4857-9b39-2f73657f67d1";
		Calendar now = Calendar.getInstance();
		Project project = restClient.getProjectByGuid(guid);		
		
		Assert.assertNotNull(project);
		Assert.assertEquals(guid, project.getGuid());
		
		Project newProjectIn = new Project(null, null,
				"MyTestProject", null, null);
		Project newProjectOut = restClient.createProject(newProjectIn);
		
		Assert.assertNotNull(newProjectOut);
		Assert.assertEquals(newProjectIn.getName(), newProjectOut.getName());
		
		Container rootCont = restClient.getProjectRoot(project.getId());
		
		Assert.assertNotNull(rootCont);
		Assert.assertEquals(new Long(-1L), rootCont.getParentId());
		
		Container childCont = new Container(null, rootCont.getId(),
				project.getId(), "Child_Cont1", (short) 0, UUID.randomUUID()
						.toString(), "", now, now);
		childCont = restClient.createContainer(childCont);
		
		Assert.assertNotNull(childCont);
		Assert.assertEquals(childCont.getName(), "Child_Cont1");
		
		Image imageIn = new Image(null, childCont.getId(), "image_1234567890",
				UUID.randomUUID().toString(), "my_dummy_url", now, now);
		Image imageOut = restClient.createImage(imageIn);
		
		Assert.assertNotNull(imageOut);
		Assert.assertEquals(childCont.getId(), imageOut.getContainerId());
	}
}
