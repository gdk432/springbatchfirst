package com.batch.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class EmployeeMapper implements FieldSetMapper<Employee> {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeMapper.class);
	@Override
	public Employee mapFieldSet(FieldSet fieldSet) throws BindException {
		
		Employee employee = new Employee();
		employee.setEmpId(fieldSet.readInt("empId"));
		employee.setEmpName(fieldSet.readString(1));
		logger.info(employee.toString());
		
		return employee;
	}

}
