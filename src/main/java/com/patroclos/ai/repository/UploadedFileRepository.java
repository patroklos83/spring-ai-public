package com.patroclos.ai.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.patroclos.ai.model.*;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
	
	Optional<UploadedFile> findByFilename(String filename);

}
