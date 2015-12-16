package com.iarchives.web.domain;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class QaSession implements Serializable {

	private static final long serialVersionUID = 1057451368223444917L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private Long containerId;
	
	@Column(nullable = false, length = 400)
	private String taskToken;
	
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createDate;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar lastUpdatedDate;

	@Column(nullable = false)
	private String status;
	
	@Column(nullable = true)
	private String result;
	
	@Column(nullable = true)
	private String reason;
    
	@PrePersist
	void preInsert() {
		if (createDate == null) {
			createDate = Calendar.getInstance();
		}

		if (lastUpdatedDate == null) {
			lastUpdatedDate = Calendar.getInstance();
		}
	}
	
	public boolean isReady() {
		return ("ready".equals(status));
	}
	
	public boolean isSuccess() {
		return ("approved".equals(result));
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

	public String getTaskToken() {
		return taskToken;
	}

	public void setTaskToken(String taskToken) {
		this.taskToken = taskToken;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
}
