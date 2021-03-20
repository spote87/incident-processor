package com.incidents.processor.batch.reader;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.incidents.processor.batch.model.IncidentStat;
import com.incidents.processor.batch.model.IncidentStatFormat;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Shivaji Pote
 **/
@Component
@Log4j2
public class IncidentStatsReader {

  @Value("${incidents.record.separator}")
  private char separator;

  public Set<IncidentStat> read(final String fileName) {
    final CsvMapper mapper = new CsvMapper();
    mapper.addMixIn(IncidentStat.class, IncidentStatFormat.class);
    final CsvSchema csvSchema = mapper.schemaFor(IncidentStat.class).withSkipFirstDataRow(true).withoutHeader().withColumnSeparator(separator);
    try (final InputStream file = new FileInputStream(fileName)) {
      final MappingIterator<IncidentStat> iterator = mapper.readerFor(IncidentStat.class).with(csvSchema).readValues(file);
      final Set<IncidentStat> allData = new HashSet<>();
      while (iterator.hasNext()) {
        allData.add(iterator.next());
      }
      return allData;
    } catch (final IOException e) {
      log.error("Failed to read incident stats. Error: {} ", e.getMessage());
    }
    return Collections.emptySet();
  }
}
