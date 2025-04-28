create table goods
(
    id          int auto_increment
        primary key,
    name        varchar(50)                              not null,
    type        varchar(20)    default 'common'          null,
    price       decimal(10, 2) default 0.00              null,
    description text                                     null,
    is_active   int            default 1                 not null,
    create_time timestamp      default CURRENT_TIMESTAMP null
);

create table news
(
    id          int auto_increment
        primary key,
    title       varchar(100)                          not null,
    content     text                                  null,
    source      varchar(50)                           null,
    type        varchar(20) default 'new'             null,
    create_time timestamp   default CURRENT_TIMESTAMP null
);

create table nursing
(
    id           int auto_increment
        primary key,
    name         varchar(50)  null,
    address      varchar(200) null,
    phone        varchar(20)  null,
    content      longtext     null,
    time         varchar(20)  null,
    bunk_count   int          null,
    worker_count int          null,
    size         mediumtext   null,
    aptitude     int          null,
    location     varchar(100) null
);

create table user
(
    id             int auto_increment
        primary key,
    username       varchar(50)   not null,
    password       varchar(50)   not null,
    phone          varchar(20)   not null,
    refresh_token  text          null,
    auth           int default 1 not null,
    introduction   text          null,
    nursing_role   int default 1 null comment '养老院中的角色',
    own_nursing_id int           null
);

create table address
(
    id         int auto_increment
        primary key,
    user_id    int                  not null,
    name       varchar(50)          not null,
    phone      varchar(20)          not null,
    province   varchar(20)          not null,
    city       varchar(20)          not null,
    district   varchar(20)          not null,
    detail     varchar(200)         not null,
    is_default tinyint(1) default 0 null,
    constraint fk_address_user_id
        foreign key (user_id) references user (id)
);

create table bed
(
    id          int auto_increment
        primary key,
    nursing_id  int            not null,
    size        int            null,
    address     varchar(200)   null,
    status      int default 0  null,
    meta        varchar(200)   null,
    price       decimal(10, 2) null,
    description text           null,
    aptitude    int default 1  null,
    own_id      int            null,
    constraint bed_own_id_user_id
        foreign key (own_id) references user (id),
    constraint fk_bed_nursing_id
        foreign key (nursing_id) references nursing (id)
);

create table cart
(
    id       int auto_increment
        primary key,
    user_id  int not null,
    goods_id int not null,
    count    int null,
    constraint fk_care_goods_id
        foreign key (goods_id) references goods (id),
    constraint fk_care_user_id
        foreign key (user_id) references user (id)
);

create table comment
(
    id          int auto_increment
        primary key,
    user_id     int                                 not null,
    goods_id    int                                 not null,
    content     text                                null,
    create_time timestamp default CURRENT_TIMESTAMP null,
    grade       int       default 1                 null,
    constraint fk_comment_goods_id
        foreign key (goods_id) references goods (id),
    constraint fk_comment_user_id
        foreign key (user_id) references user (id)
);

create table employee
(
    id          int auto_increment
        primary key,
    name        varchar(50)   null,
    phone       varchar(20)   null,
    meta        varchar(200)  null,
    description text          null,
    user_id     int           null,
    status      int default 0 null,
    job_number  bigint        null,
    nursing_id  int           null,
    constraint fk_employee_nursing_id
        foreign key (nursing_id) references nursing (id),
    constraint fk_employee_user_id
        foreign key (user_id) references user (id)
);

create table image
(
    id          int auto_increment
        primary key,
    src         varchar(200) null,
    news_id     int          null,
    user_id     int          null,
    nursing_id  int          null,
    goods_id    int          null,
    bed_id      int          null,
    employee_id int          null,
    constraint fk_goods_id
        foreign key (goods_id) references goods (id),
    constraint fk_image_bed_id
        foreign key (bed_id) references bed (id),
    constraint fk_image_employee_id
        foreign key (employee_id) references employee (id),
    constraint fk_news_id
        foreign key (news_id) references news (id),
    constraint fk_nursing_id
        foreign key (nursing_id) references nursing (id),
    constraint fk_user_id
        foreign key (user_id) references user (id)
);

create table medicine
(
    id          int auto_increment
        primary key,
    name        varchar(100) null,
    dosage      varchar(100) null,
    start_time  datetime     null,
    end_time    datetime     null,
    employee_id int          null,
    constraint fk_medicine_employee_id
        foreign key (employee_id) references employee (id)
);

create table nursing_booking
(
    id         int auto_increment
        primary key,
    name       varchar(50)   null,
    address    varchar(50)   null,
    phone      varchar(20)   null,
    time       timestamp     null,
    content    varchar(500)  null,
    user_id    int           null,
    nursing_id int           null,
    status     int default 0 null,
    constraint nursing_booking_nursing_booking__fk
        foreign key (user_id) references user (id),
    constraint nursing_booking_nursing_booking__fk_2
        foreign key (nursing_id) references nursing (id)
);

create table orders
(
    id          int auto_increment
        primary key,
    user_id     int                                 not null,
    goods_id    int                                 not null,
    create_time timestamp default CURRENT_TIMESTAMP null,
    status      int       default 1                 null,
    address     varchar(200)                        null,
    phone       varchar(20)                         null,
    price       decimal(10, 2)                      null,
    order_id    varchar(50)                         null,
    count       int                                 null,
    constraint fk_order_goods_id
        foreign key (goods_id) references goods (id),
    constraint fk_order_user_id
        foreign key (user_id) references user (id)
);

