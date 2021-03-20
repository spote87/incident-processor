package com.incidents.processor.batch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Formatter class used as mixin in jackson {@link com.fasterxml.jackson.dataformat.csv.CsvMapper} for customizing csv
 * column headers.
 *
 * @author Shivaji Pote
 **/
public abstract class IncidentStatFormat {

  @JsonProperty("Total Downtime (in %)")
  abstract String getTotalDownTime();

  @JsonProperty("Total Uptime")
  abstract int getUpTime();

  @JsonProperty("Rating")
  abstract int getRating();

  @JsonProperty("Total Incidents")
  abstract int getTotalIncidents();

  @JsonProperty("Asset Name")
  abstract String getAssetName();

}
