package com.vvs.spring_webflux_upload_app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vvs.spring_webflux_upload_app.service.FileStorageService;

import jakarta.annotation.Resource;

@SpringBootApplication
public class SpringWebfluxUploadAppApplication implements CommandLineRunner {

	@Resource
	FileStorageService storageService;

	public static void main(String[] args) {
		SpringApplication.run(SpringWebfluxUploadAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		storageService.init();
	}

}
