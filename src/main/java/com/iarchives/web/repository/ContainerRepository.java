package com.iarchives.web.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.iarchives.web.domain.Container;
import com.iarchives.web.domain.Project;

@RepositoryRestResource(collectionResourceRel = "containers", path = "containers")
public interface ContainerRepository extends PagingAndSortingRepository<Container, Long> {
	
	List<Container> findByProject(@Param("project") Project project);
	List<Container> findByProjectIdAndParentId(@Param("projectId") Long projectId, @Param("parentId") Long parentId);

}
