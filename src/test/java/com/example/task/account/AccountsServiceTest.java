package com.example.task.account;

import com.example.task.account.entity.AccountEntity;
import com.example.task.rates.CurrencyRatesCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountsServiceTest {

    @Mock
    AccountRepository accountRepoMock;
    @Mock
    TransactionEntityRepository transactionEntityRepoMock;
    @Mock
    CurrencyRatesCache currencyRatesCacheMock;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenFromAccountDoesNotExist_throwIllegalStateException() {
        when(accountRepoMock.findById("accA")).thenReturn(Optional.empty());

        var accountService = new AccountsService(accountRepoMock, transactionEntityRepoMock, currencyRatesCacheMock);

        assertThrows(IllegalStateException.class, () ->
                accountService.transferFunds("accA", "accB", "EUR", BigDecimal.valueOf(5.25)));
    }

    @Test
    void whenTransferingMoreThanFromAccountBalance_throwIllegalStateException() {
        when(accountRepoMock.findById("accA")).thenReturn(Optional.of(new AccountEntity()));
        when(currencyRatesCacheMock.currencyRate(any(), any())).thenReturn(BigDecimal.ONE);

        var accountService = new AccountsService(accountRepoMock, transactionEntityRepoMock, currencyRatesCacheMock);

        assertThrows(InsufficientFundsException.class, () ->
                accountService.transferFunds("accA", "accB", "EUR", BigDecimal.valueOf(5.25)));
    }

    @Test
    void whenToAccountDoesNotExist_throwIllegalStateException() {
        var accountEntity = new AccountEntity();
        accountEntity.setBalance(BigDecimal.TEN);
        when(accountRepoMock.findById("accA")).thenReturn(Optional.of(accountEntity));
        when(accountRepoMock.findById("accB")).thenReturn(Optional.empty());
        when(currencyRatesCacheMock.currencyRate(any(), any())).thenReturn(BigDecimal.ONE);

        var accountService = new AccountsService(accountRepoMock, transactionEntityRepoMock, currencyRatesCacheMock);

        assertThrows(IllegalStateException.class, () ->
                accountService.transferFunds("accA", "accB", "EUR", BigDecimal.valueOf(5.25)));
    }

    @Test
    void whenTransferCurrencyIsSameAsToAccountCurrency_transferFunds() {
        var fromAccountEntity = new AccountEntity();
        fromAccountEntity.setCurrency("EUR");
        fromAccountEntity.setBalance(BigDecimal.TEN);

        var toAccountEntity = new AccountEntity();
        toAccountEntity.setBalance(BigDecimal.ZERO);
        toAccountEntity.setCurrency("EUR");

        var fromAccount = "accA";
        when(accountRepoMock.findById(fromAccount)).thenReturn(Optional.of(fromAccountEntity));
        when(accountRepoMock.save(any())).thenReturn(new AccountEntity());

        var toAccount = "accB";
        when(accountRepoMock.findById(toAccount)).thenReturn(Optional.of(toAccountEntity));
        when(currencyRatesCacheMock.currencyRate(any(), any())).thenReturn(BigDecimal.ONE);

        var accountService = new AccountsService(accountRepoMock, transactionEntityRepoMock, currencyRatesCacheMock);

        var result = accountService.transferFunds(fromAccount, toAccount, "EUR", BigDecimal.valueOf(5.25));

        assertTrue(result);
        assertEquals(BigDecimal.valueOf(4.75), fromAccountEntity.getBalance());
        assertEquals(BigDecimal.valueOf(5.25), toAccountEntity.getBalance());
        verify(accountRepoMock).save(fromAccountEntity);
        verify(accountRepoMock).save(toAccountEntity);
    }

    @Test
    void whenTransferCurrencyIsNotSameAsToAccountCurrency_throwIllegalStateException() {
        var fromAccountEntity = new AccountEntity();
        fromAccountEntity.setBalance(BigDecimal.TEN);

        var toAccountEntity = new AccountEntity();
        toAccountEntity.setBalance(BigDecimal.ZERO);
        toAccountEntity.setCurrency("USD");

        var fromAccount = "accA";
        when(accountRepoMock.findById(fromAccount)).thenReturn(Optional.of(fromAccountEntity));
        when(accountRepoMock.save(any())).thenReturn(new AccountEntity());

        var toAccount = "accB";
        when(accountRepoMock.findById(toAccount)).thenReturn(Optional.of(toAccountEntity));
        when(currencyRatesCacheMock.currencyRate(any(), any())).thenReturn(BigDecimal.ONE);

        var accountService = new AccountsService(accountRepoMock, transactionEntityRepoMock, currencyRatesCacheMock);

        assertThrows(IllegalStateException.class, () ->
                accountService.transferFunds(fromAccount, toAccount, "EUR", BigDecimal.valueOf(5.25)));
    }
}