package com.medical_application;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MedicalApplication {

	public static void main(String[] args) {

		SpringApplication.run(MedicalApplication.class, args);
	}
	@Bean
    public ModelMapper modelMapper(){
		return new ModelMapper();
   }
}
