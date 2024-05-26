package com.patroclos.ai.dto;


public class Message{
	
	private String id;

	private String content;
    private Float distance;
    private String filename;

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public Float getDistance() {
		return distance;
	}

	public void setDistance(Float distance) {
		this.distance = distance;
	}
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}