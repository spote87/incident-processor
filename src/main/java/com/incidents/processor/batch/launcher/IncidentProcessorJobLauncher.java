package com.incidents.processor.batch.launcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Incident processor job launcher class.
 *
 * @author Shivaji Pote
 **/
@Log4j2
@Component
@RequiredArgsConstructor
public class IncidentProcessorJobLauncher {

  private final JobLauncher jobLauncher;

  private final Job incidentsProcessorJob;

  /**
   * This method launches incident processor job. This method is scheduled to run at time configured in cron expression
   * <em>incidents.job.cron.expression</em>.
   *
   * @throws JobParametersInvalidException       if invalid job parameters passed
   * @throws JobExecutionAlreadyRunningException if job is already in running state
   * @throws JobRestartException                 if fails to restart job
   * @throws JobInstanceAlreadyCompleteException if job is not restartable and already completed
   */
  @Scheduled(cron = "${incidents.job.cron.expression}", zone = "EST")
  public void lauchJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
    log.info("Starting Incident processor job");
    final JobParameters jobParameters = new JobParametersBuilder().toJobParameters();
    jobLauncher.run(incidentsProcessorJob, jobParameters);
  }
}
