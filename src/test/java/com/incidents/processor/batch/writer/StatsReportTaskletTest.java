package com.incidents.processor.batch.writer;

import com.incidents.processor.batch.model.IncidentStat;
import com.incidents.processor.batch.reader.IncidentStatsReader;
import com.incidents.processor.batch.report.ReportGenerator;
import com.incidents.processor.batch.report.StatsReportTasklet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.incidents.processor.batch.BatchTestUtil.getIncidentStat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

/**
 * @author Shivaji Pote
 **/
@ExtendWith(MockitoExtension.class)
class StatsReportTaskletTest {

  @InjectMocks
  private StatsReportTasklet tasklet;

  @Mock
  private IncidentStatsReader incidentStatsReader;

  @Mock
  private ReportGenerator reportGenerator;

  @Captor
  private ArgumentCaptor<Set<IncidentStat>> argumentCaptor;

  @BeforeEach
  void setup() {
    setField(tasklet, "tempFile", "TEMP_FILE");
    setField(tasklet, "outputFile", "oOUTPUT_FILE");
  }

  @Test
  void execute_UpdatesIncidentDownTimePercentAndWritesItToStatsOutputFile() throws Exception {
    final Set<IncidentStat> mockedData = new HashSet<>();
    mockedData.add(getIncidentStat("Test asset", 10, 50000, 50));
    when(incidentStatsReader.read(anyString())).thenReturn(mockedData);
    tasklet.execute(null, null);
    verify(reportGenerator, times(1)).createReport(argumentCaptor.capture(), anyString());
    assertEquals(Arrays.asList(getExpectedResult()), argumentCaptor.getAllValues());
  }

  private Set<IncidentStat> getExpectedResult() {
    final Set<IncidentStat> expectedStats = new HashSet<>();
    final IncidentStat stat = getIncidentStat("Test asset", 10, 50000, 50);
    stat.setUpTime(null);
    stat.setTotalDownTime("41.12");
    expectedStats.add(stat);
    return expectedStats;
  }
}
