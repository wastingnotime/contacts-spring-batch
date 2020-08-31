package com.henriquericcio.springbatchdemo;

import com.henriquericcio.springbatchdemo.job.ContactItemProcessor;
import com.henriquericcio.springbatchdemo.job.JobConfiguration;
import com.henriquericcio.springbatchdemo.model.Contact;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.test.AssertFile;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBatchDemoApplicationTests.BatchTestConfig.class})
public class SpringBatchDemoApplicationTests {

    private static final String TEST_OUTPUT = "src/test/resources/output.csv";
    private static final String EXPECTED_OUTPUT = "src/test/resources/expected-output.csv";
    private static final String TEST_INPUT = "src/test/resources/input.csv";
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void given_a_valid_file_when_job_executed_then_success() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");
    }

    @Test
    public void given_a_reference_output_when_job_executed_then_success() throws Exception {
        // given
        FileSystemResource expectedResult = new FileSystemResource(EXPECTED_OUTPUT);
        FileSystemResource actualResult = new FileSystemResource(TEST_OUTPUT);
        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();
        // then
        assertThat(actualJobInstance.getJobName()).isEqualTo("transformContactsJob");
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
        AssertFile.assertFileEquals(expectedResult, actualResult);
    }

    @Test
    public void given_a_contact_when_processor_runs_then_contact_has_been_changed() throws Exception {
        // given
        Contact input = new Contact(1L, "lastName", "firstName", "0000-1111");
        Contact expected = new Contact(1L, "LASTNAME", "FIRSTNAME", "0000-1111");
        // when
        ContactItemProcessor sut = new ContactItemProcessor();
        Contact actual = sut.process(input);
        // then
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }


    private JobParameters defaultJobParameters() {
        return new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .addString("inputFile", TEST_INPUT)
                .addString("outputFile", TEST_OUTPUT)
                .toJobParameters();
    }

    @Configuration
    @Import({JobConfiguration.class})
    static class BatchTestConfig {

        @Autowired
        private Job job;

        @Bean
        JobLauncherTestUtils jobLauncherTestUtils() throws NoSuchJobException {
            JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
            jobLauncherTestUtils.setJob(job);
            return jobLauncherTestUtils;
        }
    }
}
