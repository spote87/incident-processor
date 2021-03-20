package com.incidents.processor.batch.report;

import com.incidents.processor.batch.model.IncidentStat;
import com.incidents.processor.batch.reader.IncidentStatsReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This is final report generator tasklet.
 *
 * @author Shivaji Pote
 **/
@Component
@Log4j2
@RequiredArgsConstructor
public class StatsReportTasklet implements Tasklet {

  private final IncidentStatsReader incidentStatsReader;

  private final ReportGenerator reportGenerator;

  @Value("${incidents.output.temp-file}")
  private String tempFile;

  @Value("${incidents.output.file}")
  private String outputFile;

  /**
   * This method creates final report by reading data from temporary file. It deletes temporary file after successful
   * report generation.
   *
   * @param stepContribution {@link StepContribution} instance
   * @param chunkContext     {@link ChunkContext} instance
   * @return {@link RepeatStatus#FINISHED} upon successful completion
   */
  @Override
  public RepeatStatus execute(final StepContribution stepContribution, final ChunkContext chunkContext) {
    log.info("Writing stats data to {}", outputFile);
    final Set<IncidentStat> allStats = incidentStatsReader.read(tempFile);
    final Set<IncidentStat> processedStats = allStats.parallelStream().map(this::computeDownTime).collect(Collectors.toSet());
    reportGenerator.createReport(processedStats, outputFile);
    deleteTempFile();
    return RepeatStatus.FINISHED;
  }

  private IncidentStat computeDownTime(final IncidentStat incidentStat) {
    final int secondsOfDay = (int) Duration.ofDays(1).getSeconds();
    final int donTime = secondsOfDay - incidentStat.getUpTime();
    final Double downPercent = (double) (donTime * 100 / secondsOfDay);
    incidentStat.setTotalDownTime(downPercent.toString() + "%");
    return incidentStat;
  }

  private void deleteTempFile() {
    try {
      FileUtils.forceDelete(new File(tempFile));
    } catch (final IOException e) {
      log.error("Failed to delete temp file", e);
    }
  }

}
