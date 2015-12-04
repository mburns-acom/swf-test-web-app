package com.iarchives.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

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

    @RequestMapping(value = {"/projects"}, method = RequestMethod.GET)
    public ModelAndView projectsHome(@PageableDefault(page = 0, size = 25) final Pageable pageable) {
        logger.debug("Home requested.");
        
        Page<Project> projects = projectRepository.findAll(pageable);
        
        return new ModelAndView(HOME_VIEW_NAME, "page", projects);
    }

    @RequestMapping(value = "/project/{id}", method = RequestMethod.GET)
    public ModelAndView project(@PathVariable("id") Long id, HttpServletRequest req) {
    	
    	Project project = projectRepository.findOne(id);
    	List<Container> containers = containerRepository.findByProject(project);
    	req.setAttribute("containers", containers);
    	
        return new ModelAndView(PROJECT_VIEW_NAME, "project", project);
    }
}
