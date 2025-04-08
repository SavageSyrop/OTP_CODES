create table users
(
    id                  BIGSERIAL           NOT NULL,
    email               VARCHAR(128) unique not null,
    phone_number        VARCHAR(128) unique not null,
    tg_id               VARCHAR(128) unique not null,
    password            VARCHAR(128)        not null,
    role                VARCHAR(128)        not null,
    CONSTRAINT users_pk primary key (id)
);


create or replace function check_single_admin()
RETURNS trigger AS $$
begin
    -- Проверяем количество записей в таблице
    if (select count(*) from users where role = 'ADMIN') >= 1 then
        raise exception 'Таблица может содержать только одну запись админа';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

create trigger enforce_single_record_admin
before insert or update on users
for each row EXECUTE function check_single_admin();

create TABLE otp_config
(
    config_version      VARCHAR(128) unique not null,
    otp_code_length      bigint not null,
    exipes_after_millis  bigint not null,
    CONSTRAINT otp_config_pk primary key (config_version)
);

create or replace function check_single_config()
RETURNS trigger AS $$
begin
    -- Проверяем количество записей в таблице
    if (select count(*) from otp_config) >= 1 then
        raise exception 'Таблица может содержать только одну запись конфига';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

create trigger enforce_single_record_config
before insert or update on otp_config
for each row EXECUTE function check_single_config();

create table otp_codes
(
    id                  BIGSERIAL           NOT NULL,
    otp_code            VARCHAR(128) unique not null,
    otp_code_status     VARCHAR(128)        not null,
    user_id bigint REFERENCES users (id) on delete cascade,
    CONSTRAINT otp_codes_pk primary key (id)
);

