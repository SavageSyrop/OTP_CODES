alter table users
add is_banned bool;
update users set is_banned=false where is_banned is null;
