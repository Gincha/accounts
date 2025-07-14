package com.example.task.api;

import com.example.task.account.entity.AccountEntity;
import com.example.task.account.AccountsService;
import com.example.task.account.entity.TransactionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountsController.class)
class AccountsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountsService accountsService;

    @Test
    void whenGetAccountsSuccess_200OK() throws Exception {
        var accountEntity = new AccountEntity();
        accountEntity.setCurrency("EUR");
        accountEntity.setBalance(BigDecimal.TEN);

        when(accountsService.fetchAllAccounts("clientId"))
                .thenReturn(List.of(accountEntity));

        var request = MockMvcRequestBuilders
                .get("/accounts/clientId")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void whenGetTransactionsSuccess_200OK() throws Exception {
        var accountId = "accountABC";
        var transactionEntity = new TransactionEntity();
        transactionEntity.setAccount(new AccountEntity());

        when(accountsService.fetchAllTransactions(accountId, 0, 5))
                .thenReturn(new PageImpl<>(List.of(transactionEntity), PageRequest.of(0, 5), 1));

        var request = MockMvcRequestBuilders
                .get("/accounts/" + accountId + "/transactions")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void whenTransferFundsSuccessful_200OK() throws Exception {
        when(accountsService.transferFunds("accA", "accB", "XXX", BigDecimal.ZERO))
                .thenReturn(true);

        var request = MockMvcRequestBuilders
                .post("/accounts/transfer")
                .accept(MediaType.APPLICATION_JSON)
                .content("""
                        {"fromAccount":"A","toAccount":"B","currency":"EUR","amount":10.0}
                        """)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void whenInvalidRequest_negativeTransferAmount_400BadRequest() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/accounts/transfer")
                .accept(MediaType.APPLICATION_JSON)
                .content("""
                        {"fromAccount":"A","toAccount":"B","currency":"EUR","amount":-10.0}
                        """)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void whenTransferNotSuccessful_500Error() throws Exception {
        when(accountsService.transferFunds(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Test exception"));

        var request = MockMvcRequestBuilders
                .post("/accounts/transfer")
                .accept(MediaType.APPLICATION_JSON)
                .content("""
                         {"fromAccount":"A","toAccount":"A","currency":"eur","amount":10.0}
                        """)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andReturn();
    }
}