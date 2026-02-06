package it.bibs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class BibsServiceApplication {

  public static void main(final String[] args) {
    SpringApplication.run(BibsServiceApplication.class, args);
  }

  /**
   * Creates a CommandLineRunner that logs API documentation URLs when the application starts.
   *
   * @param serverProtocol the configured server protocol (http/https)
   * @param serverHost the configured server host
   * @param serverPort the configured server port
   * @param swaggerPath the configured Swagger UI path
   * @param apiDocsPath the configured OpenAPI documentation path
   * @return a CommandLineRunner that logs the URLs
   */
  @Bean
  public CommandLineRunner logApiDocumentationUrls(
      @Value("${server.protocol:http}") String serverProtocol,
      @Value("${server.host:localhost}") String serverHost,
      @Value("${server.port:8080}") String serverPort,
      @Value("${springdoc.swagger-ui.path:/swagger-ui}") String swaggerPath,
      @Value("${springdoc.api-docs.path:/api-docs}") String apiDocsPath) {
    return args -> {
      log.info("✨ API DOCUMENTATION LINKS ✨");
      String completeServerUrl = serverProtocol + "://" + serverHost + ":" + serverPort;
      String swaggerUrl = completeServerUrl + swaggerPath;
      String apiDocsUrl = completeServerUrl + apiDocsPath;
      log.info("🌐 Swagger UI: {}", swaggerUrl);
      log.info("📚 OpenAPI docs: {}", apiDocsUrl);
    };
  }
}
