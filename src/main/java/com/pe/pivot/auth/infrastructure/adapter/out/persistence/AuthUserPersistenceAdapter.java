package com.pe.pivot.auth.infrastructure.adapter.out.persistence;

import com.pe.pivot.auth.domain.model.AuthUser;
import com.pe.pivot.auth.domain.port.out.AuthUserRepositoryPort;
import com.pe.pivot.auth.infrastructure.adapter.out.persistence.mapper.AuthUserPersistenceMapper;
import com.pe.pivot.auth.infrastructure.adapter.out.persistence.repository.ReactiveAuthUserMongoRepository;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.adapter.rxjava.RxJava3Adapter;

/**
 * Persistence Adapter bridging Reactive MongoDB to RxJava 3 AuthUserRepositoryPort.
 */
@Component
@RequiredArgsConstructor
public class AuthUserPersistenceAdapter implements AuthUserRepositoryPort {

  private final ReactiveAuthUserMongoRepository repository;
  private final AuthUserPersistenceMapper mapper;

  /**
   * Saves an AuthUser to the MongoDB database.
   *
   * @param user - the AuthUser domain object to be saved
   * @return - Single emitting the saved AuthUser domain object
   */
  @Override
  public Single<AuthUser> save(AuthUser user) {
    return RxJava3Adapter.monoToSingle(
            repository.save(mapper.toDocument(user)).map(mapper::toDomain));
  }

  /**
   * Finds an AuthUser by its username in the MongoDB database.
   *
   * @param username - the username of the AuthUser to be retrieved
   * @return - Maybe emitting the found AuthUser or empty if not found
   */
  @Override
  public Maybe<AuthUser> findByUsername(String username) {
    return RxJava3Adapter.monoToMaybe(
            repository.findByUsername(username).map(mapper::toDomain));
  }

  /**
   * Checks if an AuthUser exists by its username in the MongoDB database.
   *
   * @param username - the username to check for existence
   * @return - Single emitting true if the AuthUser exists, false otherwise
   */
  @Override
  public Single<Boolean> existsByUsername(String username) {
    return RxJava3Adapter.monoToSingle(repository.existsByUsername(username));
  }

  /**
   * Checks if an AuthUser exists by its email in the MongoDB database.
   *
   * @param email - the email to check for existence
   * @return - Single emitting true if the AuthUser exists, false otherwise
   */
  @Override
  public Single<Boolean> existsByEmail(String email) {
    return RxJava3Adapter.monoToSingle(repository.existsByEmail(email));
  }
}
