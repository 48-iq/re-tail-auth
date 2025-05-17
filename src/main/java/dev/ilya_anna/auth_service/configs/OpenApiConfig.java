package dev.ilya_anna.auth_service.configs;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
  info = @Info(
    title = "Auth Service API",
    version = "1.0",
    description = "API for Auth Service",
    contact = @Contact(
      name = "Ilya",
      email = "ivn25ov@mail.ru",
      url = "https://github.com/48-iq"
    )
  )
)
@Configuration
public class OpenApiConfig {
  
}
