package com.pe.pivot.auth.infrastructure.adapter.out.persistence;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pe.pivot.auth.domain.model.AuthUser;
import com.pe.pivot.auth.infrastructure.adapter.out.persistence.entity.AuthUserDocument;
import com.pe.pivot.auth.infrastructure.adapter.out.persistence.mapper.AuthUserPersistenceMapper;
import com.pe.pivot.auth.infrastructure.adapter.out.persistence.repository.ReactiveAuthUserMongoRepository;
import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

/**
 * Unit test suite for AuthUserPersistenceAdapter.
 */
class AuthUserPersistenceAdapterTest {

  private ReactiveAuthUserMongoRepository repository;
  private AuthUserPersistenceMapper mapper;
  private AuthUserPersistenceAdapter adapter;

  @BeforeEach
  void setUp() {
    repository = mock(ReactiveAuthUserMongoRepository.class);
    mapper = mock(AuthUserPersistenceMapper.class);
    adapter = new AuthUserPersistenceAdapter(repository, mapper);
  }

  @Test
  @DisplayName("Should be save an AuthUser and return the persisted domain object (happy path)")
  void shouldBeSaveUser() {
    // Arrange
    AuthUser domain = AuthUser.builder().username("john").build();
    AuthUserDocument document = AuthUserDocument.builder().username("john").build();
    AuthUser saved = AuthUser.builder().id("user-1").username("john").build();
    when(mapper.toDocument(domain)).thenReturn(document);
    when(repository.save(document)).thenReturn(Mono.just(document));
    when(mapper.toDomain(document)).thenReturn(saved);

    // Act
    TestObserver<AuthUser> observer = adapter.save(domain).test();

    // Assert
    observer.assertComplete();
    observer.assertNoErrors();
    observer.assertValue(saved);
    verify(repository).save(document);
    verify(mapper).toDomain(document);
  }

  @Test
  @DisplayName("Should be find an AuthUser by username when it exists")
  void shouldBeFindUserByUsername() {
    // Arrange
    AuthUserDocument document = AuthUserDocument.builder().username("john").build();
    AuthUser domain = AuthUser.builder().username("john").build();
    when(repository.findByUsername("john")).thenReturn(Mono.just(document));
    when(mapper.toDomain(document)).thenReturn(domain);

    // Act
    TestObserver<AuthUser> observer = adapter.findByUsername("john").test();

    // Assert
    observer.assertComplete();
    observer.assertValue(domain);
  }

  @Test
  @DisplayName("Should be complete empty when the username does not exist")
  void shouldBeReturnEmptyWhenUsernameDoesNotExist() {
    // Arrange
    when(repository.findByUsername("ghost")).thenReturn(Mono.empty());

    // Act
    TestObserver<AuthUser> observer = adapter.findByUsername("ghost").test();

    // Assert
    observer.assertComplete();
    observer.assertNoValues();
  }

  @Test
  @DisplayName("Should be report username existence from the repository")
  void shouldBeReportUsernameExistence() {
    // Arrange
    when(repository.existsByUsername("john")).thenReturn(Mono.just(true));
    when(repository.existsByUsername("ghost")).thenReturn(Mono.just(false));

    // Act
    TestObserver<Boolean> existingObserver = adapter.existsByUsername("john").test();
    TestObserver<Boolean> missingObserver = adapter.existsByUsername("ghost").test();

    // Assert
    existingObserver.assertValue(true);
    missingObserver.assertValue(false);
  }

  @Test
  @DisplayName("Should be report email existence from the repository")
  void shouldBeReportEmailExistence() {
    // Arrange
    when(repository.existsByEmail("john@mail.com")).thenReturn(Mono.just(true));
    when(repository.existsByEmail("ghost@mail.com")).thenReturn(Mono.just(false));

    // Act
    TestObserver<Boolean> existingObserver = adapter.existsByEmail("john@mail.com").test();
    TestObserver<Boolean> missingObserver = adapter.existsByEmail("ghost@mail.com").test();

    // Assert
    existingObserver.assertValue(true);
    missingObserver.assertValue(false);
  }

  @Test
  @DisplayName("Should be propagate repository error when saving fails")
  void shouldBePropagateSaveError() {
    // Arrange
    AuthUser domain = AuthUser.builder().username("john").build();
    AuthUserDocument document = AuthUserDocument.builder().username("john").build();
    RuntimeException failure = new RuntimeException("write failed");
    when(mapper.toDocument(domain)).thenReturn(document);
    when(repository.save(document)).thenReturn(Mono.error(failure));

    // Act
    TestObserver<AuthUser> observer = adapter.save(domain).test();

    // Assert
    observer.assertError(failure);
  }
}
