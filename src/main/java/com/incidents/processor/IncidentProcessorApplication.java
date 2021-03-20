package com.incidents.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableScheduling
public class IncidentProcessorApplication {

  public static void main(final String[] args) {
    SpringApplication.run(IncidentProcessorApplication.class, args);
  }

}
