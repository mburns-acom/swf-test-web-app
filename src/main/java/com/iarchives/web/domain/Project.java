package com.iarchives.web.domain;

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
public class Project {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String guid;

    @Column(nullable = false, unique = true)
    private String name;

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

	public Project() {
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
