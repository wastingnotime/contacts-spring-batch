package com.henriquericcio.springbatchdemo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class SpringBatchDemoApplication implements CommandLineRunner {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        if (args.length == 0)
            throw new IllegalArgumentException("input file path must be set");

        File inputFile = new File (args[0]);
        String outputFileName = args.length > 1 ? args[1] :  Paths.get(inputFile.getParent(),"output.csv").toString();

        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .addString("inputFile", inputFile.getPath())
                .addString("outputFile", outputFileName)
                .toJobParameters();
        jobLauncher.run(job, params);
    }

}
