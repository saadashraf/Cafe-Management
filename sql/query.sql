create user test identified by test;

grant connect ,create sessiom,resource,DBA to test;




create table inventory(
id number,
rice number ,
beef number,
chicken number,
constraints pk_inventory primary key (id)
);  
/*initial value to start with*/
insert into inventory values(1,100,100,100);

create table item_added(
p_date date,
rice number,
r_price number,
beef number,
b_price number,
chicken number,
c_price number,
total_cost number,
constraints pk_item_added primary key (p_date)
);

/*arbitary values may not be mathematically correct*/
insert into item_added values(to_date('01/10/2018','dd/mm/yyyy'),100,10,200,120,50,0,1000);
insert into item_added values(to_date('02/10/2018','dd/mm/yyyy'),50,10,10,200,5,10,2000);
insert into item_added values(to_date('05/10/2018','dd/mm/yyyy'),100,0,10,200,10,10,2000);

create table item_removed(
r_date date,
rice number,
beef number,
chicken number,
total number,
constraints pk_item_removed primary key (r_date)
);



insert into item_removed values (to_date('02/10/2018','dd/mm/yyyy'),50,50,50,100);
insert into item_removed values (to_date('03/10/2018','dd/mm/yyyy'),100,50,50,100);
insert into item_removed values (to_date('04/10/2018','dd/mm/yyyy'),50,50,40,100);




create table consumption(
    c_date date,
    students number,
    totalconsumption number,
    wastage number,
    constraints pk_consumption primary key (c_date)
);

insert into consumption values(to_date('02/10/2018','dd/mm/yyyy'),50,50,50);
insert into consumption values(to_date('03/10/2018','dd/mm/yyyy'),100,50,50);
insert into consumption values(to_date('02/10/2018','dd/mm/yyyy'),50,60,50);


 create or replace view exp_cost as
   select * from item_removed;


create or replace view p_cost as
   select * from item_added;