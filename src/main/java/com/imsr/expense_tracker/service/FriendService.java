package com.imsr.expense_tracker.service;

import com.imsr.expense_tracker.model.Friend;
import com.imsr.expense_tracker.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FriendService {
    @Autowired
    private FriendRepository friendRepository;

    @Transactional
    public Friend addFriend(Friend friend) {
        return friendRepository.save(friend);
    }

    public List<Friend> getAllFriends() {
        return friendRepository.findAll();
    }

    @Transactional
    public void deleteFriend(Long id) {
        friendRepository.deleteById(id);
    }
}