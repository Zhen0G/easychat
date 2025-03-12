package com.easychat.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.easychat.entity.GroupMessage;

public interface GroupMessageRepository extends MongoRepository<GroupMessage, String> {
    List<GroupMessage> findByGroupIdOrderByTimestampAsc(String groupId);
}
