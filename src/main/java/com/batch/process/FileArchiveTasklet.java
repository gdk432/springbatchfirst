package com.batch.process;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;

public class FileArchiveTasklet implements Tasklet{

	private static final Logger logger = LoggerFactory.getLogger(FileArchiveTasklet.class); 
	
	Resource[] resources;
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Arrays.asList(resources).forEach(r->{
			try {
				r.getFile().delete();
			}catch(IOException e) {
				logger.error(e.getMessage());
			}
		});
		return RepeatStatus.FINISHED;
	}
	public Resource[] getResources() {
		return resources;
	}
	public void setResources(Resource[] resources) {
		this.resources = resources;
	}
	
	

}
