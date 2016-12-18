-- name: create-rep-table!
create table reps (
  id         integer primary key asc,
  first_name varchar(40),
  last_name  varchar(40),
  online     boolean not null,
  busy       boolean not null
);

-- name: create-rep<!
insert into reps (first_name, last_name, online, busy)
values (?, ?, 0, 0);

-- name: list-reps
select *
from reps;

-- name: get-rep
select *
from reps
where id = :id;

-- name: update-rep-name!
update reps
set first_name = :first_name, last_name = :last_name
where id = :id

-- name: list-idle-reps
select *
from reps
where online = 1
and busy = 0;

-- name: create-conv-table!
create table convs (
  id         integer primary key asc,
  cust_id    text,
  rep_id     integer,
  status     text
);

-- name: create-cust<!
insert into convs (cust_id, rep_id, status)
values (:cust_id, :rep_id, "active");

-- name: get-conv
select *
from convs
where id = :id;

-- name: get-conv-by-rep
select *
from convs
where rep_id = :id;

-- name: create-cust-table!
create table custs (
  id         text,
  first_name varchar(40),
  last_name  varchar(40),
  email      varchar(255)
);

-- create-cust<!
insert into custs (first_name, last_name, email)
values (?, ?, :email);

-- name: create-msg-table!
create table msgs (
  sender     varchar(4),
  body       varchar(500),
  conv_id    integer
);

-- create-msg<!
insert into msgs (sender, body)
values (:sender, :body);
