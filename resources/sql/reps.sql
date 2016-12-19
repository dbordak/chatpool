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

-- name: get-rep-name
select first_name, last_name
from reps
where id = :id;

-- name: update-rep-name!
update reps
set first_name = :first_name, last_name = :last_name
where id = :id

-- name: rep-online!
update reps
set online = 1
where id = :id

-- name: rep-offline!
update reps
set online = 0
where id = :id

-- name: rep-busy!
update reps
set busy = 1
where id = :id

-- name: rep-available!
update reps
set busy = 0
where id = :id

-- name: list-idle-reps
select *
from reps
where online = 1
and busy = 0;
