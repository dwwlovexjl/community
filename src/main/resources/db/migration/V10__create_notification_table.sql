create table notification
(
    id BIGINT auto_increment,
    notifier BIGINT not null,
    receiver BIGINT not null,
    outer_id BIGINT not null,
    type int not null,
    gmt_create BIGINT not null,
    status int default 0 not null,
    constraint NOTIFICATION_PK
        primary key (id)
);

comment on table notification is '回复表';

comment on column notification.id is '主键';

comment on column notification.notifier is '发出提醒的用户id';

comment on column notification.receiver is '收到提醒的用户id';

comment on column notification.outer_id is '相关问题或者回复的id';

comment on column notification.type is '判别回复的是回复还是问题';

comment on column notification.gmt_create is '创建时间';

comment on column notification.status is '已读未读标记';