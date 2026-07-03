insert into sys_user(username, password_hash, name, role, department, phone, status, created_at)
values
('admin', '{plain}123456', '系统管理员', '系统管理员', '行政审批局', '13800000000', '正常', now()),
('user', '{plain}123456', '办事用户', '普通用户', '个人办事端', '13900000000', '正常', now())
on duplicate key update name = values(name), role = values(role), department = values(department), phone = values(phone), status = values(status);

insert into matter_category(name, description, sort_no)
values
('户籍', '户籍迁入 迁出 证明等公安户政事项', 1),
('社保', '社保卡 参保缴费 待遇资格等人社事项', 2),
('税务', '纳税申报 清税证明 税务证明事项', 3),
('市场准入', '营业执照 企业变更 准入备案事项', 4),
('其他', '其他综合政务事项', 99)
on duplicate key update description = values(description), sort_no = values(sort_no);

insert into gov_matter(id, name, category, department, limit_days, limit_text, status, publish_channel, service_object, conditions_text, description, update_time)
values
('household-register', '户口迁入登记', '户籍', '公安户政窗口', 5, '5个工作日', '已上线', '政务服务平台 移动端大厅', '自然人', '申请人已取得合法稳定住所或符合亲属投靠政策', '用于维护户口迁入登记事项的名称 分类 办理流程 材料目录 办理时限和发布状态', '2026-07-02 09:30:00'),
('social-card', '社会保障卡申领', '社保', '人社服务科', 7, '7个工作日', '已上线', '政务服务平台 自助终端', '自然人', '申请人已完成实名登记且未重复申领有效社会保障卡', '提供社保卡首次申领 信息核验 制卡进度查询和结果领取事项配置', '2026-07-01 16:12:00'),
('tax-clearance', '清税证明开具', '税务', '税务服务厅', 3, '3个工作日', '草稿', '待发布', '企业法人', '纳税人已完成申报并结清应纳税费 滞纳金和罚款', '用于税务类事项上线前的表单 材料 流程 时限和发布范围维护', '2026-06-30 11:45:00'),
('business-license', '营业执照变更登记', '市场准入', '市场监管科', 2, '2个工作日', '已上线', '政务服务平台 企业服务专区', '企业法人', '企业主体状态正常 变更事项符合法定登记条件', '维护企业名称 住所 经营范围 法定代表人等变更事项的政务服务配置', '2026-07-02 08:50:00')
on duplicate key update name = values(name), category = values(category), department = values(department), status = values(status), update_time = values(update_time);

insert ignore into gov_matter_material(id, matter_id, name, required_flag, sort_no)
values
('household-material-1', 'household-register', '居民身份证原件及复印件', 1, 1),
('household-material-2', 'household-register', '户口簿或集体户证明', 1, 2),
('household-material-3', 'household-register', '合法稳定住所证明', 1, 3),
('household-material-4', 'household-register', '亲属关系证明', 0, 4),
('social-card-material-1', 'social-card', '居民身份证', 1, 1),
('social-card-material-2', 'social-card', '电子证件照片', 1, 2),
('social-card-material-3', 'social-card', '监护人身份证明', 0, 3),
('tax-clearance-material-1', 'tax-clearance', '统一社会信用代码证照', 1, 1),
('tax-clearance-material-2', 'tax-clearance', '经办人身份证明', 1, 2),
('tax-clearance-material-3', 'tax-clearance', '授权委托书', 0, 3),
('business-license-material-1', 'business-license', '变更登记申请书', 1, 1),
('business-license-material-2', 'business-license', '股东会决议或决定文件', 1, 2),
('business-license-material-3', 'business-license', '修改后的章程或章程修正案', 1, 3),
('business-license-material-4', 'business-license', '经办人授权委托书', 0, 4);

insert ignore into gov_matter_flow_step(id, matter_id, name, sort_no)
values
('household-flow-1', 'household-register', '在线填报申请信息', 1),
('household-flow-2', 'household-register', '窗口受理并核验材料', 2),
('household-flow-3', 'household-register', '户政科审核迁入条件', 3),
('household-flow-4', 'household-register', '办结并同步户籍结果', 4),
('social-card-flow-1', 'social-card', '提交身份信息和照片', 1),
('social-card-flow-2', 'social-card', '人社部门受理核验', 2),
('social-card-flow-3', 'social-card', '银行制卡并回传状态', 3),
('social-card-flow-4', 'social-card', '发卡完成并短信通知', 4),
('tax-clearance-flow-1', 'tax-clearance', '企业提交清税申请', 1),
('tax-clearance-flow-2', 'tax-clearance', '系统核验申报和缴税状态', 2),
('tax-clearance-flow-3', 'tax-clearance', '税务人员复核异常信息', 3),
('tax-clearance-flow-4', 'tax-clearance', '生成清税证明', 4),
('business-license-flow-1', 'business-license', '企业在线申报变更信息', 1),
('business-license-flow-2', 'business-license', '市场监管科受理材料', 2),
('business-license-flow-3', 'business-license', '登记审批科审核', 3),
('business-license-flow-4', 'business-license', '核发电子营业执照', 4);

