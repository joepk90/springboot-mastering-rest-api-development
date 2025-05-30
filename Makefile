# DOCKER_NETWORK = docker-compose network
DOCKER_NETWORK=springboot_mastering_rest_api_network
MYSQL_CONTAINER=springboot_rest_api_development_mysql
SPRING_BOOT_STORE=springboot-store

jwt-secret:
	openssl rand -base64 64

install:
	mvn dependency:resolve

compile:
	rm -rf target && mvn clean compile

# builds clean version of the package using maven (target/store-1.0.0.jar)
build:
	mvn clean package

# runs the java build (untested / env vars required)
run:
	java -jar target/store-1.0.0.jar

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


### STRIPE TESTING

stripe-login:
	stripe login

stripe-listen:
	stripe listen --forward-to http://localhost:8080/checkout/webhook

# TOOD make dynamic
stripe-publish:
	stripe trigger payment_intent.succeeded


# DOCKER

docker-build:
	docker build -t ${SPRING_BOOT_STORE} .

# primarily used for PROD image testing	
docker-run:
	docker run \
	--env-file .env \
	--network ${DOCKER_NETWORK} \
	-p 8080:8080 \
	-e SPRING_PROFILES_ACTIVE=prod \
	-e SPRING_DATASOURCE_URL="jdbc:mysql://dbuser:dbpw@${MYSQL_CONTAINER}:3306/store_api?createDatabaseIfNotExist=true" \
	${SPRING_BOOT_STORE}


