CREATE SEQUENCE task_sequence
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;

CREATE TABLE task (
                    id BIGINT PRIMARY KEY DEFAULT nextval('task_sequence'),
                    title VARCHAR(255) NOT NULL,
                    description VARCHAR(1000),
                    status VARCHAR(255) NOT NULL,
                    due_date DATE NOT NULL
);

