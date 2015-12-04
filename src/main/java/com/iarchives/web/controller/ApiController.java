package com.iarchives.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iarchives.web.domain.Project;
import com.iarchives.web.repository.ContainerRepository;
import com.iarchives.web.repository.ProjectRepository;

/**
 * Normally we would break out the REST API as a separate app. In that case we
 * could use simple spring-data-jpa-rest and not need this controller. For this
 * demo app we have combined the REST API and web app together to simplify
 * testing.
 * 
 * @author mburns
 *
 */
//@Controller
//@RequestMapping(value = "/api")
public class ApiController {
	
    /** Logger implementation. */
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    
//    @Autowired
//    private ProjectRepository projectRepository;
//    
//    @Autowired
//    private ContainerRepository containerRepository;
//
//    @RequestMapping(value = {"/projects"}, method = RequestMethod.GET)
//    @ResponseBody
//    public List<Project> getProjects() {
//    	List<Project> projects = new ArrayList<Project>();
//    	
//    	for (Project project : projectRepository.findAll()) {
//    		projects.add(project);
//    	}
//        
//        return projects;
//    }
    
    // TODO: Add other API methods 
}
