package com.incidents.processor.batch.writer;

import com.incidents.processor.batch.model.IncidentStat;
import com.incidents.processor.batch.reader.IncidentStatsReader;
import com.incidents.processor.batch.report.ReportGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Shivaji Pote
 **/
@Component
@RequiredArgsConstructor
@Log4j2
public class IncidentDataProcessor {

  private final IncidentStatsReader incidentStatsReader;

  private final ReportGenerator reportGenerator;

  @Value("${incidents.output.temp-file}")
  private String tempFile;

  public void write(final @NonNull Set<IncidentStat> incidentStats) {
    log.debug("Appending incident statistics in existing file");
    final Set<IncidentStat> existingIncidents = incidentStatsReader.read(tempFile);
    if (!CollectionUtils.isEmpty(existingIncidents)) {
      final Set<IncidentStat> allStats = new HashSet<>(existingIncidents);
      incidentStats.parallelStream().forEach(stat -> existingIncidents.parallelStream().filter(incStat -> incStat.getAssetName().equalsIgnoreCase(stat.getAssetName())).findFirst().ifPresentOrElse(ei -> allStats.add(updateIncidentStat(stat, ei)), () -> allStats.add(stat)));
      reportGenerator.createReport(allStats, tempFile);
    } else {
      reportGenerator.createReport(incidentStats, tempFile);
    }
  }

  private IncidentStat updateIncidentStat(final IncidentStat stat, final IncidentStat existingStat) {
    existingStat.setTotalIncidents(existingStat.getTotalIncidents() + stat.getTotalIncidents());
    existingStat.setRating(existingStat.getRating() + stat.getRating());
    existingStat.setUpTime(existingStat.getUpTime() + stat.getUpTime());
    return existingStat;
  }

}
