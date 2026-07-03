package com.example.govservice.approval;

import com.example.govservice.common.BusinessException;
import com.example.govservice.common.IdGenerator;
import com.example.govservice.common.RequestMaps;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ApprovalService {
    private final JdbcTemplate jdbcTemplate;

    public ApprovalService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> flows(String keyword, String status) {
        StringBuilder sql = new StringBuilder("select * from approval_flow where 1=1");
        List<Object> args = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword.trim() + "%";
            sql.append(" and (matter_name like ? or category like ?)");
            args.add(like);
            args.add(like);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" and status = ?");
            args.add(status);
        }
        sql.append(" order by update_time desc");
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), args.toArray());
        rows.forEach(this::fillNodes);
        return rows;
    }

    public Map<String, Object> getFlow(String id) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from approval_flow where id = ?", id);
        if (rows.isEmpty()) {
            throw new BusinessException(404, "审批流程不存在");
        }
        Map<String, Object> flow = rows.get(0);
        fillNodes(flow);
        return flow;
    }

    @Transactional
    public Map<String, Object> createFlow(Map<String, Object> body) {
        String id = RequestMaps.str(body, "id", IdGenerator.textId("approval-flow"));
        String matterId = RequestMaps.str(body, "matterId", "");
        String matterName = RequestMaps.str(body, "matterName", RequestMaps.str(body, "matter", "未关联事项"));
        String category = RequestMaps.str(body, "category", "其他");
        String status = RequestMaps.str(body, "status", "启用");
        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.update("insert into approval_flow(id, matter_id, matter_name, category, status, update_time) values (?, ?, ?, ?, ?, ?)",
                id, matterId, matterName, category, status, now);
        List<Map<String, Object>> nodes = RequestMaps.listOfMap(body, "nodes");
        if (nodes.isEmpty()) {
            createNode(id, Map.of("nodeName", "窗口受理", "levelType", "科室级", "department", "综合受理窗口", "roleName", "窗口工作人员", "approver", "王晓敏", "timeLimitHours", 8, "passCondition", "材料齐全"));
            createNode(id, Map.of("nodeName", "科室审核", "levelType", "科室级", "department", "业务科室", "roleName", "科室审批员", "approver", "刘志强", "timeLimitHours", 24, "passCondition", "事项条件满足"));
        } else {
            for (Map<String, Object> node : nodes) {
                createNode(id, node);
            }
        }
        return getFlow(id);
    }

    @Transactional
    public Map<String, Object> updateFlow(String id, Map<String, Object> body) {
        Map<String, Object> old = getFlow(id);
        jdbcTemplate.update("""
                update approval_flow set matter_id = ?, matter_name = ?, category = ?, status = ?, update_time = ? where id = ?
                """,
                RequestMaps.str(body, "matterId", String.valueOf(old.getOrDefault("matter_id", ""))),
                RequestMaps.str(body, "matterName", String.valueOf(old.getOrDefault("matter_name", ""))),
                RequestMaps.str(body, "category", String.valueOf(old.getOrDefault("category", "其他"))),
                RequestMaps.str(body, "status", String.valueOf(old.getOrDefault("status", "启用"))),
                LocalDateTime.now(), id);
        return getFlow(id);
    }

    @Transactional
    public void deleteFlow(String id) {
        getFlow(id);
        jdbcTemplate.update("delete from approval_node where flow_id = ?", id);
        jdbcTemplate.update("delete from approval_flow where id = ?", id);
    }

    @Transactional
    public Map<String, Object> createNode(String flowId, Map<String, Object> body) {
        getFlowBasic(flowId);
        String nodeId = RequestMaps.str(body, "id", IdGenerator.textId("approval-node"));
        int sortNo = nextSortNo(flowId);
        jdbcTemplate.update("""
                insert into approval_node(id, flow_id, node_name, level_type, department, role_name, approver, time_limit_hours, pass_condition, sort_no)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, nodeId, flowId,
                RequestMaps.str(body, "nodeName", RequestMaps.str(body, "name", "审批节点")),
                RequestMaps.str(body, "levelType", "科室级"),
                RequestMaps.str(body, "department", "待配置部门"),
                RequestMaps.str(body, "roleName", RequestMaps.str(body, "role", "审批员")),
                RequestMaps.str(body, "approver", "待配置人员"),
                RequestMaps.integer(body, "timeLimitHours", 24),
                RequestMaps.str(body, "passCondition", RequestMaps.str(body, "condition", "符合审批要求")),
                RequestMaps.integer(body, "sortNo", sortNo));
        touchFlow(flowId);
        return getFlow(flowId);
    }

    @Transactional
    public Map<String, Object> updateNode(String flowId, String nodeId, Map<String, Object> body) {
        getFlowBasic(flowId);
        Map<String, Object> old = getNode(nodeId);
        jdbcTemplate.update("""
                update approval_node set node_name = ?, level_type = ?, department = ?, role_name = ?, approver = ?, time_limit_hours = ?, pass_condition = ?, sort_no = ?
                where id = ? and flow_id = ?
                """,
                RequestMaps.str(body, "nodeName", String.valueOf(old.get("node_name"))),
                RequestMaps.str(body, "levelType", String.valueOf(old.get("level_type"))),
                RequestMaps.str(body, "department", String.valueOf(old.get("department"))),
                RequestMaps.str(body, "roleName", String.valueOf(old.get("role_name"))),
                RequestMaps.str(body, "approver", String.valueOf(old.get("approver"))),
                RequestMaps.integer(body, "timeLimitHours", Integer.parseInt(String.valueOf(old.get("time_limit_hours")))),
                RequestMaps.str(body, "passCondition", String.valueOf(old.get("pass_condition"))),
                RequestMaps.integer(body, "sortNo", Integer.parseInt(String.valueOf(old.get("sort_no")))),
                nodeId, flowId);
        touchFlow(flowId);
        return getFlow(flowId);
    }

    @Transactional
    public Map<String, Object> deleteNode(String flowId, String nodeId) {
        getFlowBasic(flowId);
        jdbcTemplate.update("delete from approval_node where id = ? and flow_id = ?", nodeId, flowId);
        touchFlow(flowId);
        return getFlow(flowId);
    }

    public List<Map<String, Object>> records(String applicationId, String flowId) {
        StringBuilder sql = new StringBuilder("select * from approval_record where 1=1");
        List<Object> args = new ArrayList<>();
        if (applicationId != null && !applicationId.isBlank()) {
            sql.append(" and application_id = ?");
            args.add(applicationId);
        }
        if (flowId != null && !flowId.isBlank()) {
            sql.append(" and flow_id = ?");
            args.add(flowId);
        }
        sql.append(" order by approved_at desc, id desc");
        return jdbcTemplate.queryForList(sql.toString(), args.toArray());
    }

    @Transactional
    public Map<String, Object> createRecord(Map<String, Object> body) {
        String applicationId = RequestMaps.str(body, "applicationId");
        if (applicationId.isBlank()) throw new BusinessException("申请编号不能为空");
        String flowId = RequestMaps.str(body, "flowId", "");
        String nodeName = RequestMaps.str(body, "nodeName", "审批节点");
        String levelType = RequestMaps.str(body, "levelType", "科室级");
        String approver = RequestMaps.str(body, "approver", "审批员");
        String department = RequestMaps.str(body, "department", "业务部门");
        String status = RequestMaps.str(body, "status", "通过");
        String opinion = RequestMaps.str(body, "opinion", "同意办理");
        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update("""
                insert into approval_record(application_id, flow_id, node_name, level_type, approver, department, status, opinion, approved_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, applicationId, flowId, nodeName, levelType, approver, department, status, opinion, now);

        if ("通过".equals(status) || "办结".equals(status)) {
            jdbcTemplate.update("update service_application set status = ?, current_node = ?, completed_time = case when ? = '办结' then ? else completed_time end where id = ?",
                    "办结".equals(status) ? "已办结" : "审核中", "办结".equals(status) ? "办结送达" : "下一审批节点", status, now, applicationId);
        } else if ("驳回".equals(status) || "退回补正".equals(status)) {
            jdbcTemplate.update("update service_application set status = '待补正', current_node = '材料补正' where id = ?", applicationId);
        }

        return jdbcTemplate.queryForMap("select * from approval_record where id = last_insert_id()");
    }

    private void fillNodes(Map<String, Object> flow) {
        String flowId = String.valueOf(flow.get("id"));
        flow.put("nodes", jdbcTemplate.queryForList("select * from approval_node where flow_id = ? order by sort_no, id", flowId));
    }

    private Map<String, Object> getFlowBasic(String id) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from approval_flow where id = ?", id);
        if (rows.isEmpty()) throw new BusinessException(404, "审批流程不存在");
        return rows.get(0);
    }

    private Map<String, Object> getNode(String nodeId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from approval_node where id = ?", nodeId);
        if (rows.isEmpty()) throw new BusinessException(404, "审批节点不存在");
        return rows.get(0);
    }

    private int nextSortNo(String flowId) {
        Integer max = jdbcTemplate.queryForObject("select coalesce(max(sort_no), 0) from approval_node where flow_id = ?", Integer.class, flowId);
        return (max == null ? 0 : max) + 1;
    }

    private void touchFlow(String flowId) {
        jdbcTemplate.update("update approval_flow set update_time = ? where id = ?", LocalDateTime.now(), flowId);
    }
}
