CREATE SEQUENCE currency_rates_id_seq INCREMENT BY 1 START WITH 1;

create table currency_rates (id numeric default nextval('public."currency_rates_id_seq"'),
                            base TEXT,
                            date date,
                            currency TEXT,
                            rate decimal(10,6));

INSERT INTO currency_rates (base, date, currency, rate) values ('EUR', '2025-07-16', 'USD', 1.16830);
INSERT INTO currency_rates (base, date, currency, rate) values ('EUR', '2025-07-16', 'GBP', 0.86);
INSERT INTO currency_rates (base, date, currency, rate) values ('EUR', '2025-07-16', 'SEK', 11.14800);
INSERT INTO currency_rates (base, date, currency, rate) values ('EUR', '2025-07-15', 'GBP', 0.83);
INSERT INTO currency_rates (base, date, currency, rate) values ('EUR', '2025-07-15', 'USD', 1.09);
INSERT INTO currency_rates (base, date, currency, rate) values ('USD', '2025-07-16', 'EUR', 0.8317);
INSERT INTO currency_rates (base, date, currency, rate) values ('USD', '2025-07-16', 'GBP', 1.14);
INSERT INTO currency_rates (base, date, currency, rate) values ('USD', '2025-07-16', 'SEK', 8.14800);
INSERT INTO currency_rates (base, date, currency, rate) values ('USD', '2025-07-15', 'GBP', 1.17);
