package com.iarchives.web.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Image implements Serializable {

	private static final long serialVersionUID = 7865782187647017374L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private Long containerId;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false, length = 40)
	private String guid;
	
	@Column(nullable = true, length = 255)
	private String url;
	
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createDate;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar lastUpdatedDate;

	@PrePersist
	void preInsert() {
		if (guid == null) {
			guid = UUID.randomUUID().toString();
		}

		if (createDate == null) {
			createDate = Calendar.getInstance();
		}

		if (lastUpdatedDate == null) {
			lastUpdatedDate = Calendar.getInstance();
		}
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
