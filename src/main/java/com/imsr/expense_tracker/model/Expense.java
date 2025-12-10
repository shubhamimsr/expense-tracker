package com.imsr.expense_tracker.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Friend paidBy;

    private BigDecimal amount;

    private String description;

    private LocalDateTime date = LocalDateTime.now();

    @ManyToMany
    private List<Friend> participants;
}
