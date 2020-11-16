package com.batch.config;



import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.batch.entity.Employee;
import com.batch.entity.EmployeeMapper;
import com.batch.process.EmployeeJobListener;
import com.batch.process.EmployeeWriter;
import com.batch.process.FileArchiveTasklet;

@Configuration
@EnableBatchProcessing
public class BatchProcessConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(BatchProcessConfiguration.class);
	
	@Autowired
	DataSource datSource;
	
	@Autowired
	EntityManagerFactory entityManagerFactory;
	
	@Autowired 
	JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	StepBuilderFactory stepBuilderFactory;
	
	@Value("classpath:/employee*.csv")
	private Resource[] employeeInput;
	
	@Bean
	public MultiResourceItemReader<Employee> multiResourceReader(){
		
		MultiResourceItemReader<Employee> reader = new MultiResourceItemReader<Employee>();
		reader.setDelegate(reader());
		reader.setResources(employeeInput);
		reader.setStrict(false);
		return reader;
	}
	
	@Bean
	public FlatFileItemReader<Employee> reader(){
		
		FlatFileItemReader<Employee> employeeReader = new FlatFileItemReader<Employee>();
		DelimitedLineTokenizer delimiter = new DelimitedLineTokenizer(); 
		String[] tokens = {"empId","empName"};
		delimiter.setNames(tokens);
		delimiter.setDelimiter(",");
		employeeReader.setStrict(false);
		DefaultLineMapper<Employee> lineMapper = new DefaultLineMapper<Employee>();
		lineMapper.setLineTokenizer(delimiter);
		EmployeeMapper mapper = new EmployeeMapper();
		lineMapper.setFieldSetMapper(mapper);
		employeeReader.setLineMapper(lineMapper);
		employeeReader.setLinesToSkip(0);
				
		return employeeReader;
	}
	
	@Bean
	public EmployeeWriter<Employee> writer(){
		
		return new EmployeeWriter<Employee>();
	}
		
	@Bean
	public JdbcBatchItemWriter<Employee> jdbcWriter(){
		
		JdbcBatchItemWriter<Employee> dbWriter = new JdbcBatchItemWriter<Employee>();
		dbWriter.setDataSource(datSource);
		dbWriter.setSql("INSERT INTO EMPLOYEE (EMP_ID,EMP_NAME) VALUES (:empId,:empName)");
		dbWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Employee>());
		return dbWriter;
	}
	
	@Bean
	public JpaItemWriter<Employee> jpaWriter(){
		
		 JpaItemWriter<Employee> employeeWriter= new  JpaItemWriter<Employee>();
		 employeeWriter.setEntityManagerFactory(entityManagerFactory);
		 return employeeWriter;
	}
	
	@Bean
	public Job readEmployeeCSV() {
		return jobBuilderFactory.get("readEmployeeCSV")
				.incrementer(new RunIdIncrementer())
				.listener(new EmployeeJobListener())
				.flow(step1())
				.next(step2())
				.end()
				.build();
				
				
	}
	
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Employee, Employee> chunk(2)
				.reader(multiResourceReader())
				.writer(jpaWriter())
				.build();
				
	}
	
	@Bean
	public Step step2() {
		FileArchiveTasklet tasklet =  new FileArchiveTasklet();
		tasklet.setResources(employeeInput);
		return stepBuilderFactory.get("step2")
				.tasklet(tasklet)
				.build();
	}
	
}
