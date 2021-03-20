package com.incidents.processor.batch.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

/**
 * Model class for holding row data from incidents input file.
 *
 * @author Shivaji Pote
 **/
@Getter
@Setter
@JsonPropertyOrder({"assetName", "startTime", "endTime", "severity"})
public class Incident {

  private String assetName;

  private String startTime;

  private String endTime;

  private int severity;
}
