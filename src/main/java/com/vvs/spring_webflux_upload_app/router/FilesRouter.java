package com.vvs.spring_webflux_upload_app.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.vvs.spring_webflux_upload_app.handler.FilesHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;

@Configuration
public class FilesRouter {
    
    @Bean
    public RouterFunction<ServerResponse> filesRoute(FilesHandler handler) {
        return nest(path("/api/files"), 
            route(
                POST("/upload"), handler::uploadFile).andRoute(
                GET(""), handler::filesList).andRoute(
                GET("/{filename:.+}"), handler::getFile));
    }
}
