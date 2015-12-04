package com.iarchives.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.iarchives.web.repository.ProjectRepository;

@Controller
public class HomeController {

    @Autowired
    private ProjectRepository projectRepository;
    
    @RequestMapping(value = {"/", "/home"}, method=RequestMethod.GET)
    public String home() {
        return "redirect:/projects";
    }
}
