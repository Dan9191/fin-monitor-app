package com.example.fin_monitor_app.utils;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.Category;
import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.entity.OperationStatus;
import com.example.fin_monitor_app.entity.TransactionType;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Настройка спецификаций по поиску операций.
 */
public class FinTransactionSpecifications {

    public static Specification<FinTransaction> byBankAccountIds(List<Integer> bankAccountIds) {
        return (root, query, cb) -> {
            if (bankAccountIds == null || bankAccountIds.isEmpty()) {
                return null; // Не применяем фильтрацию, если список пуст
            }

            Join<FinTransaction, BankAccount> bankAccountJoin = root.join("bankAccount");
            return bankAccountJoin.get("id").in(bankAccountIds);
        };
    }

    public static Specification<FinTransaction> dateFrom(LocalDateTime dateFrom) {
        return (root, query, cb) ->
                dateFrom == null ? null : cb.greaterThanOrEqualTo(root.get("createDate"), dateFrom);
    }

    public static Specification<FinTransaction> dateTo(LocalDateTime dateTo) {
        return (root, query, cb) ->
                dateTo == null ? null : cb.lessThanOrEqualTo(root.get("createDate"), dateTo);
    }

    public static Specification<FinTransaction> amountBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null) return cb.between(root.get("sum"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("sum"), min);
            return cb.lessThanOrEqualTo(root.get("sum"), max);
        };
    }

    public static Specification<FinTransaction> amountFrom(BigDecimal amountFrom) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("sum"), amountFrom);
    }

    public static Specification<FinTransaction> amountTo(BigDecimal amountTo) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("sum"), amountTo);
    }

    public static Specification<FinTransaction> hasStatusIds(List<Integer> statusIds) {
        return (root, query, cb) -> {
            if (statusIds == null || statusIds.isEmpty()) {
                return null;
            }
            Join<FinTransaction, OperationStatus> statusJoin = root.join("operationStatus");
            return statusJoin.get("id").in(statusIds);
        };
    }

    public static Specification<FinTransaction> hasCategoryIds(List<Integer> categoryIds) {
        return (root, query, cb) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return null;
            }
            Join<FinTransaction, Category> statusJoin = root.join("category");
            return statusJoin.get("id").in(categoryIds);
        };
    }

    public static Specification<FinTransaction> hasTransactionTypeIds(List<Integer> transactionTypeIds) {
        return (root, query, cb) -> {
            if (transactionTypeIds == null || transactionTypeIds.isEmpty()) {
                return null;
            }
            Join<FinTransaction, TransactionType> statusJoin = root.join("transactionType");
            return statusJoin.get("id").in(transactionTypeIds);
        };
    }

    public static Specification<FinTransaction> bySenderBank(String senderBank) {
        return (root, query, cb) ->
                senderBank == null || senderBank.isEmpty() ? null :
                        cb.like(cb.lower(root.get("senderBank")), "%" + senderBank.toLowerCase() + "%");
    }

    public static Specification<FinTransaction> byRecipientBank(String recipientBank) {
        return (root, query, cb) ->
                recipientBank == null || recipientBank.isEmpty() ? null :
                        cb.like(cb.lower(root.get("recipientBank")), "%" + recipientBank.toLowerCase() + "%");
    }

    public static Specification<FinTransaction> byRecipientTin(String recipientTin) {
        return (root, query, cb) ->
                recipientTin == null || recipientTin.isEmpty() ? null :
                        cb.equal(root.get("recipientTin"), recipientTin);
    }
}