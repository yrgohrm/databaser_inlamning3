-- This script creates the test database.
-- Intended for MS SQL Server 2019

DROP DATABASE test_db;
GO

CREATE DATABASE test_db
COLLATE Finnish_Swedish_100_CS_AI_SC_UTF8;
GO

USE test_db;
GO

CREATE TABLE example_table (
    id INT IDENTITY(1,1) PRIMARY KEY, 
    val VARCHAR(10) NOT NULL
);
GO
