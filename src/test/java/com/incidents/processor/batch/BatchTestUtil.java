package com.incidents.processor.batch;

import com.incidents.processor.batch.model.IncidentStat;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Shivaji Pote
 **/
public final class BatchTestUtil {

  private BatchTestUtil() throws InstantiationException {
    throw new InstantiationException();
  }

  public static Set<IncidentStat> mockedIncidentStats() {
    final Set<IncidentStat> incidentStats = new HashSet<>();
    final IncidentStat stat = getIncidentStat("Test asset", 3, 50000, 30);
    final IncidentStat stat1 = getIncidentStat("Test asset1", 5, 9000, 50);
    final IncidentStat stat2 = getIncidentStat("Test asset2", 0, 10000, 0);
    final IncidentStat stat3 = getIncidentStat("Test asset3", 10, 10000, 50);
    incidentStats.add(stat);
    incidentStats.add(stat1);
    incidentStats.add(stat2);
    incidentStats.add(stat3);
    return incidentStats;
  }

  public static IncidentStat getIncidentStat(final String asset, final int totalIncidents, final int totalUptime,
                                             final int rating) {
    final IncidentStat.IncidentStatBuilder incidentBuiler = IncidentStat.builder();
    incidentBuiler.assetName(asset).totalIncidents(totalIncidents).upTime(totalUptime).rating(rating);
    return incidentBuiler.build();
  }
}
