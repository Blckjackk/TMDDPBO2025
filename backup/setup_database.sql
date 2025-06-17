-- SQL script to set up the Azzam Love game database
-- Run this in your MySQL client to create the necessary database and table

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS azzam_love_db;

-- Use the database
USE azzam_love_db;

-- Create table for storing game results
CREATE TABLE IF NOT EXISTS thasil (
  username VARCHAR(100) PRIMARY KEY,
  skor INT NOT NULL,
  count INT NOT NULL
);

-- Optional: Insert some sample data for testing
INSERT INTO thasil (username, skor, count) VALUES ('TestUser', 100, 20)
ON DUPLICATE KEY UPDATE skor = 100, count = 20;

-- Confirm creation
SELECT 'Database and table created successfully!' as Message;
