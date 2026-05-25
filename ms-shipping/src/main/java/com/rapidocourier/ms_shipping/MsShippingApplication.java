package com.rapidocourier.ms_shipping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients // <--- AGREGA ESTA LÍNEA
@EnableDiscoveryClient
@SpringBootApplication
public class MsShippingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsShippingApplication.class, args);
	}

}
