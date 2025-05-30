-- store_api should be created at startup automatically
CREATE DATABASE IF NOT EXISTS store_api;
GRANT ALL PRIVILEGES ON store_api.* TO 'dbuser'@'%';


-- create user, database and set privileges for the testing database
CREATE DATABASE IF NOT EXISTS store_api_test;
CREATE USER IF NOT EXISTS 'dbuser_test'@'%' IDENTIFIED BY 'dbpw_test';
GRANT ALL PRIVILEGES ON store_api_test.* TO 'dbuser_test'@'%';