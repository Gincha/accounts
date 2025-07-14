package com.example.task.account;

import com.example.task.account.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepository extends JpaRepository<AccountEntity, String> {

    @Query(value = "SELECT a.* "
            + " from accounts a "
            + " where client_id=?1 "
            , nativeQuery = true)
    List<AccountEntity> findAllByClientId(String clientId);
}
