package com.pe.pivot.auth.domain.port.out;

import com.pe.pivot.auth.domain.model.AuthUser;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

/**
 * Output port for persisting and querying AuthUser credentials in MongoDB.
 */
public interface AuthUserRepositoryPort {

  Single<AuthUser> save(AuthUser user);

  Maybe<AuthUser> findByUsername(String username);

  Single<Boolean> existsByUsername(String username);

  Single<Boolean> existsByEmail(String email);
}