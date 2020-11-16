package com.batch.process;

import javax.batch.runtime.BatchStatus;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class EmployeeJobListener implements JobExecutionListener {

	@Override
	public void beforeJob(JobExecution jobExecution) {
		
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus().equals(BatchStatus.COMPLETED)) {
			System.out.println("comleted sucessfully");
		}
	}

}
