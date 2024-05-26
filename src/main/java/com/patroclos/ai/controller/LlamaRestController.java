package com.patroclos.ai.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.ai.chat.messages.Media;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.patroclos.ai.ollama.LlamaAiService;
import com.patroclos.ai.ollama.LlamaResponse;

@RestController
@RequestMapping("api/v1/ai/")
public class LlamaRestController {
	
	private final Logger log = org.slf4j.LoggerFactory.getLogger(LlamaRestController.class);

	private final LlamaAiService llamaAiService;

	public LlamaRestController(LlamaAiService llamaAiService) {
		this.llamaAiService = llamaAiService;
	}
	
	@PostMapping("chat")
	public ResponseEntity<LlamaResponse> chat(
			@RequestParam(value = "text", defaultValue = "") String text) throws IOException {	
		if (text.isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		log.info("generating response for text ... {}", text);
		final LlamaResponse aiResponse = llamaAiService.generateMessage(text);
		log.info("got response for text {}", aiResponse.getMessage().toString());
		return ResponseEntity.status(HttpStatus.OK).body(aiResponse);
	}
	
	@PostMapping("summarize")
	public ResponseEntity<LlamaResponse> summarize(
			@RequestParam(value = "text", defaultValue = "") String text) throws IOException {	
		if (text.isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		text = String.format("please summarize this text: %s", text);
		log.info("generating response for text ... {}", text);
		final LlamaResponse aiResponse = llamaAiService.generateMessage(text);
		log.info("got response for text {}", aiResponse.getMessage().toString());
		return ResponseEntity.status(HttpStatus.OK).body(aiResponse);
	}

	@PostMapping("generate")
	public ResponseEntity<LlamaResponse> generate(
			@RequestParam(value = "promptMessage") String promptMessage,
			@RequestParam(value = "filename", defaultValue = "") String filename) throws IOException {
		
		if (promptMessage.isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		
		List<Media> media = null;
		LlamaResponse aiResponse = null;
		
		if (filename != null && filename.length() > 0) {
			if (!filename.toLowerCase().endsWith(".png")) {
				aiResponse = new LlamaResponse();
				aiResponse.setMessage("only .png files allowed");		
				return ResponseEntity.badRequest().body(aiResponse);
			}
			
			filename = filename.startsWith("=") ? filename.substring(1, filename.length()) : filename;
			byte[] fileData = new ClassPathResource("/static/files/" + filename).getContentAsByteArray();
			media = List.of(new Media(MimeTypeUtils.IMAGE_PNG, fileData));
			
			log.info("generating response for text [{}] and file [{}] ...", promptMessage, filename);
			aiResponse = llamaAiService.generateMessage(promptMessage, media);
		}
		else
		{
			log.info("generating response for text ... {}", promptMessage);
			aiResponse = llamaAiService.generateMessage(promptMessage);
		}
		log.info("got response for text {}", aiResponse.getMessage().toString());
		 
		return ResponseEntity.status(HttpStatus.OK).body(aiResponse);
	}

}