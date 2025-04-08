create table users
(
    id                  BIGSERIAL           NOT NULL,
    username            VARCHAR(128) unique not null,
    password            VARCHAR(128)        not null,
    activation_code     varchar(128),
    reset_password_code varchar(128),
    CONSTRAINT users_pk primary key (id)
);

CREATE TABLE roles
(
    id      BIGSERIAL    NOT NULL,
    name    VARCHAR(128) NOT NULL,
    user_id bigint REFERENCES users (id) on delete cascade,
    CONSTRAINT roles_pk PRIMARY KEY (id)
);

create table info_cards
(
    unique_code VARCHAR(128) unique not null,
    user_id     bigint              not null references users (id),
    title       VARCHAR(64)         not null,
    object_type varchar(32) not null,
    required_scope varchar(32) not null,
    info        VARCHAR(255),
    CONSTRAINT info_cards_pk primary key (unique_code)
);

create table info_cards_images
(
    image_path     varchar(255) not null,
    info_card_uuid varchar(128) references info_cards (unique_code) on delete cascade,
    CONSTRAINT info_cards_images_pk primary key (image_path)
);