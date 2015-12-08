package com.iarchives.web.controller;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.iarchives.swf.direct.workflow.SwfUtils;
import com.iarchives.web.bean.ZipUploadFormBean;
import com.iarchives.web.domain.Container;
import com.iarchives.web.domain.Project;
import com.iarchives.web.repository.ContainerRepository;
import com.iarchives.web.repository.ProjectRepository;

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
	private SwfUtils swfUtils;

    @Value("${pdf.testing.bucket.name}")
    private String bucketName;

    @RequestMapping(value = {"/projects"}, method = RequestMethod.GET)
    public ModelAndView projectsHome(@PageableDefault(page = 0, size = 25) final Pageable pageable) {
        logger.debug("Home requested.");
        
        Page<Project> projects = projectRepository.findAll(pageable);
        
        return new ModelAndView(HOME_VIEW_NAME, "page", projects);
    }

    @RequestMapping(value = "/projects/{id}", method = RequestMethod.GET)
    public ModelAndView project(@PathVariable("id") Long id, HttpServletRequest req) {
    	
    	Project project = projectRepository.findOne(id);
    	List<Container> containers = containerRepository.findByProject(project);
    	req.setAttribute("containers", containers);
    	
        return new ModelAndView(PROJECT_VIEW_NAME, "project", project);
    }
    
	@RequestMapping(value = "/projects/{guid}/uploadZipFile", method = RequestMethod.POST)
	public ModelAndView uploadCsv(
			@ModelAttribute("zipUpload") ZipUploadFormBean upload, Errors errors,
			HttpServletRequest req, @PathVariable("guid") String guid) {

		Project project = projectRepository.findByGuid(guid);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		
		String msg = "";
		
		// handle the upload
		InputStream is = null;
		try {
			MultipartFile mpFile = upload.getFile();
			String origName = mpFile.getOriginalFilename();
			String key = origName.replaceAll("[ ]", "_").replace(".zip", "")
					+ "-" + sdf.format(new Date()) + ".zip";
			is = mpFile.getInputStream();
			AmazonS3 s3 = new AmazonS3Client();
			TransferManager tm = new TransferManager(s3);
			tm.upload(bucketName, key, is, null);
			swfUtils.registerTypes();
			swfUtils.startWorkflow(bucketName, key, "0");

			msg = "Successfully uploaded file and started workflow.";
		} catch (Exception ex) {
			logger.error("Severe error while uploading zip file", ex);
			msg = ex.toString();
		}
		
		req.setAttribute("message", msg);
		
        return new ModelAndView(PROJECT_VIEW_NAME, "project", project);
	}
    
    
}
