DROP TABLE people IF EXISTS;
DROP TABLE employee IF EXISTS;


CREATE TABLE people  (
    person_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20)
);

CREATE TABLE employee  (
    employee_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20),
    role VARCHAR(20),
    salary BIGINT
);