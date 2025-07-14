create table clients (id TEXT PRIMARY KEY);

create table accounts (id TEXT PRIMARY KEY,
                     client_id TEXT NOT NULL,
                     currency TEXT,
                     balance DECIMAL(10, 2),
                     constraint fk_accounts_clients
                        foreign key (client_id)
                        REFERENCES clients (id)
                          );

create table transactions (id UUID PRIMARY KEY,
                     account_id TEXT NOT NULL,
                     amount DECIMAL(10, 2),
                     date date,
                     constraint fk_transactions_accounts
                        foreign key (account_id)
                        REFERENCES accounts (id));

insert into clients values ('clientA');
insert into clients values ('clientB');

insert into accounts values ('A', 'clientA', 'EUR', 15.23);
insert into accounts values ('B', 'clientB', 'EUR', 0.01);
insert into accounts values ('C', 'clientA', 'USD', 135.0);

insert into transactions values ('18f8eebb-b8cf-4bc2-98b6-a0f03d7525dd', 'B', 0.01, '2025-07-01');
insert into transactions values ('17f8eebb-b8cf-4bc2-98b6-a0f03d7525dd', 'B', 0.10, '2025-07-14');
insert into transactions values ('16f8eebb-b8cf-4bc2-98b6-a0f03d7525dd', 'B', 15, '2025-07-05');
insert into transactions values ('27f8eebb-b8cf-4bc2-98b6-a0f03d7525dd', 'B', 2.64, '2025-07-15');
insert into transactions values ('37f8eebb-b8cf-4bc2-98b6-a0f03d7525dd', 'A', 0.01, '2025-06-14');
insert into transactions values ('27f8eebb-b8cf-4bc2-98b6-a0f03d7525db', 'B', 3.33, '2025-06-25');
insert into transactions values ('27f8eebb-b8cf-4bc2-98b6-a0f03d7525da', 'B', 23.65, '2025-07-12');