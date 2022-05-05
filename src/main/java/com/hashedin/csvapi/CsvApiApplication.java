package com.hashedin.csvapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class CsvApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CsvApiApplication.class, args);
	}

}
