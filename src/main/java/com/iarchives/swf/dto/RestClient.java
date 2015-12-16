package com.iarchives.swf.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
@PropertySource({"classpath:application.properties"})
public class RestClient {

	@Value("${iarchives.rest.api.url}")
	private String iarchivesRestApiUrl;
	
	@Value("${iarchives.rest.api.port}")
	private int iarchivesRestApiPort;
	
	private RestTemplate restTemplate() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new Jackson2HalModule());

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
		converter.setObjectMapper(mapper);
		return new RestTemplate(Collections.<HttpMessageConverter<?>> singletonList(converter));
	}

	public Project createProject(Project project) {

		String url = iarchivesRestApiUrl.concat("/projects");
		ResponseEntity<Project> response = restTemplate().postForEntity(url, project, Project.class);
		Project newOne = response.getBody();
		
		return newOne;
	}

	public List<Project> getProjects() throws Exception {
		
		String url = iarchivesRestApiUrl.concat("/projects");
		
		ResponseEntity<PagedResources<Project>> responseEntity = restTemplate()
				.exchange(
						url,
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<PagedResources<Project>>() {
						}, iarchivesRestApiPort, 0, 100);
		PagedResources<Project> resources = responseEntity.getBody();
		List<Project> projects = new ArrayList<Project>(resources.getContent());
		
		return projects;
	}
	
	public Project getProjectByName(String name) {
		
		String url = iarchivesRestApiUrl.concat("/projects/search/findByName?name=");
		url = url.concat(name);
		
		ResponseEntity<PagedResources<Project>> responseEntity = restTemplate()
				.exchange(
						url,
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<PagedResources<Project>>() {
						}, iarchivesRestApiPort, 0, 100);
		PagedResources<Project> resources = responseEntity.getBody();
		List<Project> projects = new ArrayList<Project>(resources.getContent());
		if (!projects.isEmpty()) {
			return projects.get(0);
		}

		return null;
	}
	
	public Project getProjectByGuid(String guid) {
		
		String url = iarchivesRestApiUrl.concat("/projects/search/findByGuid?guid=");
		url = url.concat(guid);
		
		ResponseEntity<PagedResources<Project>> responseEntity = restTemplate()
				.exchange(
						url,
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<PagedResources<Project>>() {
						}, iarchivesRestApiPort, 0, 100);
		PagedResources<Project> resources = responseEntity.getBody();
		List<Project> projects = new ArrayList<Project>(resources.getContent());
		if (!projects.isEmpty()) {
			return projects.get(0);
		}

		return null;
	}
	
	public Container getProjectRoot(Long projectId) {
		
		String query = String.format("/containers/search/findByProjectIdAndParentId?projectId=%d&parentId=%d", projectId, -1);
		String url = iarchivesRestApiUrl.concat(query);
		
		ResponseEntity<PagedResources<Container>> responseEntity = restTemplate()
				.exchange(
						url,
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<PagedResources<Container>>() {
						}, iarchivesRestApiPort, 0, 100);
		PagedResources<Container> resources = responseEntity.getBody();
		List<Container> containers = new ArrayList<Container>(resources.getContent());
		if (!containers.isEmpty()) {
			return containers.get(0);
		}
		
		return null;
	}
	
	public Container createContainer(Container container) {

		String url = iarchivesRestApiUrl.concat("/containers");
		ResponseEntity<Container> response = restTemplate().postForEntity(url, container, Container.class);
		Container newOne = response.getBody();
		
		return newOne;
	}

	public Container getContainerByGuid(String guid) {
		
		String url = iarchivesRestApiUrl.concat("/containers/search/findByGuid?guid=");
		url = url.concat(guid);
		
		ResponseEntity<PagedResources<Container>> responseEntity = restTemplate()
				.exchange(
						url,
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<PagedResources<Container>>() {
						}, iarchivesRestApiPort, 0, 100);
		PagedResources<Container> resources = responseEntity.getBody();
		List<Container> containers = new ArrayList<Container>(resources.getContent());
		if (!containers.isEmpty()) {
			return containers.get(0);
		}

		return null;
	}

	public Container getContainer(Long id) {
		
		String url = iarchivesRestApiUrl.concat("/containers/");
		url = url.concat(id.toString());
		
		ResponseEntity<PagedResources<Container>> responseEntity = restTemplate()
				.exchange(
						url,
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<PagedResources<Container>>() {
						}, iarchivesRestApiPort, 0, 100);
		PagedResources<Container> resources = responseEntity.getBody();
		List<Container> containers = new ArrayList<Container>(resources.getContent());
		if (!containers.isEmpty()) {
			return containers.get(0);
		}

		return null;
	}

	public Image createImage(Image image) {

		String url = iarchivesRestApiUrl.concat("/images");
		ResponseEntity<Image> response = restTemplate().postForEntity(url, image, Image.class);
		Image newOne = response.getBody();
		
		return newOne;
	}
	
	public List<Image> getImagesForContainer(Long containerId) {
		
		String url = iarchivesRestApiUrl.concat("/images/search/findByContainerId?containerId=");
		url = url.concat(containerId.toString());
		
		ResponseEntity<PagedResources<Image>> responseEntity = restTemplate()
				.exchange(
						url,
						HttpMethod.GET,
						null,
						new ParameterizedTypeReference<PagedResources<Image>>() {
						}, iarchivesRestApiPort, 0, 100);
		PagedResources<Image> resources = responseEntity.getBody();
		List<Image> images = new ArrayList<Image>(resources.getContent());
		
		return images;
	}

	public QaSession createQaSession(QaSession qa) {

		String url = iarchivesRestApiUrl.concat("/qasessions");
		ResponseEntity<QaSession> response = restTemplate().postForEntity(url, qa, QaSession.class);
		QaSession newOne = response.getBody();
		
		return newOne;
	}
	
}
