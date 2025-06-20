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

### VSCode Java/Maven Compilation Issues
if `make compile` command does not resolve `java`/`maven` compilcation issues, try cleaning the Java Language Server Workspace through VSCode.

Open the command palette (cmd + shift + p) and run:
```
Java: Clean Java Language Server Workspace
```


# Setup Steps 
Steps to setup the project.

Firstly clean the Java Language Server Workspace through VSCode - See section above ☝️

Then run:
```
make install
make compile
make dev
```
**Note:**
Let each command complete before running the next. Also the `make dev` command may not work on the first try...


# API Documentation (Swagger)
To see the availabe API endpoints, go to: [localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).

For more deails, see the documentation at [springdoc.org](https://springdoc.org/#swagger-ui-configuration).


# Application Configuration

## JWT Secret
An encryption key can be generated at [generate-random.org/encryption-key-generator](https://generate-random.org/encryption-key-generator).

Or use `openssl`:
```
make jwt-secret
```

## JWT Inspection
To inspect a JWT token the folling websote can be used: [jwt.io](https://jwt.io)


# How to Use/Test the API

## Testing
To get an understanding of the basic endpoints and the payment flow, see the following [testing.md](https://github.com/joepk90/springboot-mastering-rest-api-development/blob/main/testing.md) file.

## Stripe
A webhook is used to manage the Stripe checkout flow. See the [stripe-testing.md](https://github.com/joepk90/springboot-mastering-rest-api-development/blob/main/stripe-testing.md) file for more info.

The Webhook Signing Secret can be found here:
[dashboard.stripe.com/test/webhooks/](https://dashboard.stripe.com/test/webhooks/we_1RVFedF99b23CaumS8hFrzF9)


# Seeding the Database
To seed the database with a default dataset (categories, products, users), make a `GET` request to the Seed endpoint:
```
/seed
```

The data is generated using Faker. A single User is created that can be used to authenticate:
- Email: johnsmith@gmail.com
- Password: 123456



