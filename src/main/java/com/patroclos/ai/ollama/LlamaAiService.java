package com.patroclos.ai.ollama;

import java.io.IOException;
import java.util.List;

import org.springframework.ai.chat.messages.Media;

public interface LlamaAiService {

  LlamaResponse generateMessage(String prompt) throws IOException;
  LlamaResponse generateMessage(String promptMessage, List<Media> media) throws IOException;
  
}