package com.incidents.processor.service;

import java.io.File;

/**
 * This is service interface for managing incident stats.
 *
 * @author Shivaji Pote
 **/
public interface IncidentStatsReportService {

  /**
   * This method returns incident statics report generated by batch job. If report is not yet generated on that day, it
   * will launch batch job to generate the report. Once report is generated, it return that report.
   *
   * @return {@link File} instance pointing to incidents statistics report
   */
  File getReport();
}
