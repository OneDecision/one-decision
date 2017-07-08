-- DROP DATABASE od_db;
CREATE DATABASE od_db;
CREATE USER 'od'@'localhost' IDENTIFIED BY 'od';  
GRANT ALL ON od_db.* TO 'od'@'localhost'; 
