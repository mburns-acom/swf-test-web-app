package com.iarchives.swf.dto;

import java.util.Calendar;

public class Project {

	private Long id;
    private String guid;
    private String name;
    private Calendar createDate;
    private Calendar lastUpdatedDate;
    
    
    public Project() {
		super();
		// TODO Auto-generated constructor stub
	}
    
	public Project(Long id, String guid, String name, Calendar createDate,
			Calendar lastUpdatedDate) {
		super();
		this.id = id;
		this.guid = guid;
		this.name = name;
		this.createDate = createDate;
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public Long getId() {
    	return id;
    }
    
    public void setId(Long id) {
    	this.id = id;
    }
    
	public String getGuid() {
		return guid;
	}
	
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Calendar getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Calendar createDate) {
		this.createDate = createDate;
	}
	
	public Calendar getLastUpdatedDate() {
		return lastUpdatedDate;
	}
	
	public void setLastUpdatedDate(Calendar lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}
    
}
