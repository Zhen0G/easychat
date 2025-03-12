package com.easychat.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.easychat.entity.Group;

public interface GroupRepository extends MongoRepository<Group, String> {
    List<Group> findByMembersContaining(String userId); // 查找用户加入的所有群
}
