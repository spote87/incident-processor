package com.incidents.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Incident processor application launcher class.
 *
 * @author Shivaji Pote
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableScheduling
public class IncidentProcessorApplication {

  /**
   * Main method which will launch this spring boot application.
   *
   * @param args application startup arguments
   */
  public static void main(final String[] args) {
    SpringApplication.run(IncidentProcessorApplication.class, args);
  }

}
