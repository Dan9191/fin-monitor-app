package com.example.fin_monitor_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Финансовых транзакциях
 */
@Entity
@Table(name = "fin_transactions")
@Data
public class FinTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "operation_status_id", nullable = false)
    private OperationStatus operationStatus;

    @ManyToOne
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    @ManyToOne
    @JoinColumn(name = "transaction_type_id", nullable = false)
    private TransactionType transactionType;

    @Column(name = "commentary", length = 500)
    private String commentary;

    @Column(name = "sum", nullable = false, precision = 15, scale = 5)
    private BigDecimal sum;

    @Column(name = "sender_bank", length = 100)
    private String senderBank;

    @Column(name = "recipient_bank", length = 100)
    private String recipientBank;

    @Column(name = "recipient_tin", length = 20)
    private String recipientTin;

    @Column(name = "recipient_bank_account", length = 20)
    private String recipientBankAccount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "recipient_telephone_number", length = 20)
    private String recipientTelephoneNumber;
}
