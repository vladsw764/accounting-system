create table debts
(
    id               bigserial      not null,
    return_amount    numeric(38, 2) not null,
    received_amount  numeric(38, 2) not null,
    category         varchar(40)    not null,
    debt_status      boolean        not null,
    end_date         timestamp(6),
    start_date       timestamp(6),
    periodic_payment float(53),
    reminder         varchar(1000),
    primary key (id)
);

create table payments
(
    id      bigserial      not null,
    amount  numeric(38, 2) not null,
    date    timestamp(6),
    debt_id bigint,
    primary key (id)
);

create table transactions
(
    id       bigserial not null,
    amount   numeric(38, 2),
    category varchar(40),
    date     timestamp(6),
    comment  varchar(1000),
    primary key (id)
);

alter table if exists payments
    add constraint FK_PAYMENTS_DEBTS foreign key (debt_id) references debts;