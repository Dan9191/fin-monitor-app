package com.example.fin_monitor_app.repository;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    List<BankAccount> findAllByUser(User user);

    BankAccount findByAccountName(String bankAccountName);
}
