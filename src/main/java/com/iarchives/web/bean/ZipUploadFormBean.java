package com.iarchives.web.bean;

import org.springframework.web.multipart.MultipartFile;

public class ZipUploadFormBean {

	private MultipartFile file;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

}
