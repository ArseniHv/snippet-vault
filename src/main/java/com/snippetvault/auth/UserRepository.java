package com.snippetvault.auth;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<UserDocument, String> {
    Optional<UserDocument> findByUsername(String username);
    Optional<UserDocument> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}