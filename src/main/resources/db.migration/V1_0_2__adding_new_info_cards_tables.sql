alter table info_cards
add info_cards_info_typed_id bigint;

create table info_cards_person_info
(
    id      BIGSERIAL    NOT NULL,
    phone_number varchar(15),
    email varchar(15),
    telegram varchar(255),
    CONSTRAINT info_cards_person_info_pk primary key (id)
);

create table info_cards_place_info
(
    id      BIGSERIAL    NOT NULL,
    address varchar(255),
    working_hours varchar(255),
    phone_number varchar(15),
    CONSTRAINT info_cards_place_info_pk primary key (id)
);

create table info_cards_object_info
(
    id      BIGSERIAL    NOT NULL,
    price varchar(255),
    weight varchar(255),
    height varchar(255),
    length varchar(255),
    CONSTRAINT info_cards_object_info_pk primary key (id)
);