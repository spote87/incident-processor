package com.incidents.processor.controller;

import com.incidents.processor.service.IncidentStatsReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

import static org.springframework.http.MediaType.parseMediaType;

/**
 * Controller class containing endpoints/s for handling incident statistics.
 *
 * @author Shivaji Pote
 **/
@Log4j2
@RestController
@RequiredArgsConstructor
public class IncidentStatsController {

  public static final String TEXT_CSV = "text/csv";

  private final IncidentStatsReportService reportService;

  @Value("${incidents.output.file}")
  private String reportName;

  /**
   * This endpoint will fetch incident statics report and return the same as file attachment.
   *
   * @return {@link ResponseEntity} holding {@link FileSystemResource} which contains report
   */
  @GetMapping(value = "statistics")
  public ResponseEntity<FileSystemResource> getIncidentStats() {
    final File file = reportService.getReport();
    return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=" + reportName.substring(reportName.lastIndexOf(File.separator) + 1))
      .contentLength(file.length()).contentType(parseMediaType(TEXT_CSV))
      .body(new FileSystemResource(file));
  }
}
