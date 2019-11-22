package com.example.springbatch.batchprocessing.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.example.springbatch.batchprocessing.model.Employee;
import com.example.springbatch.batchprocessing.model.Person;

public class EmployeeFromPersonProcessor implements ItemProcessor<Person, Employee> {
	
	private static final Logger log = LoggerFactory.getLogger(EmployeeFromPersonProcessor.class);

	@Override
	public Employee process(Person person) throws Exception {
		Employee emp = new Employee(person.getFirstName(), person.getLastName());

		log.info("Converting (" + person + ") into (" + emp + ")");

		return emp;
	}

}
