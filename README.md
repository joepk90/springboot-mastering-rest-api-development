# The Ultimate Spring Boot Course: Mastering Rest API Development

To Start the application, run the following commands:

Start the Database:
```
make db-start
```

Start the Springboot App:
```
make dev
```

To view the Springboot App, go to:
[localhost:8080](http://localhost:8080)


## Gobal Dependancies
- Jenv
- Maven

```
$ brew install jenv
$ brew install maven
```

## Maven Central Repository
https://central.sonatype.com/

## Spring Boot Devtools (spring-boot-devtools)
Spring Boot Devtools is used to automatically rebuild and reload the application when changes are made.

When the application is run as a fully packaged application, launched from java -jar file, Spring Boot Devtools is automatically disabled.

For more information see the documentation:
- [docs.spring.io/spring-boot/reference/using/devtools.html](https://docs.spring.io/spring-boot/reference/using/devtools.html#using.devtools)
- [Stack Overflow Thread](https://stackoverflow.com/questions/37701330/spring-boot-dev-tools-turning-them-off-for-production)


## Tutorial Repositories:
https://github.com/mosh-hamedani/spring-api-starter
https://github.com/mosh-hamedani/spring-api-finished



## Database Management
Docker Compose used from the following project:
[github.com/joepk90/docker-sql-server](https://github.com/joepk90/docker-sql-server/)

A few changes have been made to the container, services and volume names. 
Also the 
The image has been updated to arm64v8 version.

To start the database, run the following command:
```
make db-start
```
To manage the data base via the browser using Adminer, go to:
[localhost:8080](http://localhost:8090)

Then enter the following properties to login:
- Server: `springboot_rest_api_development_mysql`
- Username: `root`
- Password: `docker_root`

<b>Connect to the mysql server via docker:</b>
```
docker exec -it springboot_mastering_the_fundamentals /bin/bash
```

<b>Login to the mysql server (see root password in `docker-compose.yaml` file):</b>
```
mysql -u root -p
```

### Automatic Databases and Migrations

In `DEV` databases are created automatically due to the `createDatabaseIfNotExist` flag.

In `DEV` database migrations are applied automatically (due to `spring-boot-devtools`). However they can also be applied manually - see the `db-migrate` make command. 

If we want the application to run migrations against the database automatically in `PROD`, the following property can be used:
```
spring.jpa.hibernate.ddl-auto=none # OPTIONS: create, create-drop, validate, update
```
