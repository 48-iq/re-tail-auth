create table users (
  id text primary key not null,
  username text not null unique,
  password text not null
);

create table roles (
  name text primary key not null
);

insert into roles (name) values ('ROLE_USER');
insert into roles (name) values ('ROLE_ADMIN');

create table users_roles (
  user_id text not null,
  role_name text not null,
  primary key (user_id, role_name),
  foreign key (user_id) references users (id),
  foreign key (role_name) references roles (name)
);

create table create_user_transactions (
  id text primary key not null,
  entity_id text not null,
  entity_username text not null unique,
  entity_password text not null,
  event_id text not null,
  event_user_id text not null,
  event_name text not null,
  event_surname text not null,
  event_nickname text not null,
  event_phone text not null,
  event_email text not null
);
