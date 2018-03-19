--clean up
delete from documents_employment;
delete from salaries;
delete from bonus;
delete from bonus_type;
delete from users;
delete from departments;
delete from roles;

insert into roles values('1', 'manager');
insert into roles values('2', 'employee');

insert into departments values (1, 'managers', null, null);

insert into users(user_id,
                  username,
                  password,
                  address,
                  avatar_content,
                  avatar_name,
                  city,
                  county,
                  email,
                  first_name,
                  last_name, 
                  hd_current_year,
                  hd_last_year,
                  hd_received_current_year,
                  hire_date,
                  phone,
                  salary,
                  department_id,
                  job_id,
                  manager_id,
                  role_id
                  ) 
values (1, 'john.smith', 'password', 'Sillicon Valey', null, 'myavatar', 'California', 'California', 'john.smith@bigcorp.com',
        'John', 'Smith', 7, 7, 7, TO_DATE('04-03-2016','DD-MM-YYYY'),'072183093', 52.78, null, null, null, 1);

insert into users(user_id,
                  username,
                  password,
                  address,
                  avatar_content,
                  avatar_name,
                  city,
                  county,
                  email,
                  first_name,
                  last_name,
                  hd_current_year,
                  hd_last_year,
                  hd_received_current_year,
                  hire_date,
                  phone,
                  salary,
                  department_id,
                  job_id,
                  manager_id,
                  role_id
                  )
values (2, 'laura.brown', 'password', 'Sillicon Valey', null, 'myavatar', 'California', 'California', 'laura.brown@bigcorp.com',
        'Laura', 'Brown', 7, 7, 7, TO_DATE('04-03-2016','DD-MM-YYYY'),'072183093', 52.78, 1, null, null, 1);


insert into bonus_type values (1, 'Performance');
insert into bonus values(1,'Yes', TO_DATE('03-05-2017','DD-MM-YYYY'), TO_DATE('07-07-2017','DD-MM-YYYY'), 'Very good guy', 'yes', 234.56, 1, 1,1, 1);

insert into salaries values(1, 11000.98, TO_DATE('24-03-2018','DD-MM-YYYY'), 1);

insert into documents_employment values(1, null, 'Previous employment', 1);

commit;