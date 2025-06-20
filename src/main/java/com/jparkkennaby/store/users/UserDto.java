package com.jparkkennaby.store.users;

// import java.time.LocalDateTime;
// import com.fasterxml.jackson.annotation.JsonFormat;
// import com.fasterxml.jackson.annotation.JsonInclude;
// import com.fasterxml.jackson.annotation.JsonIgnore;
// import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data Transfer Objets (DTO)
 * 
 * DTO's are used to manage the fields we want to expose in our public facing
 * REST API
 */

@Getter
@AllArgsConstructor
public class UserDto {
    // @JsonIgnore // ignore the annotated field
    // @JsonProperty("user_id") // rename the annotated field
    private Long id;
    private String name;
    private String email;

    // @JsonInclude(JsonInclude.Include.NON_NULL) // exlcude null fields from
    // response
    // private String phoneNumber;

    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    // private LocalDateTime createdAt; // filed populated in the UserMapper class
}
