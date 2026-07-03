package com.example.govservice.statistics;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class StatisticsService {
    private final JdbcTemplate jdbcTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final long cacheTtlSeconds;

    public StatisticsService(JdbcTemplate jdbcTemplate,
                             StringRedisTemplate redisTemplate,
                             ObjectMapper objectMapper,
                             @Value("${app.statistics.cache-ttl-seconds:60}") long cacheTtlSeconds) {
        this.jdbcTemplate = jdbcTemplate;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.cacheTtlSeconds = cacheTtlSeconds;
    }

    public List<Map<String, Object>> summary(String startDate, String endDate, String category, String department) {
        String cacheKey = cacheKey("summary", startDate, endDate, category, department);
        List<Map<String, Object>> cached = readCache(cacheKey);
        if (cached != null) return cached;

        StringBuilder sql = new StringBuilder("""
                select matter_name as matterName,
                       category,
                       count(1) as applyCount,
                       sum(case when status in ('审核中', '已办结', '待补正') then 1 else 0 end) as acceptedCount,
                       sum(case when status = '已办结' then 1 else 0 end) as completedCount,
                       sum(case when status = '待补正' then 1 else 0 end) as supplementCount
                from service_application where 1=1
                """);
        List<Object> args = new ArrayList<>();
        appendFilters(sql, args, startDate, endDate, category, department);
        sql.append(" group by matter_name, category order by applyCount desc, matterName asc");
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), args.toArray());
        rows.forEach(row -> row.put("completionRate", rate(number(row.get("completedCount")), number(row.get("applyCount")))));
        writeCache(cacheKey, rows);
        return rows;
    }

    public List<Map<String, Object>> efficiency(String startDate, String endDate, String category, String department) {
        String cacheKey = cacheKey("efficiency", startDate, endDate, category, department);
        List<Map<String, Object>> cached = readCache(cacheKey);
        if (cached != null) return cached;

        StringBuilder sql = new StringBuilder("""
                select matter_name as matterName,
                       category,
                       count(1) as sampleCount,
                       round(avg(case when completed_time is not null then timestampdiff(hour, submit_time, completed_time) else null end), 2) as averageHours,
                       min(case when completed_time is not null then timestampdiff(hour, submit_time, completed_time) else null end) as fastestHours,
                       max(case when completed_time is not null then timestampdiff(hour, submit_time, completed_time) else null end) as slowestHours
                from service_application where 1=1
                """);
        List<Object> args = new ArrayList<>();
        appendFilters(sql, args, startDate, endDate, category, department);
        sql.append(" group by matter_name, category order by averageHours asc, matterName asc");
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), args.toArray());
        rows.forEach(row -> {
            double avg = decimal(row.get("averageHours"));
            String status;
            if (avg <= 0) status = "暂无办结样本";
            else if (avg <= 24) status = "高效";
            else if (avg <= 72) status = "正常";
            else status = "需优化";
            row.put("efficiencyStatus", status);
        });
        writeCache(cacheKey, rows);
        return rows;
    }

    public Map<String, Object> overview() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("matterTotal", jdbcTemplate.queryForObject("select count(1) from gov_matter", Integer.class));
        result.put("onlineMatterTotal", jdbcTemplate.queryForObject("select count(1) from gov_matter where status = '已上线'", Integer.class));
        result.put("applicationTotal", jdbcTemplate.queryForObject("select count(1) from service_application", Integer.class));
        result.put("completedTotal", jdbcTemplate.queryForObject("select count(1) from service_application where status = '已办结'", Integer.class));
        result.put("supplementTotal", jdbcTemplate.queryForObject("select count(1) from service_application where status = '待补正'", Integer.class));
        result.put("approvalFlowTotal", jdbcTemplate.queryForObject("select count(1) from approval_flow", Integer.class));
        return result;
    }

    private void appendFilters(StringBuilder sql, List<Object> args, String startDate, String endDate, String category, String department) {
        if (startDate != null && !startDate.isBlank()) {
            sql.append(" and submit_time >= ?");
            args.add(LocalDateTime.of(LocalDate.parse(startDate), LocalTime.MIN));
        }
        if (endDate != null && !endDate.isBlank()) {
            sql.append(" and submit_time <= ?");
            args.add(LocalDateTime.of(LocalDate.parse(endDate), LocalTime.MAX));
        }
        if (category != null && !category.isBlank() && !"全部".equals(category) && !"全部分类".equals(category)) {
            sql.append(" and category = ?");
            args.add(category);
        }
        if (department != null && !department.isBlank() && !"全部".equals(department)) {
            sql.append(" and exists (select 1 from gov_matter m where m.name = service_application.matter_name and m.department = ?)");
            args.add(department);
        }
    }

    private String cacheKey(String type, String... parts) {
        StringBuilder builder = new StringBuilder("gov:statistics:").append(type);
        for (String part : parts) {
            builder.append(':').append(part == null ? "" : part.replace(':', '_'));
        }
        return builder.toString();
    }

    private List<Map<String, Object>> readCache(String key) {
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json == null || json.isBlank()) return null;
            return objectMapper.readValue(json.getBytes(StandardCharsets.UTF_8), new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception ignored) {
            return null;
        }
    }

    private void writeCache(String key, List<Map<String, Object>> data) {
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(data), cacheTtlSeconds, TimeUnit.SECONDS);
        } catch (Exception ignored) {
            // 缓存失败不影响主流程
        }
    }

    private int number(Object value) {
        if (value instanceof Number number) return number.intValue();
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception exception) {
            return 0;
        }
    }

    private double decimal(Object value) {
        if (value instanceof Number number) return number.doubleValue();
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (Exception exception) {
            return 0;
        }
    }

    private String rate(int completed, int total) {
        if (total <= 0) return "0%";
        return Math.round(completed * 10000.0 / total) / 100.0 + "%";
    }
}
