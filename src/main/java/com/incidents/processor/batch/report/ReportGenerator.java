package com.incidents.processor.batch.report;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.incidents.processor.batch.model.IncidentStat;
import com.incidents.processor.batch.model.IncidentStatFormat;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

/**
 * Incidents statistics report creator class.
 *
 * @author Shivaji Pote
 **/
@Log4j2
@Component
public class ReportGenerator {

  @Value("${incidents.record.separator}")
  private char separator;

  /**
   * This method creates report(file) from provided set of {@link IncidentStat}s. It creates the report with provided
   * file name.
   *
   * @param incidents set of incidents
   * @param fileName  path of the report file
   */
  public void createReport(final @NonNull Set<IncidentStat> incidents, final @NonNull String fileName) {
    final CsvMapper csvMapper = new CsvMapper();
    csvMapper.addMixIn(IncidentStat.class, IncidentStatFormat.class);
    final CsvSchema csvSchema = csvMapper.schemaFor(IncidentStat.class).withHeader().withColumnSeparator(separator);
    final ObjectWriter objectWriter = csvMapper.writer(csvSchema);
    try (final FileOutputStream outputStream = new FileOutputStream(fileName)) {
      objectWriter.writeValue(outputStream, incidents);
    } catch (final IOException e) {
      log.error("Failed to write incidents data", e);
    }
  }
}
