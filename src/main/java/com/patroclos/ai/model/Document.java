package com.patroclos.ai.model;

public class Document {
	
	private long id;
	private String content;
	private float[] vector;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public float[] getVector() {
		return vector;
	}
	public void setVector(float[] vector) {
		this.vector = vector;
	}
}
