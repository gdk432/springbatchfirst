package com.batch.process;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

public class EmployeeWriter<T> implements ItemWriter<T>{

	private static final Logger logger = LoggerFactory.getLogger(EmployeeWriter.class);
			
	@Override
	public void write(List<? extends T> employees) throws Exception {
		employees.forEach(e->logger.info(e.toString()));
		
	}

}
