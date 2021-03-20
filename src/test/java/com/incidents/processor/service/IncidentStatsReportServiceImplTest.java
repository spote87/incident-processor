package com.incidents.processor.service;

import com.incidents.processor.batch.launcher.IncidentProcessorJobLauncher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.ReflectionTestUtils.setField;

/**
 * @author Shivaji Pote
 **/
@ExtendWith(MockitoExtension.class)
class IncidentStatsReportServiceImplTest {

  public static final String OUTPUT_1_CSV = "src/test/resources/output1.csv";

  @InjectMocks
  private IncidentStatsReportServiceImpl reportService;

  @Mock
  private IncidentProcessorJobLauncher jobLauncher;

  @Test
  void getReport_ReturnsFileIfExists() {
    setField(reportService, "statsFile", "src/test/resources/output.csv");
    final File file = reportService.getReport();
    assertNotNull(file);
    assertTrue(file.exists());
  }

  @Test
  void getReport_LaunchesBatchJobIfReportFileDoesNotExist() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
    setField(reportService, "statsFile", "OUTPUT_1_CSV");
    final File file = reportService.getReport();
    verify(jobLauncher, Mockito.times(1)).lauchJob();
  }

  @Test
  void getReport_ThrowsExceptionWhenJobLauncherThrowsException() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
    setField(reportService, "statsFile", "OUTPUT_1_CSV");
    doThrow(JobRestartException.class).when(jobLauncher).lauchJob();
    assertThrows(ResponseStatusException.class, () -> reportService.getReport());
    verify(jobLauncher, Mockito.times(1)).lauchJob();
  }
}
