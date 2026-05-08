CREATE TABLE students (
  student_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  student_name TEXT NOT NULL,
  student_surname TEXT NOT NULL,
  created_at TIMESTAMPZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_student_id ON students(student_id);

CREATE TABLE files (
  file_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  file_name TEXT NOT NULL,
  student_id BIGINT NOT NULL REFERENCES students(student_id),
  created_at TIMESTAMPZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_file_student ON files(student_id);