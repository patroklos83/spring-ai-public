package com.patroclos.ai.config;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.postgresml.PostgresMlEmbeddingClient;
import org.springframework.ai.postgresml.PostgresMlEmbeddingClient.VectorType;
import org.springframework.ai.postgresml.PostgresMlEmbeddingOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/***
  docker run \
    -it \
    -v postgresml_data:/var/lib/postgresql \
    -p 5433:5432 \
    -p 8000:8000 \
    ghcr.io/postgresml/postgresml:2.7.13 \
    sudo -u postgresml psql -d postgresml

    CREATE EXTENSION IF NOT EXISTS pgml;
    SELECT pgml.version();

    https://postgresml.org/docs/resources/developer-docs/quick-start-with-docker
 */
@Configuration
public class AppConfiguration {

	@Autowired
	private DataSource dataSource;

	@Bean("embeddingClient")
	PostgresMlEmbeddingClient embeddingClient() {
		PostgresMlEmbeddingClient embeddingClient = new PostgresMlEmbeddingClient(
				new JdbcTemplate(dataSource),
				PostgresMlEmbeddingOptions.builder()
				.withTransformer("distilbert-base-uncased") // huggingface transformer model name.
				.withVectorType(VectorType.PG_VECTOR) //vector type in PostgreSQL.
				.withKwargs(Map.of("device", "cpu")) // optional arguments.
				.withMetadataMode(MetadataMode.EMBED) // Document metadata mode.
				.build());

		return embeddingClient;
	}

	@Bean(name = "taskExecutor")
	AsyncTaskExecutor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(15);
		executor.setQueueCapacity(50);
		return executor;
	}


}
