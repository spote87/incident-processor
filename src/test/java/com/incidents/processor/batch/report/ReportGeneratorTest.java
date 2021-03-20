package com.incidents.processor.batch.report;

import com.incidents.processor.batch.model.IncidentStat;
import com.incidents.processor.batch.reader.IncidentStatsReader;
import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static com.incidents.processor.batch.BatchTestUtil.mockedIncidentStats;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Shivaji Pote
 **/
@ExtendWith(MockitoExtension.class)
class ReportGeneratorTest {

  private final String TEMP_FILE = "src/test/resources/temp-output.csv";

  @InjectMocks
  private ReportGenerator writer;

  private IncidentStatsReader incidentStatsReader = new IncidentStatsReader();

  @SneakyThrows
  @AfterEach
  void cleanUp() {
    FileUtils.forceDelete(new File(TEMP_FILE));
  }

  @Test
  void write_WritesIncidentStatsDataToFile() throws IOException {
    writer.createReport(mockedIncidentStats(), TEMP_FILE);
    final Set<IncidentStat> incidentStats = incidentStatsReader.read(TEMP_FILE);
    assertNotNull(incidentStats);
    assertFalse(incidentStats.isEmpty());
    assertEquals(4, incidentStats.size());
  }
}
