package com.example.task.account;

import com.example.task.account.entity.AccountEntity;
import com.example.task.account.entity.TransactionEntity;
import com.example.task.rates.CurrencyRatesCache;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AccountsService {

    private final AccountRepository accountRepository;
    private final TransactionEntityRepository transactionEntityRepository;
    private final CurrencyRatesCache currencyRatesCache;

    public List<AccountEntity> fetchAllAccounts(String clientId) {
        return accountRepository.findAllByClientId(clientId);
    }

    public Page<TransactionEntity> fetchAllTransactions(String accountId, int offset, int limit) {
        var page = PageRequest.of(offset / limit, limit, Sort.by("date").descending());
        return transactionEntityRepository.findAllByAccountId(accountId, page);
    }

    @Transactional
    public boolean transferFunds(String fromAccount, String toAccount, String currency, BigDecimal amount) {
        if (fromAccount.equals(toAccount)) {
            throw new IllegalStateException("Should not transfer to same account");
        }
        accountRepository.findById(fromAccount)
                .map(fromAcc -> accountRepository.save(remove(amount, currency, fromAcc)))
                .orElseThrow(() -> new IllegalStateException("Invalid account provided " + fromAccount));
        accountRepository.findById(toAccount)
                .map(toAcc -> {
                    if (currency.equals(toAcc.getCurrency())) {
                        return accountRepository.save(add(amount, toAcc));
                    }
                    throw new IllegalStateException("Receiving account does not support " + currency + " transactions");
                })
                .orElseThrow(() -> new IllegalStateException("Invalid account provided " + toAccount));
        return true;
    }

    private AccountEntity remove(BigDecimal amount, String currency, AccountEntity account) {
        var transferableAmount = calculateTransferableAmount(currency, account, amount);
        if (containsEnoughFunds(account, amount)) {
            var newFromAccBalance = account.getBalance().subtract(transferableAmount);
            account.add(new TransactionEntity(account, BigDecimal.ZERO.subtract(transferableAmount)));
            account.setBalance(newFromAccBalance);
            return account;
        }
        throw new InsufficientFundsException("Insufficient Funds in account " + account);
    }

    private BigDecimal calculateTransferableAmount(String currency, AccountEntity account, BigDecimal amount) {
        if (currency.equals(account.getCurrency())) {
            return amount;
        }
        return amount.divide(currencyRatesCache.currencyRate(account.getCurrency(), currency), RoundingMode.HALF_UP);
    }

    private static AccountEntity add(BigDecimal amount, AccountEntity account) {
        account.add(new TransactionEntity(account, amount));
        account.setBalance(account.getBalance().add(amount));
        return account;
    }

    private static boolean containsEnoughFunds(AccountEntity fromAcc, BigDecimal amount) {
        return fromAcc.getBalance().compareTo(amount) >= 0;
    }
}
