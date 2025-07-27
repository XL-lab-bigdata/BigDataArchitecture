--
-- log_analysis_db.sql - comprehensive-case-flink
--
-- Copyright 2023 Jinsong Zhang
--
-- This file can NOT be copied and/or distributed without the express permission of Jinsong Zhang
--

DO
$do$
BEGIN
  IF EXISTS (
    SELECT FROM pg_catalog.pg_roles
    WHERE  rolname = 'log_analyzer') THEN

    RAISE NOTICE 'Role "log_analyzer" already exists. Skipping.';
  ELSE
    CREATE ROLE log_analyzer LOGIN PASSWORD 'log_analyzer_pass';
  END IF;
END
$do$;

SELECT 'CREATE DATABASE log_analysis WITH OWNER = log_analyzer ENCODING = ''UTF8'''
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'log_analysis')\gexec

\c log_analysis

CREATE TABLE IF NOT EXISTS web01_nginx_log_analysis
(
  id serial,
  start_timestamp timestamp with time zone NOT NULL,
  end_timestamp timestamp with time zone NOT NULL,
  request_count integer NOT NULL,
  min_request_time double precision NOT NULL,
  max_request_time double precision NOT NULL,
  mean_request_time double precision NOT NULL,
  std_dev_request_time double precision NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT web01_nginx_log_analysis_duration_unique UNIQUE (start_timestamp, end_timestamp)
);

ALTER TABLE IF EXISTS public.web01_nginx_log_analysis
    OWNER TO log_analyzer;
