package com.patroclos.ai.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.patroclos.ai.dto.Message;
import com.patroclos.ai.repository.UploadedFileRepository;
import com.patroclos.ai.service.EmbeddingService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("ai")
public class UserInterfaceController {

	private final Logger log = org.slf4j.LoggerFactory.getLogger(UserInterfaceController.class);

	private final UploadedFileRepository uploadedFileRepository;
	private final EmbeddingService embeddingService;

	@Autowired 
	private VectorStore vectorStore;

	public UserInterfaceController(
			UploadedFileRepository uploadedFileRepository,
			EmbeddingService embeddingService) {

		this.uploadedFileRepository = uploadedFileRepository;
		this.embeddingService = embeddingService;
	}

	@GetMapping("/")
	public ModelAndView index() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("index");
		return mav;
	}

	@GetMapping("/embedding")
	public ModelAndView embed(@RequestParam(value = "search", defaultValue = "") String message) throws IOException {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("index");

		if (message == null || message.isBlank())
			return mav;

		SearchRequest req = SearchRequest.query(message);
		List<Document> documents = vectorStore.similaritySearch(req);

		if (documents != null)
			mav.addObject("messages", documents.stream().map(d -> 
			{
				Message m = new Message();
				m.setId(UUID.randomUUID().toString());
				m.setContent(d.getContent());
				m.setDistance(1f - (Float)d.getMetadata().get("distance"));
				m.setFilename((String)d.getMetadata().getOrDefault("filename", ""));
				return m;
			}
					)
					.toList());
		else
			mav.addObject("message", new ArrayList<Message>());

		return mav;
	}

	@PostMapping(path = "/embedding")
	public ModelAndView addEmbed(@RequestParam(value = "document", defaultValue = "") String document) throws NoSuchAlgorithmException {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("index");
		embeddingService.saveDocument(document);
		return mav;
	}

	@PostMapping("/upload") 
	public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file ) {
		String fileName = file.getOriginalFilename();
		try {
			if (uploadedFileRepository.findByFilename(fileName).isPresent()) {
				return ResponseEntity.ok("File already uploaded.");
			}

			InputStream initialStream = file.getInputStream();
			byte[] buffer = new byte[initialStream.available()];
			initialStream.read(buffer);

			File targetFile = new File("src/main/resources/static/files/" + fileName);

			try (FileOutputStream outStream = new FileOutputStream(targetFile)) {
				outStream.write(buffer);
			}
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} 
		return ResponseEntity.ok(String.format("%s", fileName));
	}

	@GetMapping("/downloadFile/{filename}")
	public ResponseEntity<?> downloadFile(@PathVariable("filename") String filename) {
		Resource resource = null;
		try {
			resource = getFileAsResource(filename);
		} catch (IOException e) {
			return ResponseEntity.internalServerError().build();
		}

		if (resource == null) {
			return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
		}

		String contentType = "application/octet-stream";
		String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
				.body(resource);       
	}

	private Resource getFileAsResource(String filename) throws IOException {
		UrlResource foundFileResource = null;
		Path dirPath = Paths.get("src", "main", "resources", "static", "files");

		Optional<Path> file = Files
				.list(dirPath)
				.filter(f -> f.getFileName().toString().equals(filename))
				.distinct().findFirst();


		if (file.isPresent()) {
			foundFileResource = new UrlResource(file.get().toAbsolutePath().normalize().toUri());
		}

		return foundFileResource;
	}

	@GetMapping("/file/{filename}")
	public void download(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("filename") String filename) throws IOException {

		log.info("Inside the download controller," +
				" resource fileName =" + filename);
		
		Resource resource = getFileAsResource(filename);
		if (resource.exists()) {
			log.info("Resource exists!");
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", 
					String.format("attachment; filename=" + 
							resource.getFilename()));
			response.setContentLength((int) resource.contentLength());
			InputStream inputStream = resource.getInputStream();

			FileCopyUtils.copy(inputStream, response.getOutputStream());
		}
	}

}
