package com.spring.realworld.core.user;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {

    void save(User user);

    Optional<User> findById(String id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    void saveRelation(FollowRelation followRelation);

    void removeRelation(FollowRelation followRelation);

    Optional<FollowRelation> findRelation(String userId, String targetId);

}