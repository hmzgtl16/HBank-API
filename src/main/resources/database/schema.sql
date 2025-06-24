create table if not exists table_address
(
    address_id           uuid not null
        primary key,
    address_municipality varchar(255),
    address_province     varchar(255),
    address_street       varchar(255),
    address_zip          varchar(255)
);

create table if not exists table_file
(
    file_id   uuid         not null
        primary key,
    file_name varchar(255) not null,
    file_type varchar(255) not null,
    file_data oid          not null
);

create table if not exists table_privilege
(
    privilege_id   uuid         not null
        primary key,
    privilege_name varchar(255) not null
        unique
);

create table if not exists table_role
(
    role_id   uuid         not null
        primary key,
    role_name varchar(255) not null
        unique
);

create table if not exists table_role_privilege
(
    privilege_id uuid not null
        constraint fk5te72p3js3dj4js7r4of5ty89
            references table_privilege,
    role_id      uuid not null
        constraint fk53vr45gkyf2pjub52u9nb1moa
            references table_role,
    primary key (privilege_id, role_id)
);

create table if not exists table_token
(
    token_created_at timestamp(6) with time zone not null,
    token_id         uuid                        not null
        primary key,
    token_value      varchar(255)                not null
        unique
);

create table if not exists table_user
(
    user_enabled      boolean                     not null,
    user_created_at   timestamp(6) with time zone not null,
    user_modified_at  timestamp(6) with time zone not null,
    user_id           uuid                        not null
        primary key,
    user_email        varchar(255)                not null
        unique,
    user_password     varchar(255)                not null,
    user_phone_number varchar(255)
        unique,
    user_username     varchar(255)                not null
        unique
);

create table if not exists table_customer
(
    customer_birthdate   date,
    customer_verified    boolean                     not null,
    customer_created_at  timestamp(6) with time zone not null,
    customer_modified_at timestamp(6) with time zone not null,
    customer_address_id  uuid
        unique
        constraint fkhlb21wgp03upi6xvhuqra853f
            references table_address,
    customer_file_id     uuid
        unique
        constraint fk3umq9xx1iqit19nt9o7xeyfx6
            references table_file,
    customer_id          uuid                        not null
        primary key,
    customer_user_id     uuid                        not null
        unique
        constraint fksmxrqi6hvj9fd2jtvrr0s2hof
            references table_user,
    customer_first_name  varchar(255),
    customer_last_name   varchar(255)
);

create table if not exists table_account
(
    account_balance     double precision            not null,
    account_limit       smallint                    not null
        constraint table_account_account_limit_check
            check ((account_limit >= 0) AND (account_limit <= 2)),
    account_status      smallint                    not null
        constraint table_account_account_status_check
            check ((account_status >= 0) AND (account_status <= 2)),
    account_type        smallint                    not null
        constraint table_account_account_type_check
            check ((account_type >= 0) AND (account_type <= 2)),
    account_created_at  timestamp(6) with time zone not null,
    account_modified_at timestamp(6) with time zone not null,
    account_customer_id uuid                        not null
        unique
        constraint fk62jfsw6uac07wkbl9n9c4mt2j
            references table_customer,
    account_id          uuid                        not null
        primary key,
    account_name        varchar(255)                not null,
    account_number      varchar(255)                not null
        unique
);

create table if not exists table_account_token
(
    account_id uuid not null
        constraint fkja3mixbos87ttk1rk8qyptyxh
            references table_account,
    token_id   uuid not null
        constraint fksbi0aixmyqjj4wd6wx45g77k
            references table_token on delete cascade,
    primary key (account_id, token_id)
);

create table if not exists table_transaction
(
    transaction_amount      double precision,
    transaction_fees        double precision,
    transaction_status      smallint
        constraint table_transaction_transaction_status_check
            check ((transaction_status >= 0) AND (transaction_status <= 5)),
    transaction_type        smallint
        constraint table_transaction_transaction_type_check
            check ((transaction_type >= 0) AND (transaction_type <= 1)),
    transaction_created_at  timestamp(6) with time zone,
    transaction_modified_at timestamp(6) with time zone,
    transaction_from        uuid not null
        constraint fkki5t2i6qyj0rhjtt7tedod2qv
            references table_account,
    transaction_id          uuid not null
        primary key,
    transaction_reference   uuid not null
        unique,
    transaction_to          uuid not null
        constraint fk3j29v83v36vnkjxqxt5aaimsu
            references table_account
);

create index if not exists table_transaction_transaction_from_index
    on table_transaction (transaction_from);

create index if not exists table_transaction_transaction_to_index
    on table_transaction (transaction_to);

create table if not exists table_user_role
(
    role_id uuid not null
        constraint fk5c1kle5ek797ah7xymqdaodo7
            references table_role,
    user_id uuid not null
        constraint fkmlh9fb3bfwvirrh616p1bhjj7
            references table_user,
    primary key (role_id, user_id)
);

create table if not exists table_transaction_token
(
    transaction_id uuid not null
        constraint fkrmbncyg8rf71o59lwkc3hfs6v
            references table_transaction,
    token_id       uuid not null
        constraint fkgy2qax8xax3o8mf0g1w7tv0i0
            references table_token on delete cascade,
    primary key (transaction_id, token_id)
);

create table if not exists table_user_token
(
    type     smallint
        constraint table_user_token_type_check
            check ((type >= 0) AND (type <= 1)),
    token_id uuid not null
        constraint fkioiwf2fpiqw6pg0qy94r15ml6
            references table_token on delete cascade,
    user_id  uuid not null
        constraint fk1468cfqdfxf48bdlnl8rg5yfp
            references table_user,
    primary key (token_id, user_id)
);

