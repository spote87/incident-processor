package com.incidents.processor.service;

import com.incidents.processor.batch.launcher.IncidentProcessorJobLauncher;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * @author Shivaji Pote
 **/
@Service
@Log4j2
@RequiredArgsConstructor
public class IncidentStatsReportServiceImpl implements IncidentStatsReportService {

  private final IncidentProcessorJobLauncher jobLauncher;

  @Value("${incidents.output.file}")
  private String statsFile;

  @Override
  public File getReport() {
    log.debug("Fetching incident statistics");
    final File file = new File(statsFile);
    try {
      if (!file.exists() || file.isDirectory()
        || !isUpdatedToday(file)) {
        return createAndGetReport();
      }
    } catch (final IOException | CompletionException e) {
      log.error("Failed to read/create report. ", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to read/create report", e);
    }
    return file;
  }

  private File createAndGetReport() {
    log.error("Report not generated today. Generating one.");
    return CompletableFuture.runAsync(() -> {
      try {
        jobLauncher.lauchJob();
      } catch (final Exception e) {
        throw new CompletionException(e);
      }
    }).thenApply(a -> new File(statsFile)).join();
  }

  private boolean isUpdatedToday(final File file) throws IOException {
    final FileTime modifiedTime = Files.readAttributes(file.toPath(), BasicFileAttributes.class).lastModifiedTime();
    final LocalDate dateTime = LocalDate.ofInstant(modifiedTime.toInstant(), ZoneId.of("-05:00"));
    return dateTime.equals(LocalDate.now());
  }

}
