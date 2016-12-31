-- name: create-rep-table!
create table reps (
  id         serial primary key,
  uid        text,
  first_name varchar(40),
  last_name  varchar(40)
);

-- name: drop-rep-table!
drop table reps;

-- name: create-rep<!
insert into reps (first_name, last_name)
values (?, ?);

-- name: list-reps
select *
from reps;

-- name: list-idle-reps
select *
from reps
where uid is not null
and id not in (select rep_id from convs where active = TRUE);

-- name: get-rep
select *
from reps
where id = :id;

-- name: get-rep-by-uid
select *
from reps
where uid = :uid;

-- name: get-rep-name
select first_name, last_name
from reps
where id = :id;

-- name: update-rep-name!
update reps
set first_name = :first_name, last_name = :last_name
where id = :id;

-- name: rep-online!
update reps
set uid = :uid
where id = :id;

-- name: rep-offline!
update reps
set uid = null
where id = :id;
