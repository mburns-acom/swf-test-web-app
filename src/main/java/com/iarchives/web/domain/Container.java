package com.iarchives.web.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the container database table.
 * 
 */
@Entity
public class Container implements Serializable {

	private static final long serialVersionUID = -6974696525924492253L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private Long parentId;
	
	// many-to-one association to Project
//	@ManyToOne
//	@JoinColumn(name="project_id")
//	private Project project;
//	
//	@Column(name = "project_id", insertable=false, updatable=false)
//	private Long projectId;
	
	@Column(name = "project_id")
	private Long projectId;

	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private short containerType;

	@Column(nullable = false, length = 40)
	private String guid;
	
	@Column(nullable = true, length = 40)
	private String srcguid;
	
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createDate;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar lastUpdatedDate;
    
    @Column(nullable = true)
    private String bucket;

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

	protected Container() {
	}

	public Container(String name, String guid, String srcguid) {
		super();
		this.name = name;
		this.guid = guid;
		this.srcguid = srcguid;
	}

	public Container(String name, String guid, String srcguid, short containerType, Calendar createDate, Calendar lastUpdatedDate, String bucket) {
		super();
		this.name = name;
		this.guid = guid;
		this.srcguid = srcguid;
		this.containerType = containerType;
		this.createDate = createDate;
		this.lastUpdatedDate = lastUpdatedDate;
		this.bucket = bucket;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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

	@Override
	public String toString() {
		return getName() + "," + getGuid() + "," + getSrcguid();
	}
}
