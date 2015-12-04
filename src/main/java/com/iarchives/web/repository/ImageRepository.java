package com.iarchives.web.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.iarchives.web.domain.Image;

@RepositoryRestResource(collectionResourceRel = "images", path = "images")
public interface ImageRepository extends PagingAndSortingRepository<Image, Long> {
	
	// TODO: For simplicity this does not require paging but should for production
	List<Image> findByContainerId(@Param("containerId") Long containerId);
	//Page<Image> findByContainerid(@Param("containerId") Integer containerId, Pageable pageable);

}
