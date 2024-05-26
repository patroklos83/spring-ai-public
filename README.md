
  

# Spring Boot and A.I Integration incl. PostgresML / Ollama Large Language Models (LLMs)

  

User Interface for chatting with AI Ollama models and creating embeddings of text
  

![enter image description here](/img/Screenshot1.png)

  

## PostgresML

  

To run a Postgres Vector database run the following docker container

    docker run 
    -it \
    -v postgresml_data:/var/lib/postgresql \  
    -p 5433:5432 \
    -p 8000:8000 \  
    ghcr.io/postgresml/postgresml:2.7.13 \  
    sudo -u postgresml  psql -d postgresml


*Make sure to run this command to create the pgml extention*

    CREATE EXTENSION IF NOT EXISTS pgml;
    
    SELECT pgml.version();

  

https://postgresml.org/docs/resources/developer-docs/quick-start-with-docker

  

## Requirements

 
For building and running the application you need:

- JDK 22 or newer

- [Eclipse IDE or other Java compatible IDE](https://www.eclipse.org/ide/)
[enter link description here](https://docs.spring.io/spring-ai/reference/api/clients/ollama-chat.html)
- [Docker 4.x.x or newer ](https://www.docker.com/products/docker-desktop/)
  

## Useful information


- [Spring Boot 3 Framework](https://docs.spring.io/spring-ai/reference/api/clients/ollama-chat.html)

- [Ollama](https://ollama.com/)

- [PostgresML](https://postgresml.org/)

  
## Running the app

Step 1. Run Docker Compose file

*from the root directory run command*

docker compose up
  
Go to http://localhost:8080/ai/