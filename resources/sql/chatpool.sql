-- name: create-conv-table!
create table convs (
  id         integer primary key asc,
  cust_id    text,
  rep_id     integer,
  status     text
);

-- name: create-conv<!
insert into convs (cust_id, rep_id, status)
values (:cust_id, :rep_id, "active");

-- name: get-conv
select *
from convs
where id = :id;

-- name: get-rep-convs
select *
from convs
where rep_id = :id;

-- name: get-rep-conv
select *
from convs
where rep_id = :id
and status = "active";

-- name: create-cust-table!
create table custs (
  uid        text,
  first_name varchar(40),
  last_name  varchar(40),
  email      varchar(255)
);

-- name: create-cust<!
insert into custs (first_name, last_name, email)
values (?, ?, :email);

-- name: create-msg-table!
create table msgs (
  sender     varchar(4),
  body       varchar(500),
  conv_id    integer
);

-- name: create-msg<!
insert into msgs (sender, body)
values (:sender, :body);

-- name: get-conv-msgs
select *
from msgs
where conv_id = :id;
