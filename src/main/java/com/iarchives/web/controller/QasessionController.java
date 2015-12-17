package com.iarchives.web.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.iarchives.swf.direct.workflow.SwfUtils;
import com.iarchives.web.domain.Container;
import com.iarchives.web.domain.Image;
import com.iarchives.web.domain.Project;
import com.iarchives.web.domain.QaSession;
import com.iarchives.web.repository.ContainerRepository;
import com.iarchives.web.repository.ImageRepository;
import com.iarchives.web.repository.ProjectRepository;
import com.iarchives.web.repository.QaSessionRepository;

@Controller
public class QasessionController {
	
    /** Logger implementation. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public static final String QA_VIEW_NAME = "site.qa";

    @Autowired
    private ContainerRepository containerRepository;
    
    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private QaSessionRepository qaSessionRepository;
    
	@Autowired
	private SwfUtils swfUtils;

    @RequestMapping(value = "/qasessions/{id}", method = RequestMethod.GET)
    public ModelAndView startQaSession(@PathVariable("id") Long id, HttpServletRequest req) {
    	
    	logger.info("About to get QaSession Page");
    	QaSession qa = qaSessionRepository.findOne(id);
    	req.setAttribute("qa", qa);
    	Container container = containerRepository.findOne(qa.getContainerId());
    	req.setAttribute("container", container);
    	Project project = projectRepository.findOne(container.getProjectId());
    	req.setAttribute("project", project);
    	List<Image> images = imageRepository.findByContainerId(container.getId());
    	
    	Map<Long, String> urls = new LinkedHashMap<Long, String>();
    	for (Image image : images) {
			String objectKey = image.getUrl().replace("/raw", "/thumb") + image.getGuid() + ".jpg";
    		urls.put(image.getId(), swfUtils.generateImageUrl(objectKey, container.getBucket()));
    	}
    	req.setAttribute("imageUrls", urls);
    	
        return new ModelAndView(QA_VIEW_NAME, "images", images);
    }
    
}
