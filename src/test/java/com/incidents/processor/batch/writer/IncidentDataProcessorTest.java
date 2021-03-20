package com.incidents.processor.batch.writer;

import com.incidents.processor.batch.model.IncidentStat;
import com.incidents.processor.batch.reader.IncidentStatsReader;
import com.incidents.processor.batch.report.ReportGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.incidents.processor.batch.BatchTestUtil.getIncidentStat;
import static com.incidents.processor.batch.BatchTestUtil.mockedIncidentStats;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Shivaji Pote
 **/
@ExtendWith(MockitoExtension.class)
class IncidentDataProcessorTest {

  @InjectMocks
  private IncidentDataProcessor statsWriter;

  @Mock
  private IncidentStatsReader incidentStatsReader;

  @Mock
  private ReportGenerator reportGenerator;

  @Captor
  private ArgumentCaptor<Set<IncidentStat>> argumentCaptor;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(statsWriter, "tempFile", "TEMP_FILE");
  }

  @Test
  void write_CallsWriterMethodsToWriteFreshData() throws IOException {
    when(incidentStatsReader.read(anyString())).thenReturn(null);
    final Set<IncidentStat> statsData = mockedIncidentStats();
    statsWriter.write(statsData);
    verify(incidentStatsReader, times(1)).read(anyString());
    verify(reportGenerator, times(1)).createReport(argumentCaptor.capture(), anyString());
    assertEquals(statsData, argumentCaptor.getValue());
  }

  @Test
  void write_CreatesFileAndWritesIncidentsStatsInFile() throws IOException {
    final Set<IncidentStat> statsData = mockedIncidentStats();
    when(incidentStatsReader.read(anyString())).thenReturn(statsData);
    final Set<IncidentStat> newData = new HashSet<>();
    final IncidentStat asset4 = getIncidentStat("Test asset4", 1, 100, 10);
    final IncidentStat asset1 = getIncidentStat("Test asset3", 1, 200, 10);
    newData.add(asset4);
    newData.add(asset1);
    statsWriter.write(newData);
    verify(incidentStatsReader, times(1)).read(anyString());
    statsData.add(asset4);
    verify(reportGenerator, times(1)).createReport(argumentCaptor.capture(), anyString());
    assertEquals(Arrays.asList(expectedData()), argumentCaptor.getAllValues());
  }

  private Set<IncidentStat> expectedData() {
    final Set<IncidentStat> stats = mockedIncidentStats();
    final IncidentStat asset4 = getIncidentStat("Test asset4", 1, 100, 10);
    final IncidentStat asset3 = getIncidentStat("Test asset3", 11, 1200, 60);
    stats.remove(asset3);
    stats.add(asset3);
    stats.add(asset4);
    return stats;
  }

}
