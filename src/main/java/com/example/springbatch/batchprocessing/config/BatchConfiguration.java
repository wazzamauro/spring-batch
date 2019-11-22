package com.example.springbatch.batchprocessing.config;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;

import com.example.springbatch.batchprocessing.model.Employee;
import com.example.springbatch.batchprocessing.model.Person;
import com.example.springbatch.batchprocessing.processor.EmployeeFromPersonProcessor;
import com.example.springbatch.batchprocessing.processor.PersonItemProcessor;
import com.example.springbatch.batchprocessing.util.JobCompletionNotificationListener;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	@Bean
	public FlatFileItemReader<Person> readerPers() {
		return new FlatFileItemReaderBuilder<Person>().name("personItemReader")
				.resource(new ClassPathResource("sample-data.csv")).delimited()
				.names(new String[] { "firstName", "lastName" })
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {
					{
						setTargetType(Person.class);
					}
				}).build();
	}

	@Bean(destroyMethod="")
	public ItemReader<Person> readerEmp(DataSource dataSource) {
		JdbcCursorItemReader<Person> cursorItemReader = new JdbcCursorItemReader<Person>();
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setSql("SELECT first_name, last_name FROM people");
		cursorItemReader.setRowMapper(new PersonRowMapper());
		return cursorItemReader;

	}

	public class PersonRowMapper implements RowMapper<Person>{
		@Override
		public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
			Person person = new Person();
			person.setFirstName(rs.getString("first_name"));
			person.setLastName(rs.getString("last_name"));
			return person;
		}
	}

	@Bean
	public PersonItemProcessor processorPers() {
		return new PersonItemProcessor();
	}

	@Bean
	public EmployeeFromPersonProcessor processorEmp() {
		return new EmployeeFromPersonProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Person> writerPers(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Person>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
				.dataSource(dataSource).build();
	}

	@Bean
	public JdbcBatchItemWriter<Employee> writerEmp(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Employee>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO employee (first_name, last_name, role, salary) VALUES (:firstName, :lastName, :role, :salary)")
				.dataSource(dataSource).build();
	}

	@Bean
	public Job uniqueJob(Step stepPers, Step stepEmp) {
		return jobBuilderFactory.get("uniqueJob").incrementer(new RunIdIncrementer()).start(stepPers).next(stepEmp).build();
		
	}
	
//	@Bean
//	@Order(1)
//	public Job importUserJob(JobCompletionNotificationListener listener, Step stepPers) {
//		return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer()).listener(listener)
//				.flow(stepPers).end().build();
//	}
//
//	@Bean
//	@Order(2)
//	public Job transformPeopleInEmployee(Step stepEmp) {
//		return jobBuilderFactory.get("transformPeopleInEmployee").incrementer(new RunIdIncrementer()).flow(stepEmp).end()
//				.build();
//	}

	@Bean
	public Step stepPers(JdbcBatchItemWriter<Person> writerPers) {
		return stepBuilderFactory.get("stepPers").<Person, Person>chunk(10).reader(readerPers())
				.processor(processorPers()).writer(writerPers).build();
	}

	@Bean
	public Step stepEmp(JdbcBatchItemWriter<Employee> writerEmp) {
		return stepBuilderFactory.get("stepEmp").<Person, Employee>chunk(10).reader(readerEmp(dataSource))
				.processor(processorEmp()).writer(writerEmp).build();
	}
}
