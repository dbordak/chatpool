-- name: create-rep-table!
create table reps (
  id         integer primary key asc,
  first_name varchar(40),
  last_name  varchar(40)
);

-- name: drop-rep-table!
drop table reps;

-- name: create-rep<!
insert into reps (first_name, last_name) values (?, ?);

-- name: create-conv-table!
create table convs (
  id         integer primary key asc,
  cust_id    text,
  rep_id     integer,
  status     text
);

-- name: create-cust-table!
create table custs (
  id         text,
  first_name varchar(40),
  last_name  varchar(40),
  email      varchar(255)
);

-- name: create-msg-table!
create table msgs (
  sender     varchar(4),
  body       varchar(500),
  conv_id    integer
);
