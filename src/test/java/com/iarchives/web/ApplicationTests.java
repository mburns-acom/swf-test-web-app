package com.iarchives.web;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.iarchives.swf.direct.workflow.JsonUtils;
import com.iarchives.swf.dto.Container;
import com.iarchives.swf.dto.Image;
import com.iarchives.swf.dto.Project;
import com.iarchives.swf.dto.QaSession;
import com.iarchives.swf.dto.RestClient;
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
						.toString(), "", now, now, null);
		childCont = restClient.createContainer(childCont);
		
		Assert.assertNotNull(childCont);
		Assert.assertEquals(childCont.getName(), "Child_Cont1");
		
		Image imageIn = new Image(null, childCont.getId(), "image_1234567890",
				UUID.randomUUID().toString(), "my_dummy_url", now, now);
		Image imageOut = restClient.createImage(imageIn);
		
		Assert.assertNotNull(imageOut);
		Assert.assertEquals(childCont.getId(), imageOut.getContainerId());
		
		QaSession qa = new QaSession();
		qa.setContainerId(rootCont.getId());
		qa.setTaskToken("AAAAKgAAAAIAAAAAAAAAAyqA42XKR/8GD25tKvsxY8m1n0OGIm9Y3FuLDiDwGJtYLnSo72uknTsq24rQkMF+xfWG/UNGeBK02MKWcZlNp2uvhajjhAd8LEdTcG1nzvi/R8Oe7Xor5jlsFTkh9YFZb8VexNOY+N+S2g1HYj6ICv5J3RM1SY0DFH/fdExWlShqco3A00hxkS99Ce2106Urra0kpCto95jRyHiOMANbXuJEvhifJnpwDZi6Enfy7fgpQVOW9goUMuhr8bm39Wi3Ke+HH+CJpMgc8r+LX3/hl+6W8dH2obJEynHtow29cQ8Fg5ll+QqJ6emqTZ1u+qj3zRNLeIvn54s5ZhJDn377PGo=");
		qa.setStatus("completed");
		qa.setResult("approved");
		qa.setReason("This is a test");
		QaSession qaResult = restClient.createQaSession(qa);
		
		Assert.assertEquals(qa.getContainerId(), qaResult.getContainerId());
		Assert.assertEquals(qa.getStatus(), qaResult.getStatus());
	}
	
	@Test
	public void testJsonUtils() {
		
		String expected = "approved";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("outcome", expected);
		String resultJson = JsonUtils.toString(resultMap);
		Map<String, Object> results = JsonUtils.fromString(resultJson);
		String actual = (String) results.get("outcome");
		
		Assert.assertEquals(expected, actual);
		
		resultJson = "{\r\n  \"outcome\" : \"approved\"\r\n}";
		results = JsonUtils.fromString(resultJson);
		actual = (String) results.get("outcome");
		
		Assert.assertEquals(expected, actual);
	}
	
//	@Test
//	public void testGSExec() throws Exception {
//		String dirStr = "C:\\Users\\mburns\\AppData\\Local\\Temp\\temp466854548845310392232161579387943.d";
//		File pdf = new File(dirStr, "PNI_Phoenix_AZ_20151102_1_Republic_A004_A_uscpcent02-6my4sbzi6zdvd3nr2wy.pdf");
//		File jpeg = new File(dirStr, "PNI_Phoenix_AZ_20151102_1_Republic_A004_A_uscpcent02-6my4sbzi6zdvd3nr2wy.jpg");
//
//		String gsPath = "gswin64c.exe";
//		Process p = new ProcessBuilder(gsPath, "-dNOPAUSE", "-dBATCH",
//				"-dFirstPage=1", "-dLastPage=1", "-sDEVICE=jpeg", "-g"
//						+ Integer.toString(300) + "x"
//						+ Integer.toString(300), "-dPDFFitPage=true",
//				"-sOutputFile=" + jpeg.getAbsolutePath(), pdf.getAbsolutePath())
//				.start();
//		p.waitFor();
//		
//		Assert.assertTrue(jpeg.exists());
//	}
}
