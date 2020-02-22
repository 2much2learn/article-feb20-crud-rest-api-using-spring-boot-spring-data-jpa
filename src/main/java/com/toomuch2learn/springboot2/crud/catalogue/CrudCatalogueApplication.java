package com.toomuch2learn.springboot2.crud.catalogue;

import com.toomuch2learn.springboot2.crud.catalogue.configuration.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
	FileStorageProperties.class
})
public class CrudCatalogueApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudCatalogueApplication.class, args);
	}
}
