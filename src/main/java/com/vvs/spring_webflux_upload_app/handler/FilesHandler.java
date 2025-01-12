package com.vvs.spring_webflux_upload_app.handler;

import java.util.stream.Stream;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;

import com.vvs.spring_webflux_upload_app.message.ResponseMessage;
import com.vvs.spring_webflux_upload_app.model.FileInfo;
import com.vvs.spring_webflux_upload_app.service.FileStorageService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;;

@Component
@RequiredArgsConstructor
public class FilesHandler {
    
    private final FileStorageService fileStorage;

    public Mono<ServerResponse> uploadFile(ServerRequest request) {

        return request.multipartData()
            .flatMap(parts -> {
                return fileStorage.save(Mono.just((FilePart) parts.get("file").get(0)))
                    .flatMap(filename -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(new ResponseMessage("Upload the file successfully: " + filename))));
                })
                .onErrorResume(ex -> ServerResponse
                    .badRequest()
                    .body(ex.getMessage(), Throwable.class));
    }

    public Mono<ServerResponse> filesList(ServerRequest request) {
        Stream<FileInfo> fileInfo = fileStorage.loadAll()
            .map(path -> {
                String filename = path.getFileName().toString();
                String url = UriComponentsBuilder.newInstance().path("/api/files/{filename}").buildAndExpand(filename).toUriString();
                return new FileInfo(filename, url);
            });
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromValue(fileInfo));
    }

    public Mono<ServerResponse> getFile(ServerRequest request) {
        String filename = request.pathVariable("filename");
        return Mono.just(fileStorage.load(filename))
            .flatMap(file -> ServerResponse
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename +"\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file, DataBuffer.class));
    }
}
