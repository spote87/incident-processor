package com.incidents.processor.batch;

import com.incidents.processor.batch.model.Incident;
import com.incidents.processor.batch.writer.IncidentJobWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;


/**
 * @author Shivaji Pote
 **/
@Log4j2
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class SpringBatchConfiguration {

  private final JobBuilderFactory jobBuilderFactory;

  private final StepBuilderFactory stepBuilderFactory;

  private final IncidentJobWriter incidentJobWriter;

  private final Tasklet statsWriterTasklet;

  @Value("${incidents.input.file}")
  private Resource inputFile;

  @Value("${incidents.record.separator}")
  private String separator;

  @Bean
  public Job incidentsProcessorJob() {
    return jobBuilderFactory.get("Incidents Processor Job").incrementer(new RunIdIncrementer()).flow(incidentProcessorStep()).next(statsFileWriterStep()).end().build();
  }

  private Step statsFileWriterStep() {
    return stepBuilderFactory.get("Create Incidents Stats").allowStartIfComplete(true).tasklet(statsWriterTasklet).build();
  }

  @Bean
  public Step incidentProcessorStep() {
    return stepBuilderFactory.get("Incident Processor Step").allowStartIfComplete(true).<Incident, Incident>
      chunk(1000).reader(incidentDataReader()).processor(incidentProcessor()).writer(incidentJobWriter).build();
  }

  @Bean
  public ItemProcessor<Incident, Incident> incidentProcessor() {
    return a -> a;
  }

  @Bean
  public ItemReader<Incident> incidentDataReader() {
    log.debug("Creating incidents reader instance");
    final BeanWrapperFieldSetMapper<Incident> mapper = new BeanWrapperFieldSetMapper<>();
    mapper.setTargetType(Incident.class);
    final DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
    tokenizer.setIncludedFields(0, 1, 2, 3);
    tokenizer.setDelimiter(separator);
    tokenizer.setNames(new String[]{"assetName", "startTime", "endTime", "severity"});
    return new FlatFileItemReaderBuilder<Incident>().name("IncidentsReader").resource(inputFile).linesToSkip(1).lineTokenizer(tokenizer).fieldSetMapper(mapper).build();
  }
}
