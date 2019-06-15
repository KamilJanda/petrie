# Petrie

## database setup

install postgres on your local machine.

than create database and user with this commands: 

```sql
CREATE ROLE xxxx WITH LOGIN PASSWORD 'pass123';
CREATE DATABASE petrie;
GRANT ALL PRIVILEGES ON DATABASE petrie TO xxxx;
```