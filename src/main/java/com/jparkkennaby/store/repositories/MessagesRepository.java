package com.jparkkennaby.store.repositories;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;

// import com.jparkkennaby.store.annoations.MaxTableSizeCheck;
import com.jparkkennaby.store.entities.Message;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The RepositoryRestResource annotation exposes a message endpoint.
 * 
 * This endoint has all the default GET, POST, PUT and DELETE methods,
 * as well as pagination, and the security can be controlled in the
 * MessageSecurityRules class.
 * 
 * It does not however, show up in Swagger UI - I'm guessing due to the
 * complexities that pagination brings... But perhaps there are other
 * reasons for this as well.
 * 
 * It could be worthwhile exploring ths further, or look into creating an
 * Aspect that creates the default GET, POST, PUT and DELETE without pagination
 * that does get show in Swagger UI.
 */

// @MaxTableSizeCheck(entity = Message.class) // MAX TABLE LIMIT NOT WORKING
@RepositoryRestResource
public interface MessagesRepository extends JpaRepository<Message, Long> {
}
