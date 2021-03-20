package com.incidents.processor.controller;

import com.incidents.processor.service.IncidentStatsReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Shivaji Pote
 **/
@SpringBootTest
@AutoConfigureMockMvc
class IncidentStatsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private IncidentStatsReportService reportService;

  @Test
  void getIncidentStats_ReturnsIncidentStatisticReport() throws Exception {
    when(reportService.getReport()).thenReturn(new File("src/test/resources/output.csv"));
    mockMvc.perform(get("/statistics")).andExpect(status().isOk()).andExpect(content().string("\"Asset Name\";\"Total Incidents\";\"Total Uptime\";\"Total Downtime (in %)\";Rating\n" +
      "Insurance;3;60071;\"30.0%\";70\n" +
      "Homeloans;2;70161;\"18.0%\";40\n" +
      "CRM;1;77961;\"9.0%\";30\n" +
      "\"Payments Gateway\";3;86400;\"0.0%\";30\n"));
  }

  @Test
  void getIncidentStats_ThrowsExceptionWhenFileNotFound() throws Exception {
    when(reportService.getReport()).thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error error"));
    mockMvc.perform(get("/statistics")).andExpect(status().isInternalServerError());
  }
}
