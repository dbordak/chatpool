-- name: create-conv-table!
create table convs (
  id         integer primary key asc,
  cust_uid   text,
  rep_id     integer,
  active     boolean not null
);

-- name: create-conv<!
insert into convs (cust_uid, rep_id, active)
values (:cust_uid, :rep_id, 1);

-- name: get-conv
select *
from convs
where id = :id;

-- name: delete-conv!
delete from convs
where id = :id;

-- name: delete-msgs!
delete from msgs
where conv_id = :id;

-- name: get-rep-convs
select *
from convs
where rep_id = :id;

-- name: get-rep-conv
select *
from convs
where rep_id = :id
and active = 1;

-- name: get-cust-conv
select *
from convs
where cust_uid = :uid
and active = 1;

-- name: get-cust-rep
select *
from reps
where id in
(select rep_id
 from convs
 where cust_uid = :uid
 and active = 1);

-- name: end-conv!
update convs
set active = 0
where id = :id;

-- name: create-cust-table!
create table custs (
  uid        text,
  first_name varchar(40),
  last_name  varchar(40),
  email      varchar(255)
);

-- name: create-cust<!
insert into custs (uid, first_name, last_name, email)
values (:uid, ?, ?, :email);

-- name: create-msg-table!
create table msgs (
  sender     varchar(4),
  body       varchar(500),
  conv_id    integer
);

-- name: create-msg<!
insert into msgs (sender, body, conv_id)
values (:sender, :body, :id);

-- name: get-conv-msgs
select *
from msgs
where conv_id = :id;

-- name: get-user-name
select first_name
from reps
where uid = :uid
union
select first_name
from custs
where uid = :uid;
