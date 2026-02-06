package it.bibs.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
  static {
    io.swagger.v3.core.jackson.ModelResolver.enumsAsRef = true;
  }

  @Bean
  public OpenAPI openApiSpec() {
    return new OpenAPI()
        .info(
            new Info().title("BIBS API").version("0.0.1").description("API documentation for BIBS"))
        .addSecurityItem(new SecurityRequirement().addList("oauth2"))
        .components(
            new Components()
                .addSecuritySchemes(
                    "oauth2",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .flows(
                            new OAuthFlows()
                                .authorizationCode(
                                    new OAuthFlow()
                                        .authorizationUrl(
                                            "http://localhost:8085/realms/bibs/protocol/openid-connect/auth?prompt=login")
                                        .tokenUrl(
                                            "http://localhost:8085/realms/bibs/protocol/openid-connect/token")
                                        .scopes(
                                            new Scopes()
                                                .addString("openid", "openid")
                                                .addString("profile", "profile")
                                                .addString("email", "email")))))
                .addSchemas(
                    "ApiErrorResponse",
                    new ObjectSchema()
                        .addProperty("status", new IntegerSchema())
                        .addProperty("code", new StringSchema())
                        .addProperty("message", new StringSchema())
                        .addProperty(
                            "fieldErrors",
                            new ArraySchema()
                                .items(new Schema<ArraySchema>().$ref("ApiFieldError"))))
                .addSchemas(
                    "ApiFieldError",
                    new ObjectSchema()
                        .addProperty("code", new StringSchema())
                        .addProperty("message", new StringSchema())
                        .addProperty("property", new StringSchema())
                        .addProperty("rejectedValue", new ObjectSchema())
                        .addProperty("path", new StringSchema())));
  }

  @Bean
  public OperationCustomizer operationCustomizer() {
    // add error type to each operation
    return (operation, handlerMethod) -> {
      operation
          .getResponses()
          .addApiResponse(
              "4xx/5xx",
              new ApiResponse()
                  .description("Error")
                  .content(
                      new Content()
                          .addMediaType(
                              "*/*",
                              new MediaType()
                                  .schema(new Schema<MediaType>().$ref("ApiErrorResponse")))));
      return operation;
    };
  }

  @Bean
  public OpenApiCustomizer schemaCustomizer() {
    return openApi -> {
      // Sort schemas alphabetically
      if (openApi.getComponents() != null && openApi.getComponents().getSchemas() != null) {
        Components components = openApi.getComponents();
        components.setSchemas(
            components.getSchemas().entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .collect(
                    java.util.stream.Collectors.toMap(
                        java.util.Map.Entry::getKey,
                        java.util.Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        java.util.LinkedHashMap::new)));
      }
    };
  }
}
