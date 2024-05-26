package com.patroclos.ai.ollama;

import java.io.IOException;
import java.util.List;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

@Service
public class LlamaAiServiceImpl implements LlamaAiService {

	private final OllamaChatClient chatClient;

	public LlamaAiServiceImpl(OllamaChatClient chatClient) {
		this.chatClient = chatClient;
	}

	public LlamaResponse generateMessage(String promptMessage) throws IOException {
		return generateMessage(promptMessage, null);
	}

	@Override
	public LlamaResponse generateMessage(String promptMessage, List<Media> media) throws IOException {
		String llamaMessage = null;
		
		if (media == null) {
		    llamaMessage = chatClient.call(promptMessage);
		}
		else {
			var userMessage = new UserMessage(promptMessage, media);
			ChatResponse response = chatClient.call(new Prompt(List.of(userMessage), OllamaOptions.create().withModel("mistral")));
			llamaMessage = response.getResult().getOutput().getContent();
		}


		LlamaResponse resp = new LlamaResponse();
		resp.setMessage(llamaMessage);
		return resp;
	}
}