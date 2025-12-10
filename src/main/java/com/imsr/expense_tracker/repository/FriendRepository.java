package com.imsr.expense_tracker.repository;

import com.imsr.expense_tracker.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
}