insert into service_application(id, applicant, phone, id_no, matter_id, matter_name, category, description, submit_time, status, current_node, completed_time)
values
('ZW202607020001', '张明', '13800000000', '370100199901010011', 'business-license', '营业执照变更登记', '市场准入', '企业经营范围和注册地址需要同步变更', '2026-07-02 09:16:00', '审核中', '登记审批科审核', null),
('ZW202607020016', '李娜', '13900000000', '370100199802020022', 'household-register', '户口迁入登记', '户籍', '因购房申请户口迁入', '2026-07-02 10:40:00', '待补正', '材料补正', null),
('ZW202606300082', '山东星河科技有限公司', '053100000000', '91370100MA0000000A', 'tax-clearance', '清税证明开具', '税务', '企业注销前开具清税证明', '2026-06-30 08:35:00', '已办结', '办结送达', '2026-07-01 10:20:00'),
('ZW202606280036', '王强', '13700000000', '370100199701010033', 'social-card', '社会保障卡申领', '社保', '首次申领社会保障卡', '2026-06-28 14:10:00', '已办结', '办结送达', '2026-06-30 09:30:00')
on duplicate key update status = values(status), current_node = values(current_node), completed_time = values(completed_time);

insert ignore into application_file(id, application_id, name, size_text, status, opinion, file_url)
values
('file-1', 'ZW202607020001', '变更登记申请书.pdf', '1.2MB', '合格', '内容完整', ''),
('file-2', 'ZW202607020001', '股东会决议.pdf', '836KB', '合格', '签章清晰', ''),
('file-3', 'ZW202607020001', '授权委托书.pdf', '520KB', '合格', '已核验', ''),
('file-4', 'ZW202607020016', '合法稳定住所证明.pdf', '640KB', '不合格', '地址信息不完整 请重新上传', ''),
('file-5', 'ZW202606300082', '统一社会信用代码证照.pdf', '780KB', '合格', '已核验', ''),
('file-6', 'ZW202606280036', '居民身份证.pdf', '300KB', '合格', '已核验', '');

insert ignore into application_timeline(id, application_id, node, handler, status, opinion, handled_at)
values
(1, 'ZW202607020001', '申请提交', '申请人', '已完成', '申请提交成功', '2026-07-02 09:16:00'),
(2, 'ZW202607020001', '窗口受理', '王晓敏', '已完成', '材料齐全 予以受理', '2026-07-02 09:42:00'),
(3, 'ZW202607020001', '登记审批科审核', '刘志强', '办理中', '正在核对登记事项', '2026-07-02 10:28:00'),
(4, 'ZW202607020016', '申请提交', '申请人', '已完成', '申请提交成功', '2026-07-02 10:40:00'),
(5, 'ZW202607020016', '窗口受理', '王晓敏', '退回补正', '住所证明地址不完整', '2026-07-02 11:20:00'),
(6, 'ZW202606300082', '办结送达', '系统归档', '已完成', '清税证明已生成', '2026-07-01 10:20:00'),
(7, 'ZW202606280036', '办结送达', '系统归档', '已完成', '社保卡已发卡', '2026-06-30 09:30:00');

insert into approval_flow(id, matter_id, matter_name, category, status, update_time)
values
('flow-business-license', 'business-license', '营业执照变更登记', '市场准入', '启用', '2026-07-02 09:00:00'),
('flow-household-register', 'household-register', '户口迁入登记', '户籍', '启用', '2026-07-02 09:20:00')
on duplicate key update status = values(status), update_time = values(update_time);

insert ignore into approval_node(id, flow_id, node_name, level_type, department, role_name, approver, time_limit_hours, pass_condition, sort_no)
values
('node-business-1', 'flow-business-license', '窗口受理', '科室级', '市场监管科', '窗口工作人员', '王晓敏', 8, '材料完整', 1),
('node-business-2', 'flow-business-license', '科室审核', '科室级', '市场监管科', '科室审批员', '刘志强', 24, '登记事项符合法定条件', 2),
('node-business-3', 'flow-business-license', '部门复核', '部门级', '行政审批局', '部门管理员', '陈主任', 24, '重大变更事项复核通过', 3),
('node-household-1', 'flow-household-register', '窗口受理', '科室级', '公安户政窗口', '窗口工作人员', '赵雪', 8, '材料齐全', 1),
('node-household-2', 'flow-household-register', '户政科审核', '科室级', '公安户政科', '科室审批员', '周警官', 48, '迁入条件符合政策', 2);

insert ignore into approval_record(id, application_id, flow_id, node_name, level_type, approver, department, status, opinion, approved_at)
values
(1, 'ZW202607020001', 'flow-business-license', '窗口受理', '科室级', '王晓敏', '市场监管科', '通过', '材料齐全 予以受理', '2026-07-02 09:42:00'),
(2, 'ZW202607020001', 'flow-business-license', '科室审核', '科室级', '刘志强', '市场监管科', '通过', '经营范围变更内容符合要求', '2026-07-02 10:28:00'),
(3, 'ZW202607020016', 'flow-household-register', '窗口受理', '科室级', '赵雪', '公安户政窗口', '退回补正', '住所证明地址信息不完整', '2026-07-02 11:20:00'),
(4, 'ZW202606300082', 'flow-business-license', '办结送达', '部门级', '系统归档', '税务服务厅', '办结', '清税证明已生成', '2026-07-01 10:20:00');
