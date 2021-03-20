package com.incidents.processor.batch;

import com.incidents.processor.IncidentProcessorApplication;
import com.incidents.processor.batch.model.IncidentStat;
import com.incidents.processor.batch.reader.IncidentStatsReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Shivaji Pote
 **/
@SpringBatchTest
@Import(IncidentProcessorBatchTest.DataSourceConfig.class)
@SpringBootTest(classes = IncidentProcessorApplication.class)
@TestPropertySource(locations = {"classpath:application.properties"})
class IncidentProcessorBatchTest {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private Job incidentsProcessorJob;

  @Autowired
  private IncidentStatsReader incidentStatsReader;

  @Autowired
  private JobRepositoryTestUtils jobRepositoryTestUtils;

  @BeforeEach
  void setup() {
    jobRepositoryTestUtils.removeJobExecutions();
  }

  @Test
  void testIncidentsProcessorJob_Launch() throws Exception {
    jobLauncherTestUtils.setJob(incidentsProcessorJob);
    final JobExecution execution = jobLauncherTestUtils.launchJob();
    assertEquals(BatchStatus.COMPLETED, execution.getStatus());
    final Set<IncidentStat> stats = incidentStatsReader.read("src/test/resources/output.csv");
    assertNotNull(stats);
    assertEquals(4, stats.size());
    final IncidentStat insuranceStat = stats.stream().filter(incidentStat -> incidentStat.getAssetName().equalsIgnoreCase("Insurance")).findFirst().orElse(null);
    assertNotNull(insuranceStat);
    assertEquals(3, insuranceStat.getTotalIncidents());
    assertEquals(60071, insuranceStat.getUpTime());
    assertEquals("30.0%", insuranceStat.getTotalDownTime());
    assertEquals(70, insuranceStat.getRating());
  }


  @Configuration
  public static class DataSourceConfig {
    @Bean
    public DataSource dataSource() {
      return new EmbeddedDatabaseBuilder().build();
    }
  }

}
