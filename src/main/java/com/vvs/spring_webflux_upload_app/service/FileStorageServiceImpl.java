package com.vvs.spring_webflux_upload_app.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    
    @Value("${file.upload-dir}")
    private String uploadDir = "uploads";
    
    private final Path root = Paths.get(uploadDir);

    @Override
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder to uploads.");
        }
    }

    @Override
    public Mono<String> save(Mono<FilePart> filePart) {
        return filePart.doOnNext(fp -> System.out.println("Reciving file: " + fp.filename()))
            .flatMap(filePrt -> {
                String filename = filePrt.filename();
                return filePrt.transferTo(root.resolve(filename))
                    .then(Mono.just(filename));
            });
    }

    @Override
    public Flux<DataBuffer> load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 4098);
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
          return Files.walk(this.root, 1)
              .filter(path -> !path.equals(this.root))
              .map(this.root::relativize);
        } catch (IOException e) {
          throw new RuntimeException("Could not load the files!");
        }
    }
}
