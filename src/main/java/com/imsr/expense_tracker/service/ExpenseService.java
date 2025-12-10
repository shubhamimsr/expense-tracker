package com.imsr.expense_tracker.service;

import com.imsr.expense_tracker.model.Expense;
import com.imsr.expense_tracker.model.Friend;
import com.imsr.expense_tracker.repository.ExpenseRepository;
import com.imsr.expense_tracker.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final FriendRepository friendRepository;

    @Transactional
    public Expense addExpense(Expense expense) {

        // 🔥 1. Attach paidBy Friend entity properly
        Friend payer = friendRepository.findById(expense.getPaidBy().getId())
                .orElseThrow(() -> new RuntimeException("Payer not found"));
        expense.setPaidBy(payer);

        // 🔥 2. Attach participants list properly
        List<Friend> attachedParticipants = expense.getParticipants().stream()
                .map(p -> friendRepository.findById(p.getId())
                        .orElseThrow(() -> new RuntimeException("Friend not found: " + p.getId())))
                .toList();

        expense.setParticipants(attachedParticipants);

        // 🔥 3. Save final expense
        return expenseRepository.save(expense);
    }

    @Transactional
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    @Transactional
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    public List<Map<String, Object>> settleExpenses() {
        List<Expense> expenses = getAllExpenses();
        Map<Long, BigDecimal> balances = new HashMap<>();

        for (Expense exp : expenses) {
            BigDecimal share = exp.getAmount().divide(BigDecimal.valueOf(exp.getParticipants().size()), 2, RoundingMode.HALF_UP);

            balances.merge(exp.getPaidBy().getId(), exp.getAmount(), BigDecimal::add);

            for (Friend part : exp.getParticipants()) {
                balances.merge(part.getId(), share.negate(), BigDecimal::add);
            }
        }

        List<Map.Entry<Long, BigDecimal>> creditors = new ArrayList<>();
        List<Map.Entry<Long, BigDecimal>> debtors = new ArrayList<>();

        for (Map.Entry<Long, BigDecimal> entry : balances.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(entry);
            } else if (entry.getValue().compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(entry);
            }
        }

        creditors.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        debtors.sort(Comparator.comparing(Map.Entry::getValue));

        List<Map<String, Object>> transactions = new ArrayList<>();
        int i = 0, j = 0;

        while (i < creditors.size() && j < debtors.size()) {
            BigDecimal credAmount = creditors.get(i).getValue();
            BigDecimal debtAmount = debtors.get(j).getValue().abs();

            BigDecimal transfer = credAmount.min(debtAmount);

            Long fromId = debtors.get(j).getKey();
            Long toId = creditors.get(i).getKey();

            Map<String, Object> tx = new HashMap<>();
            tx.put("fromId", fromId);
            tx.put("fromName", friendRepository.findById(fromId).get().getName());
            tx.put("toId", toId);
            tx.put("toName", friendRepository.findById(toId).get().getName());
            tx.put("amount", transfer);

            transactions.add(tx);

            creditors.get(i).setValue(credAmount.subtract(transfer));
            debtors.get(j).setValue(debtors.get(j).getValue().add(transfer));

            if (creditors.get(i).getValue().compareTo(BigDecimal.ZERO) == 0) i++;
            if (debtors.get(j).getValue().compareTo(BigDecimal.ZERO) == 0) j++;
        }

        return transactions;
    }

}