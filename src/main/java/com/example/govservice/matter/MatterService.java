package com.example.govservice.matter;

import com.example.govservice.common.BusinessException;
import com.example.govservice.common.IdGenerator;
import com.example.govservice.common.RequestMaps;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MatterService {
    private final JdbcTemplate jdbcTemplate;

    public MatterService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> list(String category, String status, String keyword) {
        StringBuilder sql = new StringBuilder("select * from gov_matter where 1=1");
        List<Object> args = new ArrayList<>();
        if (category != null && !category.isBlank() && !"全部分类".equals(category)) {
            sql.append(" and category = ?");
            args.add(category);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" and status = ?");
            args.add(status);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" and (name like ? or department like ? or description like ?)");
            String like = "%" + keyword.trim() + "%";
            args.add(like);
            args.add(like);
            args.add(like);
        }
        sql.append(" order by update_time desc");
        List<Map<String, Object>> matters = jdbcTemplate.queryForList(sql.toString(), args.toArray());
        matters.forEach(this::fillChildren);
        return matters;
    }

    public Map<String, Object> get(String id) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from gov_matter where id = ?", id);
        if (list.isEmpty()) {
            throw new BusinessException(404, "事项不存在");
        }
        Map<String, Object> matter = list.get(0);
        fillChildren(matter);
        return matter;
    }

    @Transactional
    public Map<String, Object> create(Map<String, Object> body) {
        String id = RequestMaps.str(body, "id", IdGenerator.textId("matter"));
        String name = RequestMaps.str(body, "name", "新增政务事项");
        String category = RequestMaps.str(body, "category", "其他");
        String department = RequestMaps.str(body, "department", "待配置部门");
        String limitText = RequestMaps.str(body, "limit", RequestMaps.str(body, "limitText", "5个工作日"));
        int limitDays = RequestMaps.integer(body, "limitDays", parseLimitDays(limitText));
        String status = RequestMaps.str(body, "status", "草稿");
        String publishChannel = RequestMaps.str(body, "publishChannel", "待发布");
        String serviceObject = RequestMaps.str(body, "serviceObject", "自然人");
        String conditions = RequestMaps.str(body, "conditions", "请录入事项受理条件");
        String description = RequestMaps.str(body, "description", "请录入事项说明");

        jdbcTemplate.update("""
                insert into gov_matter(id, name, category, department, limit_days, limit_text, status, publish_channel, service_object, conditions_text, description, update_time)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, id, name, category, department, limitDays, limitText, status, publishChannel, serviceObject, conditions, description, LocalDateTime.now());

        List<Map<String, Object>> materials = RequestMaps.listOfMap(body, "materials");
        if (materials.isEmpty()) {
            addMaterial(id, "申请表", true, 1);
        } else {
            for (int i = 0; i < materials.size(); i++) {
                Map<String, Object> item = materials.get(i);
                addMaterial(id, RequestMaps.str(item, "name", "材料" + (i + 1)), RequestMaps.bool(item, "required", true), i + 1);
            }
        }

        List<Map<String, Object>> flow = RequestMaps.listOfMap(body, "flow");
        if (flow.isEmpty()) {
            addFlowStep(id, "申请提交", 1);
        } else {
            for (int i = 0; i < flow.size(); i++) {
                addFlowStep(id, RequestMaps.str(flow.get(i), "name", "流程节点" + (i + 1)), i + 1);
            }
        }

        ensureCategory(category);
        return get(id);
    }

    @Transactional
    public Map<String, Object> update(String id, Map<String, Object> body) {
        get(id);
        String name = RequestMaps.str(body, "name", null);
        String category = RequestMaps.str(body, "category", null);
        String department = RequestMaps.str(body, "department", null);
        String limitText = RequestMaps.str(body, "limit", RequestMaps.str(body, "limitText", null));
        String status = RequestMaps.str(body, "status", null);
        String publishChannel = RequestMaps.str(body, "publishChannel", null);
        String serviceObject = RequestMaps.str(body, "serviceObject", null);
        String conditions = RequestMaps.str(body, "conditions", null);
        String description = RequestMaps.str(body, "description", null);

        Map<String, Object> old = get(id);
        String finalLimitText = limitText == null ? String.valueOf(old.get("limit_text")) : limitText;
        int limitDays = RequestMaps.integer(body, "limitDays", parseLimitDays(finalLimitText));

        jdbcTemplate.update("""
                update gov_matter set name = ?, category = ?, department = ?, limit_days = ?, limit_text = ?, status = ?, publish_channel = ?,
                service_object = ?, conditions_text = ?, description = ?, update_time = ? where id = ?
                """,
                name == null ? old.get("name") : name,
                category == null ? old.get("category") : category,
                department == null ? old.get("department") : department,
                limitDays,
                finalLimitText,
                status == null ? old.get("status") : status,
                publishChannel == null ? old.get("publish_channel") : publishChannel,
                serviceObject == null ? old.get("service_object") : serviceObject,
                conditions == null ? old.get("conditions_text") : conditions,
                description == null ? old.get("description") : description,
                LocalDateTime.now(), id);

        if (category != null) {
            ensureCategory(category);
        }
        return get(id);
    }

    @Transactional
    public void delete(String id) {
        get(id);
        jdbcTemplate.update("delete from gov_matter_flow_step where matter_id = ?", id);
        jdbcTemplate.update("delete from gov_matter_material where matter_id = ?", id);
        jdbcTemplate.update("delete from gov_matter where id = ?", id);
    }

    public Map<String, Object> publish(String id, Map<String, Object> body) {
        get(id);
        String status = RequestMaps.str(body, "status", "已上线");
        String channel = RequestMaps.str(body, "publishChannel", "政务服务平台");
        jdbcTemplate.update("update gov_matter set status = ?, publish_channel = ?, update_time = ? where id = ?", status, channel, LocalDateTime.now(), id);
        return get(id);
    }

    public List<Map<String, Object>> categories() {
        return jdbcTemplate.queryForList("""
                select c.id, c.name, c.description, c.sort_no,
                       (select count(1) from gov_matter m where m.category = c.name) as matter_count
                from matter_category c order by c.sort_no, c.id
                """);
    }

    public Map<String, Object> createCategory(Map<String, Object> body) {
        String name = RequestMaps.str(body, "name");
        if (name.isBlank()) {
            throw new BusinessException("分类名称不能为空");
        }
        String description = RequestMaps.str(body, "description", "");
        int sortNo = RequestMaps.integer(body, "sortNo", 100);
        jdbcTemplate.update("insert into matter_category(name, description, sort_no) values (?, ?, ?)", name, description, sortNo);
        return jdbcTemplate.queryForMap("select * from matter_category where name = ?", name);
    }

    private void fillChildren(Map<String, Object> matter) {
        String id = String.valueOf(matter.get("id"));
        matter.put("materials", jdbcTemplate.queryForList("select id, name, required_flag as required, sort_no from gov_matter_material where matter_id = ? order by sort_no", id));
        matter.put("flow", jdbcTemplate.queryForList("select id, name, sort_no from gov_matter_flow_step where matter_id = ? order by sort_no", id));
    }

    private void addMaterial(String matterId, String name, boolean required, int sortNo) {
        jdbcTemplate.update("insert into gov_matter_material(id, matter_id, name, required_flag, sort_no) values (?, ?, ?, ?, ?)",
                IdGenerator.textId("material"), matterId, name, required, sortNo);
    }

    private void addFlowStep(String matterId, String name, int sortNo) {
        jdbcTemplate.update("insert into gov_matter_flow_step(id, matter_id, name, sort_no) values (?, ?, ?, ?)",
                IdGenerator.textId("flow-step"), matterId, name, sortNo);
    }

    private void ensureCategory(String category) {
        Integer count = jdbcTemplate.queryForObject("select count(1) from matter_category where name = ?", Integer.class, category);
        if (count != null && count == 0) {
            jdbcTemplate.update("insert into matter_category(name, description, sort_no) values (?, ?, ?)", category, "系统自动补充分组", 100);
        }
    }

    private int parseLimitDays(String limitText) {
        if (limitText == null) return 5;
        String digits = limitText.replaceAll("[^0-9]", "");
        if (digits.isBlank()) return 5;
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException exception) {
            return 5;
        }
    }
}
