package com.patroclos.ai.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name="uploaded_files")
public class UploadedFile {
	
	@Id
	@GeneratedValue(
	    strategy = GenerationType.SEQUENCE,
	    generator = "uploaded_files_id_seq"
	)
	@SequenceGenerator(
	    name = "uploaded_files_id_seq",
	    allocationSize = 1
	)
	private Long id;
	private String filename;
	private String path;
	private String status;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
