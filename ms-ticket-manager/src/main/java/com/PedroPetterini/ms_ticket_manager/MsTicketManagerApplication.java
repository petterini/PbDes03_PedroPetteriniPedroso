package com.PedroPetterini.ms_ticket_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsTicketManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsTicketManagerApplication.class, args);
	}

}
