package com.fedex.aggregate_api;

import org.springframework.boot.SpringApplication;

public class TestAggregateApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(AggregateApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
