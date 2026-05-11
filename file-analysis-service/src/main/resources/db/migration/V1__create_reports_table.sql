CREATE TABLE reports (
  report_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  homework_id BIGINT NOT NULL,
  file_name TEXT NOT NULL,
  status TEXT NOT NULL,
  file_format TEXT,
  file_size_bytes BIGINT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_report_homework ON reports(homework_id);

CREATE TABLE report_issues (
  report_id BIGINT NOT NULL REFERENCES reports(report_id) ON DELETE CASCADE,
  issue TEXT NOT NULL
);

CREATE INDEX idx_issue_report ON report_issues(report_id);
