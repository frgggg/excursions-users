create sequence hibernate_sequence start 1 increment 1;

create table users (
id int8 not null, coins int8 not null check (coins>=0), coins_last_update timestamp not null, name varchar(90) not null, primary key (id)
);
