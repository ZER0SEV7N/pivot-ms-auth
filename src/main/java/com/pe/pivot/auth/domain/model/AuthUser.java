package com.pe.pivot.auth.domain.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Core Domain Model representing an authenticated user in db_pivot_auth.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {

  private String id;
  private String username;
  private String email;
  private String passwordHash;
  private Role role;
  private String customerId;
  private boolean active;
  private Instant createdAt;
  private Instant updatedAt;
}