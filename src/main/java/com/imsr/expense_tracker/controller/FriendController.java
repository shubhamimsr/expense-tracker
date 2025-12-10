package com.imsr.expense_tracker.controller;

import com.imsr.expense_tracker.model.Friend;
import com.imsr.expense_tracker.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@CrossOrigin(origins = "http://localhost:3000")
public class FriendController {
    @Autowired
    private FriendService friendService;

    @PostMapping
    public Friend addFriend(@RequestBody Friend friend) {
        return friendService.addFriend(friend);
    }

    @GetMapping
    public List<Friend> getAllFriends() {
        return friendService.getAllFriends();
    }

    @DeleteMapping("/{id}")
    public void deleteFriend(@PathVariable Long id) {
        friendService.deleteFriend(id);
    }
}