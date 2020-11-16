package com.batch;

import javax.batch.operations.JobExecutionAlreadyCompleteException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class SpringBatchFirstApplication {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	JobOperator jobOperator;

	@Autowired
	Job job;

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchFirstApplication.class, args);
	}

	@Scheduled(fixedDelay=30000)
	public void doJob(){
		JobParameters parameters = new JobParametersBuilder()
		.addString("JobId", String.valueOf(System.currentTimeMillis()))
		.addString("JobName",job.getName())
		.toJobParameters();
		
		try {
			jobLauncher.run(job,parameters)	;
		}catch(JobExecutionAlreadyRunningException e){
			e.printStackTrace();
		}catch(JobRestartException e) {
			e.printStackTrace();
		}catch(JobInstanceAlreadyCompleteException e) {
			e.printStackTrace();
		}catch(JobExecutionAlreadyCompleteException e) {
			e.printStackTrace();
		} catch (JobParametersInvalidException e) {
			e.printStackTrace();
		}
		
	}

}
