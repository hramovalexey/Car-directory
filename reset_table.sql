drop table if exists auto_list cascade;
create table auto_list (
	number varchar(12),
	model varchar(50) not null,
	color varchar(50),
	year integer,
timestamp timestamp default current_timestamp,
	constraint pk_auto_list primary key (number)
);