package com.iarchives.swf.dto;

import java.util.Calendar;

public class Container {

	private Long id;
	private Long parentId;
	private Long projectId;
	private String name;
	private short containerType;
	private String guid;
	private String srcguid;
    private Calendar createDate;
    private Calendar lastUpdatedDate;
    private String bucket;
    
    public Container() {
    	super();
    }
    
	public Container(Long id, Long parentId, Long projectId, String name,
			short containerType, String guid, String srcguid,
			Calendar createDate, Calendar lastUpdatedDate, String bucket) {
		super();
		this.parentId = parentId;
		this.projectId = projectId;
		this.name = name;
		this.containerType = containerType;
		this.guid = guid;
		this.srcguid = srcguid;
		this.createDate = createDate;
		this.lastUpdatedDate = lastUpdatedDate;
		this.bucket = bucket;
	}

    public Long getId() {
    	return id;
    }
    
    public void setId(Long id) {
    	this.id = id;
    }
    
	public Long getParentId() {
		return parentId;
	}
	
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	public Long getProjectId() {
		return projectId;
	}
	
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public short getContainerType() {
		return containerType;
	}
	
	public void setContainerType(short containerType) {
		this.containerType = containerType;
	}
	
	public String getGuid() {
		return guid;
	}
	
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	public String getSrcguid() {
		return srcguid;
	}
	
	public void setSrcguid(String srcguid) {
		this.srcguid = srcguid;
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

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

}
