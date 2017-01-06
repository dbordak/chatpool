-- name: drop-rep-table!
drop table reps;

-- name: -create-rep<!
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

-- name: -get-rep-by-id
select *
from reps
where id = :id;

-- name: -get-rep-by-uid
select *
from reps
where uid = :uid;

-- name: -get-rep-name
select first_name, last_name
from reps
where id = :id;

-- name: -update-rep-name!
update reps
set first_name = :?, last_name = :?
where id = :id;

-- name: -rep-online!
update reps
set uid = :uid
where id = :id;

-- name: -rep-offline!
update reps
set uid = null
where id = :id;



-- name: drop-conv-table!
drop table convs;

-- name: -create-conv<!
insert into convs (cust_uid, rep_id, active)
values (:cust_uid, :rep_id, TRUE);

-- name: -get-conv
select *
from convs
where id = :id;

-- name: -delete-conv!
delete from convs
where id = :id;

-- name: -delete-msgs!
delete from msgs
where conv_id = :id;

-- name: -get-rep-convs
select *
from convs
where rep_id = :id;

-- name: -get-rep-conv
select *
from convs
where rep_id = :id
and active = TRUE;

-- name: -get-cust-conv
select *
from convs
where cust_uid = :uid
and active = TRUE;

-- name: -get-cust-rep
select *
from reps
where id in
(select rep_id
 from convs
 where cust_uid = :uid
 and active = TRUE);

-- name: -end-conv!
update convs
set active = FALSE
where id = :id;



-- name: drop-cust-table!
drop table custs;

-- name: drop-msg-table!
drop table msgs;

-- name: -create-cust<!
insert into custs (uid, first_name, last_name, email, page)
values (:uid, ?, ?, :email, :page);

-- name: -set-cust-page!
update custs
set page = :page
where uid = :uid;

-- name: -create-msg<!
insert into msgs (sender, body, conv_id)
values (:sender, :body, :id);

-- name: -get-conv-msgs
select *
from msgs
where conv_id = :id;

-- name: -get-user-name
select first_name
from reps
where uid = :uid
union
select first_name
from custs
where uid = :uid;
