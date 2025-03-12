package com.easychat.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.easychat.entity.Friend;

public interface FriendRepository extends MongoRepository<Friend, String> {
    List<Friend> findByUserId(String userId);
    boolean existsByUserIdAndFriendId(String userId, String friendId);
    void deleteByUserIdAndFriendId(String userId, String friendId);
}
