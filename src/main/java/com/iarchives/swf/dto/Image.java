package com.iarchives.swf.dto;

import java.util.Calendar;

public class Image {

	private Long id;
	private Long containerId;
	private String name;
	private String guid;
	private String url;
    private Calendar createDate;
    private Calendar lastUpdatedDate;
    
	public Image() {
		super();
	}
	
	public Image(Long id, Long containerId, String name, String guid,
			String url, Calendar createDate, Calendar lastUpdatedDate) {
		super();
		this.id = id;
		this.containerId = containerId;
		this.name = name;
		this.guid = guid;
		this.url = url;
		this.createDate = createDate;
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getContainerId() {
		return containerId;
	}

	public void setContainerId(Long containerId) {
		this.containerId = containerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
