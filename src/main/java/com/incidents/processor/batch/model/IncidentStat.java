package com.incidents.processor.batch.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.Objects;

/**
 * Model class for holding incidents statistics data.
 *
 * @author Shivaji Pote
 **/
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"assetName", "totalIncidents", "upTime", "totalDownTime", "rating"})
public class IncidentStat {

  private String assetName;

  private int totalIncidents;

  private Integer upTime;

  private String totalDownTime;

  private int rating;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final IncidentStat stat = (IncidentStat) o;
    return assetName.equals(stat.assetName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(assetName);
  }
}
