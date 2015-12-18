package com.iarchives.web.controller;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.iarchives.swf.direct.workflow.SwfUtils;
import com.iarchives.web.bean.ZipUploadFormBean;
import com.iarchives.web.domain.Container;
import com.iarchives.web.domain.Project;
import com.iarchives.web.domain.QaSession;
import com.iarchives.web.repository.ContainerRepository;
import com.iarchives.web.repository.ProjectRepository;
import com.iarchives.web.repository.QaSessionRepository;

@Controller
public class ProjectController {
	
    /** Logger implementation. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public static final String HOME_VIEW_NAME = "site.homepage";
    public static final String PROJECT_VIEW_NAME = "site.project";
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private ContainerRepository containerRepository;
    
    @Autowired
    private QaSessionRepository qaSessionRepository;
    
	@Autowired
	private SwfUtils swfUtils;

    @Value("${pdf.testing.bucket.name}")
    private String bucketName;

	@Value("${s3.access.id}")
	private String s3AccessId;
	
	@Value("${s3.secret.key}")
	private String s3SecretKey;
	
    /**
     * Display the list of projects in the repo.
     * 
     * @param pageable
     * @return
     */
    @RequestMapping(value = {"/projects"}, method = RequestMethod.GET)
    public ModelAndView projectsHome(@PageableDefault(page = 0, size = 25) final Pageable pageable) {
        logger.debug("Home requested.");
        
        Page<Project> projects = projectRepository.findAll(pageable);
        
        return new ModelAndView(HOME_VIEW_NAME, "page", projects);
    }

    /**
     * Display the project details page.
     * 
     * @param id
     * @param req
     * @return
     */
    @RequestMapping(value = "/projects/{id}", method = RequestMethod.GET)
    public ModelAndView projectDetails(@PathVariable("id") Long id, HttpServletRequest req) {
    	
    	Project project = projectRepository.findOne(id);
    	List<Container> containers = containerRepository.findByProjectId(project.getId());
    	req.setAttribute("containers", containers);
    	List<QaSession> work = new ArrayList<QaSession>();
    	for (Container cont : containers) {
    		work.addAll(qaSessionRepository.findByContainerId(cont.getId()));
    	}
    	req.setAttribute("work", work);
    	
        return new ModelAndView(PROJECT_VIEW_NAME, "project", project);
    }
    
    /**
     * AJAX method to simply approve the qa session.
     * 
     * @param id - id of the QaSession
     * @param req
     */
    @RequestMapping(value = "/projects/quickapproval/{id}", method = RequestMethod.GET)
    public void ajaxQuickApproval(@PathVariable("id") Long id, HttpServletResponse response) {
    	
		String msg = null;
    	String outcome = "approved";
    	
    	try {
			// Save the change to our database
			QaSession qa = qaSessionRepository.findOne(id);
			qa.setLastUpdatedDate(Calendar.getInstance());
			qa.setStatus("completed");
			qa.setResult(outcome);
			qa.setReason("Quick Approval");
			qaSessionRepository.save(qa);
			
			// Send the message to SWF
			logger.info("Task Token: " + qa.getTaskToken());
			logger.info("Outcome: " + outcome);
			swfUtils.completeWorkflow(qa.getTaskToken(), outcome);
			logger.info("COMPLETED Workflow");
			
			msg = "<span class='success'>Success: Approved</span>";
		} catch (Exception e) {
			msg = "<span class='error'>Error: " + e.toString() + "</span>";
			logger.error("Error in ajaxQuickApproval", e);
		}
		
		// Write to the response
		try {
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().print(msg);
			response.flushBuffer();
		} catch (Exception e) {
			logger.error("Exception in ajaxQuickApproval: " + e.toString(), e);
		}
    }
    
    /**
     * AJAX method to simply reject the qa session.
     * 
     * @param id - id of the QaSession
     * @param req
     */
    @RequestMapping(value = "/projects/quickfail/{id}", method = RequestMethod.GET)
    public void ajaxQuickFail(@PathVariable("id") Long id, HttpServletResponse response) {
    	
		String msg = null;
    	String outcome = "failed";
    	
    	try {
			// Save the change to our database
			QaSession qa = qaSessionRepository.findOne(id);
			qa.setLastUpdatedDate(Calendar.getInstance());
			qa.setStatus("completed");
			qa.setResult(outcome);
			qa.setReason("Quick Fail");
			qaSessionRepository.save(qa);
			
			// Send the message to SWF
			logger.info("Task Token: " + qa.getTaskToken());
			logger.info("Outcome: " + outcome);
			swfUtils.completeWorkflow(qa.getTaskToken(), outcome);
			logger.info("COMPLETED Workflow");
			
			msg = "<span class='success'>Success: Approved</span>";
		} catch (Exception e) {
			msg = "<span class='error'>Error: " + e.toString() + "</span>";
			logger.error("Error in ajaxQuickFail", e);
		}
		
		// Write to the response
		try {
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().print(msg);
			response.flushBuffer();
		} catch (Exception e) {
			logger.error("Exception in ajaxQuickFail: " + e.toString(), e);
		}
    }
    
	/**
	 * Handle the upload of a zip file.
	 * 
	 * @param upload
	 * @param errors
	 * @param req
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/projects/{id}/uploadZipFile", method = RequestMethod.POST)
	public String uploadZipFile(
			@ModelAttribute("zipUpload") ZipUploadFormBean upload, Errors errors,
			HttpServletRequest req, @PathVariable("id") Long id) {

		Project project = projectRepository.findOne(id);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss.SSS");
		
		String msg = "";
		
		// handle the upload
		InputStream is = null;
		try {
			MultipartFile mpFile = upload.getFile();
			String origName = mpFile.getOriginalFilename();
			String key = origName.replaceAll("[ ]", "_").replace(".zip", "")
					+ "-" + sdf.format(new Date()) + ".zip";
			is = mpFile.getInputStream();
			AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(s3AccessId, s3SecretKey));
			TransferManager tm = new TransferManager(s3);
			tm.upload(bucketName, key, is, null).waitForCompletion();
			swfUtils.registerTypes();
			swfUtils.startWorkflow(bucketName, key, "0");

			msg = "Successfully uploaded file and started workflow.";
		} catch (Exception ex) {
			logger.error("Severe error while uploading zip file", ex);
			msg = ex.toString();
		}
		
		req.setAttribute("message", msg);
		
        return "redirect:/projects/" + project.getId();
	}
}
