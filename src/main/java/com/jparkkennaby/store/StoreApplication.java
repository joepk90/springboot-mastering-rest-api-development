package com.jparkkennaby.store;

import org.springdoc.core.configuration.SpringDocDataRestConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

// SpringDocDataRestConfiguration import required for RepositoryRestResource implemetation.
// SpringDocDataRestConfiguration import resolves swagger ui issues, however it does not show
// endpoints created with the RepositoryRestResource annitaiotn in swagger ui (likely due to pagination complexities)
@Import(SpringDocDataRestConfiguration.class)
@SpringBootApplication
public class StoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreApplication.class, args);
    }
}
