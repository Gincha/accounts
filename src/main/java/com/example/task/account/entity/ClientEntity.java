package com.example.task.account.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "clients")
public class ClientEntity {

    @Id
    private String id;

    @OneToMany(mappedBy = "clientId")
    private List<AccountEntity> accounts;
}
