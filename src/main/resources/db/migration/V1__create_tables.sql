create table if not exists sys_user (
    id bigint primary key auto_increment,
    username varchar(64) not null unique comment '登录账号',
    password_hash varchar(255) not null comment '密码密文',
    name varchar(64) not null comment '姓名',
    role varchar(64) not null comment '角色',
    department varchar(128) not null comment '部门',
    phone varchar(32) default null comment '联系电话',
    status varchar(32) not null default '正常' comment '状态',
    created_at datetime not null comment '创建时间'
) engine=InnoDB default charset=utf8mb4 comment='系统账号表';

create table if not exists matter_category (
    id bigint primary key auto_increment,
    name varchar(64) not null unique comment '分类名称',
    description varchar(255) default null comment '分类说明',
    sort_no int not null default 100 comment '排序号'
) engine=InnoDB default charset=utf8mb4 comment='事项分类表';

create table if not exists gov_matter (
    id varchar(64) primary key,
    name varchar(128) not null comment '事项名称',
    category varchar(64) not null comment '事项分类',
    department varchar(128) not null comment '办理部门',
    limit_days int not null default 5 comment '办理时限天数',
    limit_text varchar(64) not null comment '办理时限展示文本',
    status varchar(32) not null default '草稿' comment '草稿 已上线 已下线',
    publish_channel varchar(255) default null comment '发布渠道',
    service_object varchar(64) not null default '自然人' comment '服务对象',
    conditions_text text comment '受理条件',
    description text comment '事项说明',
    update_time datetime not null comment '更新时间',
    index idx_gov_matter_category(category),
    index idx_gov_matter_status(status),
    index idx_gov_matter_name(name)
) engine=InnoDB default charset=utf8mb4 comment='政务事项表';

create table if not exists gov_matter_material (
    id varchar(64) primary key,
    matter_id varchar(64) not null comment '事项ID',
    name varchar(255) not null comment '材料名称',
    required_flag tinyint(1) not null default 1 comment '是否必填',
    sort_no int not null default 1 comment '排序号',
    index idx_material_matter(matter_id),
    constraint fk_material_matter foreign key (matter_id) references gov_matter(id) on delete cascade
) engine=InnoDB default charset=utf8mb4 comment='事项材料表';

create table if not exists gov_matter_flow_step (
    id varchar(64) primary key,
    matter_id varchar(64) not null comment '事项ID',
    name varchar(255) not null comment '流程步骤名称',
    sort_no int not null default 1 comment '排序号',
    index idx_flow_matter(matter_id),
    constraint fk_flow_matter foreign key (matter_id) references gov_matter(id) on delete cascade
) engine=InnoDB default charset=utf8mb4 comment='事项办理流程步骤表';

create table if not exists service_application (
    id varchar(64) primary key comment '申请编号',
    applicant varchar(64) not null comment '申请人',
    phone varchar(32) not null comment '联系电话',
    id_no varchar(64) default null comment '证件号码',
    matter_id varchar(64) default null comment '事项ID',
    matter_name varchar(128) not null comment '事项名称',
    category varchar(64) not null comment '事项分类',
    description text comment '申请说明',
    submit_time datetime not null comment '提交时间',
    status varchar(32) not null comment '待受理 审核中 待补正 已办结',
    current_node varchar(128) not null comment '当前节点',
    completed_time datetime default null comment '办结时间',
    index idx_application_status(status),
    index idx_application_matter(matter_name),
    index idx_application_submit_time(submit_time)
) engine=InnoDB default charset=utf8mb4 comment='办事申请表';

create table if not exists application_file (
    id varchar(64) primary key,
    application_id varchar(64) not null comment '申请编号',
    name varchar(255) not null comment '材料文件名',
    size_text varchar(64) default null comment '文件大小文本',
    status varchar(32) not null default '待核验' comment '待核验 合格 不合格',
    opinion varchar(255) default null comment '核验意见',
    file_url varchar(500) default null comment '文件地址',
    index idx_file_application(application_id),
    constraint fk_file_application foreign key (application_id) references service_application(id) on delete cascade
) engine=InnoDB default charset=utf8mb4 comment='申请材料文件表';

create table if not exists application_timeline (
    id bigint primary key auto_increment,
    application_id varchar(64) not null comment '申请编号',
    node varchar(128) not null comment '流程节点',
    handler varchar(64) not null comment '处理人',
    status varchar(32) not null comment '节点状态',
    opinion varchar(255) default null comment '处理意见',
    handled_at datetime default null comment '处理时间',
    index idx_timeline_application(application_id),
    constraint fk_timeline_application foreign key (application_id) references service_application(id) on delete cascade
) engine=InnoDB default charset=utf8mb4 comment='申请状态轨迹表';

create table if not exists approval_flow (
    id varchar(64) primary key,
    matter_id varchar(64) default null comment '事项ID',
    matter_name varchar(128) not null comment '事项名称',
    category varchar(64) not null comment '事项分类',
    status varchar(32) not null default '启用' comment '启用 停用',
    update_time datetime not null comment '更新时间',
    index idx_approval_flow_matter(matter_name),
    index idx_approval_flow_status(status)
) engine=InnoDB default charset=utf8mb4 comment='审批流程配置表';

create table if not exists approval_node (
    id varchar(64) primary key,
    flow_id varchar(64) not null comment '审批流程ID',
    node_name varchar(128) not null comment '节点名称',
    level_type varchar(32) not null comment '科室级 部门级',
    department varchar(128) not null comment '审批部门',
    role_name varchar(64) not null comment '审批角色',
    approver varchar(64) not null comment '审批人',
    time_limit_hours int not null default 24 comment '节点办理时限小时',
    pass_condition varchar(255) default null comment '通过条件',
    sort_no int not null default 1 comment '审批顺序',
    index idx_node_flow(flow_id),
    constraint fk_node_flow foreign key (flow_id) references approval_flow(id) on delete cascade
) engine=InnoDB default charset=utf8mb4 comment='审批流程节点表';

create table if not exists approval_record (
    id bigint primary key auto_increment,
    application_id varchar(64) not null comment '申请编号',
    flow_id varchar(64) default null comment '审批流程ID',
    node_name varchar(128) not null comment '审批节点',
    level_type varchar(32) not null comment '审批级别',
    approver varchar(64) not null comment '审批人员',
    department varchar(128) not null comment '审批部门',
    status varchar(32) not null comment '通过 驳回 退回补正 办结',
    opinion varchar(255) default null comment '审批意见',
    approved_at datetime not null comment '审批时间',
    index idx_record_application(application_id),
    index idx_record_flow(flow_id)
) engine=InnoDB default charset=utf8mb4 comment='审批记录表';
