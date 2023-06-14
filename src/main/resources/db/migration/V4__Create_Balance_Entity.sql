CREATE TABLE balance
(
    id             bigserial primary key,
    date           date           not null,
    balance_amount numeric(38, 2) not null
);