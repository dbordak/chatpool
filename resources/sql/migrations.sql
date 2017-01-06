-- name: create-rep-table!
create table reps (
  id         serial primary key,
  uid        varchar(40),
  first_name varchar(40),
  last_name  varchar(40)
);

-- name: create-conv-table!
create table convs (
  id         serial primary key,
  cust_uid   varchar(40),
  rep_id     integer,
  active     boolean not null
);

-- name: create-cust-table!
create table custs (
  uid        varchar(40),
  first_name varchar(40),
  last_name  varchar(40),
  email      varchar(255),
  page       varchar(40)
);

-- name: create-msg-table!
create table msgs (
  sender     varchar(4),
  body       varchar(500),
  conv_id    integer,
  time       timestamp not null default current_timestamp()
);
