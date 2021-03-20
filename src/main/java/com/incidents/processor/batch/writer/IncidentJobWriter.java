package com.incidents.processor.batch.writer;

import com.incidents.processor.batch.model.Incident;
import com.incidents.processor.batch.model.IncidentStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Shivaji Pote
 **/
@Log4j2
@Component
@RequiredArgsConstructor
public class IncidentJobWriter implements ItemWriter<Incident> {

  private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-M-d H:m:s");

  private final IncidentDataProcessor incidentDataProcessor;

  @Override
  public void write(final List<? extends Incident> list) throws Exception {
    final Map<String, List<Incident>> incidentMap = list.parallelStream().collect(Collectors.groupingBy(Incident::getAssetName));
    final Set<IncidentStat> incidentStats = getCurrentIncidents(incidentMap);
    incidentDataProcessor.write(incidentStats);
  }

  private Set<IncidentStat> getCurrentIncidents(final Map<String, List<Incident>> incidentMap) {
    return incidentMap.entrySet().parallelStream().map(entry -> {
      final int upTime = getTotalUptime(entry);
      final int rating = entry.getValue().parallelStream().map(incident -> incident.getSeverity() == 1 ? 30 : 10).reduce(0, Integer::sum);
      return IncidentStat.builder().assetName(entry.getKey()).totalIncidents(entry.getValue().size()).upTime(upTime).rating(rating).build();
    }).collect(Collectors.toSet());
  }

  private int getTotalUptime(final Map.Entry<String, List<Incident>> entry) {
    final int secondsOfDay = (int) Duration.ofDays(1).getSeconds();
    final int downSeconds = entry.getValue().parallelStream().filter(incident -> incident.getSeverity() == 1).map(incident ->
      LocalDateTime.parse(incident.getEndTime(), DATE_TIME_FORMAT).toLocalTime().toSecondOfDay() - LocalDateTime.parse(incident.getStartTime(), DATE_TIME_FORMAT).toLocalTime().toSecondOfDay()).reduce(0, Integer::sum);
    return secondsOfDay - downSeconds;
  }

}
