-- Borja de Régil Basáñez
-- Antonio Caparrini López

drop table if exists coordinates;
drop table if exists users;
drop table if exists interests;

drop table if exists questions;
drop table if exists answers;

drop table if exists messages;
drop table if exists text_messages;
drop table if exists friendreq_message;
drop table if exists questionreq_message;

drop table if exists users_users;
drop table if exists users_questions;
drop table if exists users_messages;

create table coordinates (
    id int auto_increment primary key,
    latitude decimal not null,
    longitude decimal not null
);

create table users (
    email varchar(250) primary key,
    pass varchar(162) not null,
    userName varchar(50) not null,

    ownGender enum('male', 'female') not null,
    otherGender enum('male', 'female', 'both') not null,

    birthDate date,
    profileImage blob,
    description varchar(500),
    coordinatesId int,

    foreign key (coordinatesId) references coordinates(id) on delete set null
);

create table interests (
    id int auto_increment primary key,
    content varchar(500) not null,
    userId varchar(250) not null,

    foreign key (userId) references users(email) on delete cascade
);

create table users_users (
    ownId varchar(250) not null,
    friendId varchar(250) not null,

    foreign key (ownId) references users(email) on delete cascade,
    foreign key (friendId) references users(email) on delete cascade
);

create table questions (
    id int auto_increment primary key,
    content varchar(500) not null
);

create table answers (
    answerId int auto_increment primary key,
    questionId int not null,
    questionOrder int not null,
    content varchar(500) not null,

    unique key (questionId, questionOrder),
    foreign key (questionId) references questions(id) on delete cascade
);

create table users_questions (
    userId varchar(250) not null,
    rating int not null,
    answerId int not null,

    foreign key (userId) references users(email),
    foreign key (answerId) references answers(answerId) on delete cascade
);

create table messages (
    id int auto_increment primary key,
    sent timestamp not null,
    readStatus boolean not null
);

create table text_messages (
    messageId int not null,
    content varchar(500) not null,

    foreign key (messageId) references messages(id) on delete cascade
);

create table friendreq_message (
    messageId int not null,
    accepted boolean not null,

    foreign key (messageId) references messages(id) on delete cascade
);

create table questionreq_message (
    messageId int not null,
    questionId int,

    foreign key (messageId) references messages(id) on delete cascade,
    foreign key (questionId) references questions(id) on delete set null
);

create table users_messages (
    fromId varchar(250) not null,
    toId varchar(250),
    messageId int not null,

    foreign key (fromId) references users(email) on delete cascade,
    foreign key (toId) references users(email) on delete set null,
    foreign key (messageId) references messages(id) on delete cascade
);
