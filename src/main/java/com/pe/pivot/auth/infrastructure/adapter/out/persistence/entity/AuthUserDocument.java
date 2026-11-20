package com.pe.pivot.auth.infrastructure.adapter.out.persistence.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document representing an AuthUser in db_pivot_auth.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "auth_users")
public class AuthUserDocument {

  @Id
  private String id;

  @Indexed(unique = true)
  private String username;

  @Indexed(unique = true)
  private String email;

  private String passwordHash;
  private String role;
  private String customerId;
  private boolean active;
  private Instant createdAt;
  private Instant updatedAt;
}
