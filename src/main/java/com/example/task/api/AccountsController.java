package com.example.task.api;

import com.example.task.account.entity.AccountEntity;
import com.example.task.account.AccountsService;
import com.example.task.account.entity.TransactionEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
public class AccountsController {

    private final AccountsService accountsService;

    @GetMapping("/{clientId}")
    public ResponseEntity<GetAccountsResponse> getAccounts(@PathVariable("clientId") String clientId) {
        return ResponseEntity.ok(
                new GetAccountsResponse(
                        accountsService.fetchAllAccounts(clientId),
                        clientId)
        );
    }

    public record GetAccountsResponse(String clientId,
                                      List<AccountResponse> accounts) {

        public GetAccountsResponse(List<AccountEntity> clientAccounts, String clientId) {
            this(clientId, clientAccounts.stream()
                    .map(account ->
                            new GetAccountsResponse.AccountResponse(
                                    account.getId(),
                                    account.getCurrency(),
                                    account.getBalance()))
                    .toList());
        }

        public record AccountResponse(String id,
                                      String currency,
                                      BigDecimal balance) {
        }
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<GetTransactionsResponse> getTransactions(@PathVariable("accountId") String accountId,
                                                                   @RequestParam(name = "offset", defaultValue = "0") int offset,
                                                                   @RequestParam(name = "limit", defaultValue = "5") int limit) {
        return ResponseEntity.ok(
                new GetTransactionsResponse(accountId,
                        accountsService.fetchAllTransactions(accountId, offset, limit))
        );
    }

    public record GetTransactionsResponse(String accountId,
                                          int page,
                                          int pageSize,
                                          int totalPages,
                                          List<Transaction> transactions) {

        public GetTransactionsResponse(String accountId, Page<TransactionEntity> page) {
            this(accountId,
                    page.getPageable().getPageNumber() + 1,
                    page.getPageable().getPageSize(),
                    page.getTotalPages(),
                    convert(page));
        }

        private static List<Transaction> convert(Page<TransactionEntity> page) {
            return page.stream()
                    .map(trx -> Transaction.builder()
                            .id(trx.getId().toString())
                            .accountId(trx.getAccount().getId())
                            .amount(trx.getAmount())
                            .date(trx.getDate())
                            .build())
                    .toList();
        }

        @Builder
        public record Transaction(String id,
                                  String accountId,
                                  BigDecimal amount,
                                  LocalDate date) {
        }

    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestBody @Valid TransferRequest request) {
        try {
            accountsService.transferFunds(request.fromAccount(), request.toAccount(), request.currency(), request.amount());
            return ResponseEntity.ok(new TransferResponse("Success"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new TransferResponse("Bad luck. " + e.getMessage()));
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TransferRequest(@NotBlank String fromAccount, @NotBlank String toAccount,
                                  @NotBlank String currency, @NotNull BigDecimal amount) {

        public TransferRequest {
            validate(amount);
            fromAccount = ofNullable(fromAccount).map(String::toUpperCase).orElseThrow();
            toAccount = ofNullable(toAccount).map(String::toUpperCase).orElseThrow();
            currency = ofNullable(currency).map(String::toUpperCase).orElseThrow();
        }

        private BigDecimal validate(BigDecimal amount) {
            if (amount.signum() <= 0) {
                throw new RuntimeException("Invalid transfer amount " + amount);
            }
            return amount;
        }

    }

    public record TransferResponse(String message) {

    }

}
