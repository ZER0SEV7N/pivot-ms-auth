package com.pe.pivot.auth.infrastructure.adapter.out.persistence.repository;

import com.pe.pivot.auth.infrastructure.adapter.out.persistence.entity.AuthUserDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ReactiveAuthUserMongoRepository
        extends ReactiveMongoRepository<AuthUserDocument, String> {

  /**
   * Finds an AuthUserDocument by its username.
   *
   * @param username - the username to search for
   * @return - a Mono emitting the found AuthUserDocument, or empty if not found
   */
  Mono<AuthUserDocument> findByUsername(String username);

  /**
   * Checks if an AuthUserDocument exists with the specified username.
   *
   * @param username - the username to check for existence
   * @return - a Mono emitting true if a document exists, false otherwise
   */
  Mono<Boolean> existsByUsername(String username);

  /**
   * Checks if an AuthUserDocument exists with the specified email.
   *
   * @param email - the email to check for existence
   * @return - a Mono emitting true if a document exists, false otherwise
   */
  Mono<Boolean> existsByEmail(String email);
}
