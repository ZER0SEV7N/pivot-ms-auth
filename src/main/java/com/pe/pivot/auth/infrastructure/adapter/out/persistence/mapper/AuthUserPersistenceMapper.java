package com.pe.pivot.auth.infrastructure.adapter.out.persistence.mapper;

import com.pe.pivot.auth.domain.model.AuthUser;
import com.pe.pivot.auth.domain.model.Role;
import com.pe.pivot.auth.infrastructure.adapter.out.persistence.entity.AuthUserDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper converting between AuthUser domain model and AuthUserDocument entity.
 */
@Component
public class AuthUserPersistenceMapper {

  /**
   * Maps an AuthUser domain object to an AuthUserDocument for persistence in MongoDB.
   *
   * @param domain - The AuthUser domain object to be converted.
   * @return - The corresponding AuthUserDocument for MongoDB storage.
   */
  public AuthUserDocument toDocument(AuthUser domain) {
    return AuthUserDocument.builder()
            .id(domain.getId())
            .username(domain.getUsername())
            .email(domain.getEmail())
            .passwordHash(domain.getPasswordHash())
            .role(domain.getRole() != null ? domain.getRole().name() : Role.ROLE_USER.name())
            .customerId(domain.getCustomerId())
            .active(domain.isActive())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
  }

  /**
   * Maps an AuthUserDocument from MongoDB to an AuthUser domain object.
   *
   * @param doc - The AuthUserDocument retrieved from MongoDB.
   * @return - The corresponding AuthUser domain object.
   */
  public AuthUser toDomain(AuthUserDocument doc) {
    return AuthUser.builder()
            .id(doc.getId())
            .username(doc.getUsername())
            .email(doc.getEmail())
            .passwordHash(doc.getPasswordHash())
            .role(doc.getRole() != null ? Role.valueOf(doc.getRole()) : Role.ROLE_USER)
            .customerId(doc.getCustomerId())
            .active(doc.isActive())
            .createdAt(doc.getCreatedAt())
            .updatedAt(doc.getUpdatedAt())
            .build();
  }
}
