package com.example.govservice.application;

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
public class ApplicationService {
    private final JdbcTemplate jdbcTemplate;

    public ApplicationService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> list(String status, String keyword) {
        StringBuilder sql = new StringBuilder("select * from service_application where 1=1");
        List<Object> args = new ArrayList<>();
        if (status != null && !status.isBlank()) {
            sql.append(" and status = ?");
            args.add(status);
        }
        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword.trim() + "%";
            sql.append(" and (id like ? or applicant like ? or matter_name like ? or phone like ?)");
            args.add(like);
            args.add(like);
            args.add(like);
            args.add(like);
        }
        sql.append(" order by submit_time desc");
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), args.toArray());
        rows.forEach(this::fillChildren);
        return rows;
    }

    public Map<String, Object> get(String id) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from service_application where id = ?", id);
        if (rows.isEmpty()) {
            throw new BusinessException(404, "申请不存在");
        }
        Map<String, Object> item = rows.get(0);
        fillChildren(item);
        return item;
    }

    public Map<String, Object> status(String id) {
        Map<String, Object> item = get(id);
        return Map.of(
                "id", item.get("id"),
                "status", item.get("status"),
                "currentNode", item.get("current_node"),
                "timeline", item.get("timeline"),
                "files", item.get("files")
        );
    }

    @Transactional
    public Map<String, Object> create(Map<String, Object> body) {
        String id = IdGenerator.applicationNo();
        String applicant = RequestMaps.str(body, "applicant");
        if (applicant.isBlank()) throw new BusinessException("申请人不能为空");
        String phone = RequestMaps.str(body, "phone");
        String matterName = RequestMaps.str(body, "matter", RequestMaps.str(body, "matterName", "未选择事项"));
        String matterId = RequestMaps.str(body, "matterId", "");
        String category = RequestMaps.str(body, "category", queryMatterCategory(matterId, matterName));
        String idNo = RequestMaps.str(body, "idNo", RequestMaps.str(body, "idNumber", ""));
        String description = RequestMaps.str(body, "description", "");
        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update("""
                insert into service_application(id, applicant, phone, id_no, matter_id, matter_name, category, description, submit_time, status, current_node)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, '待受理', '窗口受理')
                """, id, applicant, phone, idNo, matterId, matterName, category, description, now);

        addTimeline(id, "申请提交", "申请人", "已完成", "申请提交成功", now);
        addTimeline(id, "窗口受理", "窗口工作人员", "待处理", "等待受理", null);

        List<Map<String, Object>> files = RequestMaps.listOfMap(body, "files");
        if (files.isEmpty()) {
            files = RequestMaps.listOfMap(body, "materials");
        }
        for (Map<String, Object> file : files) {
            addFile(id,
                    RequestMaps.str(file, "name", "申请材料"),
                    RequestMaps.str(file, "size", RequestMaps.str(file, "sizeText", "")),
                    RequestMaps.str(file, "status", "待核验"),
                    RequestMaps.str(file, "opinion", "待材料核验"),
                    RequestMaps.str(file, "url", ""));
        }
        return get(id);
    }

    @Transactional
    public Map<String, Object> supplement(String id, Map<String, Object> body) {
        get(id);
        List<Map<String, Object>> files = RequestMaps.listOfMap(body, "files");
        if (files.isEmpty()) {
            throw new BusinessException("补正材料不能为空");
        }
        for (Map<String, Object> file : files) {
            addFile(id,
                    RequestMaps.str(file, "name", "补正材料"),
                    RequestMaps.str(file, "size", ""),
                    "待核验",
                    RequestMaps.str(file, "opinion", "申请人已重新提交补正材料"),
                    RequestMaps.str(file, "url", ""));
        }
        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.update("update service_application set status = '审核中', current_node = '补正材料复核' where id = ?", id);
        addTimeline(id, "材料补正", "申请人", "已完成", RequestMaps.str(body, "opinion", "已提交补正材料"), now);
        return get(id);
    }

    @Transactional
    public Map<String, Object> rejectMaterials(String id, Map<String, Object> body) {
        get(id);
        String opinion = RequestMaps.str(body, "opinion", "材料不符合要求 请补正后重新提交");
        jdbcTemplate.update("update service_application set status = '待补正', current_node = '材料补正' where id = ?", id);
        jdbcTemplate.update("update application_file set status = '不合格', opinion = ? where application_id = ? and status <> '合格'", opinion, id);
        addTimeline(id, "材料补正", "窗口工作人员", "退回补正", opinion, LocalDateTime.now());
        return get(id);
    }

    @Transactional
    public Map<String, Object> accept(String id, Map<String, Object> body) {
        get(id);
        String handler = RequestMaps.str(body, "handler", "窗口工作人员");
        String opinion = RequestMaps.str(body, "opinion", "材料齐全 予以受理");
        jdbcTemplate.update("update service_application set status = '审核中', current_node = '科室审核' where id = ?", id);
        jdbcTemplate.update("update application_file set status = '合格', opinion = '已核验' where application_id = ?", id);
        addTimeline(id, "窗口受理", handler, "已完成", opinion, LocalDateTime.now());
        addTimeline(id, "科室审核", "科室审批员", "办理中", "等待科室审批", LocalDateTime.now());
        return get(id);
    }

    private void fillChildren(Map<String, Object> item) {
        String id = String.valueOf(item.get("id"));
        item.put("files", jdbcTemplate.queryForList("select * from application_file where application_id = ? order by id", id));
        item.put("timeline", jdbcTemplate.queryForList("select node, handled_at as time, handler, status, opinion from application_timeline where application_id = ? order by id", id));
    }

    private void addFile(String applicationId, String name, String size, String status, String opinion, String url) {
        jdbcTemplate.update("insert into application_file(id, application_id, name, size_text, status, opinion, file_url) values (?, ?, ?, ?, ?, ?, ?)",
                IdGenerator.textId("file"), applicationId, name, size, status, opinion, url);
    }

    private void addTimeline(String applicationId, String node, String handler, String status, String opinion, LocalDateTime time) {
        jdbcTemplate.update("insert into application_timeline(application_id, node, handler, status, opinion, handled_at) values (?, ?, ?, ?, ?, ?)",
                applicationId, node, handler, status, opinion, time);
    }

    private String queryMatterCategory(String matterId, String matterName) {
        try {
            if (!matterId.isBlank()) {
                return jdbcTemplate.queryForObject("select category from gov_matter where id = ?", String.class, matterId);
            }
            return jdbcTemplate.queryForObject("select category from gov_matter where name = ? limit 1", String.class, matterName);
        } catch (Exception ignored) {
            return "其他";
        }
    }
}
