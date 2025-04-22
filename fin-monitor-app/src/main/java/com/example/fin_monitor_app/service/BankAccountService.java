package com.example.fin_monitor_app.service;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.model.PersonTypeEnum;
import com.example.fin_monitor_app.repository.BankAccountRepository;
import com.example.fin_monitor_app.service.cache.PersonTypeCacheService;
import com.example.fin_monitor_app.view.CreateBankAccountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сервис по работе с счетами.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    private final PersonTypeCacheService personTypeCacheService;


    @Transactional
    public void save(CreateBankAccountDto createBankAccountDto, User user) {
        PersonTypeEnum personTypeEnum = createBankAccountDto.getPersonType();
        BankAccount account = new BankAccount();
        account.setUser(user);
        account.setAccountName(createBankAccountDto.getBankAccountName());
        account.setPersonType(personTypeCacheService.findById(personTypeEnum.getId()));
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(createBankAccountDto.getBalance() != null ?
                createBankAccountDto.getBalance() : BigDecimal.ZERO);
        log.info("save bank account: {} for {} ", account.getAccountName(), user.getName());
        bankAccountRepository.save(account);
    }

    @Transactional
    public void delete(BankAccount bankAccount) {
        log.info("delete bank account: {}", bankAccount.getAccountName());
        bankAccountRepository.delete(bankAccount);
    }

    public List<BankAccount> getBankAccounts(User user) {
        return bankAccountRepository.findAllByUser(user);
    }

    public BankAccount getBankAccountById(Integer id) {
        return bankAccountRepository.findById(id);
    }


    private String generateAccountNumber() {
        // Генерация номера счета (реализуйте по своему усмотрению)
        return "ACC" + System.currentTimeMillis();
    }
}
