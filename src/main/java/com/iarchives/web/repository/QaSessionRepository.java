package com.iarchives.web.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.iarchives.web.domain.QaSession;

@RepositoryRestResource(collectionResourceRel = "qasessions", path = "qasessions")
public interface QaSessionRepository extends PagingAndSortingRepository<QaSession, Long> {

	List<QaSession> findByContainerId(@Param("containerId") Long containerId);
}
