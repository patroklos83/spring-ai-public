package com.patroclos.ai.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {
	
	private final Logger log = org.slf4j.LoggerFactory.getLogger(EmbeddingService.class);
	private final Double SIMILARITY_THRESHOLD = 0.4;
	
	@Autowired 
	private VectorStore vectorStore;

	
	public void saveDocument(String document) throws NoSuchAlgorithmException {
		saveDocument(document, new HashMap<String, Object>());
	}
	
	public void saveDocument(String document, Map<String, Object> metadata) throws NoSuchAlgorithmException {
		
		if (document != null && !document.isEmpty()) {
			document = document.trim();
			final String hash = hashOfString(document);
			List<Document> existingDocuments = findExistingDocuments(document);
			if (existingDocuments != null) {
				Optional<Document> existingMatch = existingDocuments
						.stream()
						.filter(d -> d.getMetadata().getOrDefault("hash", "").equals(hash))
						.findFirst(); 

				existingMatch.ifPresent(d -> {
				    log.info("found similar existing document");
				    vectorStore.delete(List.of(d.getId()));
				    log.info("deleted similar existing document");
				});
			}
			
			metadata.put("hash", hash);
			List<Document> documents = List.of(new Document(document, metadata));
			vectorStore.add(documents);
			log.info("document saved");
		}
	}
	
	public List<Document> findExistingDocuments(String document) throws NoSuchAlgorithmException {
		FilterExpressionBuilder b = new FilterExpressionBuilder();

		List<Document> existingDocuments = vectorStore.similaritySearch(SearchRequest.defaults()
		    .withFilterExpression("hash == '" + hashOfString(document) + "'"));
		
		return existingDocuments;
	}
	
	private String hashOfString(String text) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] encodedhash = digest.digest(text.getBytes(StandardCharsets.UTF_8));

		StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
		for (int i = 0; i < encodedhash.length; i++) {
			String hex = Integer.toHexString(0xff & encodedhash[i]);
			if(hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		
		return hexString.toString();
	}

}
