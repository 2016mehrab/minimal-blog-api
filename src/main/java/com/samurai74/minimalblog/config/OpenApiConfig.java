package com.samurai74.minimalblog.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info =@Info(
                contact = @Contact(
                        name = "Mehrab Hasan",
                        email="2016mehrab@gmail.com",
                        url="https://github.com/2016mehrab"
                ),
                description = "OpenApi documentation for minimal-blog",
                title = "OpenApi Specification - Mehrab",
                version = "0.0"

        ),
        servers ={
                @Server(
                        description = "Local ENV",
                        url="http://localhost:8080"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "JWT Authentication"
                )
        }
)
@SecurityScheme(
        name = "JWT Authentication",
        description = "Authenticate with your JWT Token",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
