package com.example.springbatch.batchprocessing.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Employee extends Person{

//	private String lastName;
//	private String firstName;
	private Integer salary;
	private String role;

	private enum Role {
		Manager, Senior, Junior,
	};

	private static final List<Role> VALUES = Collections.unmodifiableList(Arrays.asList(Role.values()));
	private static final int SIZE = VALUES.size();
	private static final Random RANDOM = new Random();

	private static String randomRole() {
		return VALUES.get(RANDOM.nextInt(SIZE)).toString();
	}

	private Integer randomSalary() {
		return RANDOM.nextInt(1000) + 1500;
	}

	public Employee() {
	}

	public Employee(String lastName, String firstName) {
		super.setLastName(lastName);
		super.setFirstName(firstName);
		this.role = randomRole();
		this.salary = randomSalary();
	}
//
//	public String getLastName() {
//		return lastName;
//	}
//
//	public void setLastName(String lastName) {
//		this.lastName = lastName;
//	}
//
//	public String getFirstName() {
//		return firstName;
//	}
//
//	public void setFirstName(String firstName) {
//		this.firstName = firstName;
//	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Integer getSalary() {
		return salary;
	}

	public void setSalary(Integer salary) {
		this.salary = salary;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Employee [lastName=");
		builder.append(getLastName());
		builder.append(", firstName=");
		builder.append(getFirstName());
		builder.append(", role=");
		builder.append(role);
		builder.append(", salary=");
		builder.append(salary);
		builder.append("]");
		return builder.toString();
	}

}
