package com.henriquericcio.springbatchdemo.job;

import com.henriquericcio.springbatchdemo.model.Contact;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public FlatFileItemReader<Contact> reader(@Value("#{jobParameters['inputFile']}") String inputFile) {
        return new FlatFileItemReaderBuilder<Contact>()
                .name("contactItemReader")
                .resource(new FileSystemResource(inputFile))
                .linesToSkip(1)
                .delimited()
                .names(new String[]{"id", "firstName", "lastName", "phoneNumber"})
                .targetType(Contact.class)
                .build();
    }

    @Bean
    public ContactItemProcessor processor() {
        return new ContactItemProcessor();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Contact> writer(@Value("#{jobParameters['outputFile']}") String outputFile) {
        return new FlatFileItemWriterBuilder<Contact>()
                .name("contactItemWriter")
                .resource(new FileSystemResource(outputFile))
                //.append(true)
                .delimited()
                .names(new String[]{"id", "firstName", "lastName"})
                .build();
    }

    @Bean
    @StepScope
    public FileBackupTasklet deleteTasklet(@Value("#{jobParameters['inputFile']}") String inputFile) {
        FileBackupTasklet tasklet = new FileBackupTasklet();
        tasklet.setResource(new FileSystemResource(inputFile));
        return tasklet;
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Contact, Contact>chunk(10)
                .reader(reader(null))
                .processor(processor())
                .writer(writer(null))
                .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet(deleteTasklet(null))
                .build();
    }

    @Bean
    public JobCompletionNotificationListener listener(){
        return new JobCompletionNotificationListener();
    }

    @Bean
    public Job importContactJob(JobCompletionNotificationListener listener, Step step1, Step step2) {
        return jobBuilderFactory.get("transformContactsJob")
                .listener(listener)
                .start(step1)
                .next(step2)
                .build();
    }
}
