package com.pe.pivot.auth.infrastructure.adapter.out.persistence.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.pe.pivot.auth.domain.model.AuthUser;
import com.pe.pivot.auth.domain.model.Role;
import com.pe.pivot.auth.infrastructure.adapter.out.persistence.entity.AuthUserDocument;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test suite for AuthUserPersistenceMapper.
 */
class AuthUserPersistenceMapperTest {

  private AuthUserPersistenceMapper mapper;
  private Instant now;

  @BeforeEach
  void setUp() {
    mapper = new AuthUserPersistenceMapper();
    now = Instant.now();
  }

  @Test
  @DisplayName("Should be map a complete AuthUser to an AuthUserDocument (happy path)")
  void shouldBeMapDomainToDocument() {
    // Arrange
    AuthUser domain = AuthUser.builder()
            .id("user-1")
            .username("john")
            .email("john@mail.com")
            .passwordHash("hash")
            .role(Role.ROLE_ADMIN)
            .customerId("customer-1")
            .active(true)
            .createdAt(now)
            .updatedAt(now)
            .build();

    // Act
    AuthUserDocument document = mapper.toDocument(domain);

    // Assert
    assertEquals("user-1", document.getId());
    assertEquals("john", document.getUsername());
    assertEquals("john@mail.com", document.getEmail());
    assertEquals("hash", document.getPasswordHash());
    assertEquals("ROLE_ADMIN", document.getRole());
    assertEquals("customer-1", document.getCustomerId());
    assertEquals(true, document.isActive());
    assertEquals(now, document.getCreatedAt());
    assertEquals(now, document.getUpdatedAt());
  }

  @Test
  @DisplayName("Should be default role to ROLE_USER when mapping a domain with null role")
  void shouldBeDefaultRoleWhenMappingDomainWithNullRole() {
    // Arrange
    AuthUser domain = AuthUser.builder()
            .username("john")
            .build();

    // Act
    AuthUserDocument document = mapper.toDocument(domain);

    // Assert
    assertEquals("ROLE_USER", document.getRole());
    assertNull(document.getId());
  }

  @Test
  @DisplayName("Should be map a complete AuthUserDocument to an AuthUser")
  void shouldBeMapDocumentToDomain() {
    // Arrange
    AuthUserDocument document = AuthUserDocument.builder()
            .id("user-1")
            .username("john")
            .email("john@mail.com")
            .passwordHash("hash")
            .role("ROLE_ADMIN")
            .customerId("customer-1")
            .active(true)
            .createdAt(now)
            .updatedAt(now)
            .build();

    // Act
    AuthUser domain = mapper.toDomain(document);

    // Assert
    assertEquals("user-1", domain.getId());
    assertEquals("john", domain.getUsername());
    assertEquals("john@mail.com", domain.getEmail());
    assertEquals("hash", domain.getPasswordHash());
    assertEquals(Role.ROLE_ADMIN, domain.getRole());
    assertEquals("customer-1", domain.getCustomerId());
    assertEquals(true, domain.isActive());
    assertEquals(now, domain.getCreatedAt());
    assertEquals(now, domain.getUpdatedAt());
  }

  @Test
  @DisplayName("Should be default role to ROLE_USER when mapping a document with null role")
  void shouldBeDefaultRoleWhenMappingDocumentWithNullRole() {
    // Arrange
    AuthUserDocument document = AuthUserDocument.builder()
            .username("john")
            .build();

    // Act
    AuthUser domain = mapper.toDomain(document);

    // Assert
    assertEquals(Role.ROLE_USER, domain.getRole());
    assertNull(domain.getId());
  }
}
