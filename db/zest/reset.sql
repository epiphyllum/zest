delete from j_agent;
delete from j_merchant;
delete from j_sub;
delete from j_maccount;
delete from j_balance;
delete from j_log;
delete from j_card;
delete from j_mcard;
delete from sys_dept where name not in ('大吉');
delete from sys_user where username not in ('dj', 'admin');
delete from sys_role where  name not in ('大吉管理员');
delete from j_exchange;
delete from j_money;


