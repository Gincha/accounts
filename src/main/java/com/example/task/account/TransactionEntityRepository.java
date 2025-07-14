package com.example.task.account;

import com.example.task.account.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionEntityRepository extends JpaRepository<TransactionEntity, UUID> {

    Page<TransactionEntity> findAllByAccountId(String accountId, Pageable page);
}
