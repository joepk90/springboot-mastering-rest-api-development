install:
	mvn dependency:resolve

# use maven to run the springboot application
dev:
	./mvnw spring-boot:run

db-start:
	docker-compose up -d

db-stop:
	docker-compose down

db-delete:
	docker-compose down -v

# see plugins for other migration flyway commands (Section in VSCode beneath Java Projects)
db-migrate:
	mvn flyway:migrate

# uses the SchemaGenerator class to generate migration files based of entities, compared to the existing database schema
# note: the SpringApplication.run method must be invoked (enabled / not commented out) in the StoreApplication class
db-migration-generate:
	./mvnw spring-boot:run \
	-Dspring-boot.run.profiles=schema-gen \
	-Dspring-boot.run.arguments="--spring.main.web-application-type=none"

# -Dspring-boot.run.main-class=com.springbootfundamentals.store.SchemaGenerator \ # can be run a seperate package (the SpringBootApplication annotation must added to the SchemaGenerator class) 