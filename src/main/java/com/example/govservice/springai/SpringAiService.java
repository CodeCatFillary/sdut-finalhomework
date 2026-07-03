package com.example.govservice.springai;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SpringAiService {
    private static final String HISTORY_KEY = "gov:spring-ai:chat-history";
    private final StringRedisTemplate redisTemplate;

    public SpringAiService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Map<String, Object> chat(String question, String scene) {
        String safeQuestion = question == null ? "" : question.trim();
        String safeScene = scene == null || scene.isBlank() ? "智能咨询问答" : scene.trim();
        String answer = buildAnswer(safeQuestion, safeScene);
        List<String> suggestions = buildSuggestions(safeQuestion, safeScene);
        saveHistory(safeQuestion, safeScene, answer);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("answer", answer);
        result.put("suggestions", suggestions);
        result.put("scene", safeScene);
        result.put("time", LocalDateTime.now());
        return result;
    }

    private String buildAnswer(String question, String scene) {
        if (question.isBlank()) {
            return "请输入需要咨询的政务事项问题 例如办理流程 所需材料 审批进度 补正操作或报表查询";
        }

        String text = question + " " + scene;
        if (containsAny(text, "材料", "证件", "附件", "上传")) {
            return "材料办理建议：请先确认事项名称 再准备身份证明 申请表 业务证明材料和授权委托材料。系统会在受理环节核验材料完整性 不合格材料会退回补正 并记录补正原因和再次提交时间。";
        }
        if (containsAny(text, "流程", "步骤", "怎么办", "办理")) {
            return "办理流程建议：通常按 在线提交申请 窗口受理 科室级审核 部门级审批 办结送达 的顺序流转。管理员可以在审批流程管理模块为不同事项配置节点 顺序 审批角色 办理时限和通过条件。";
        }
        if (containsAny(text, "进度", "状态", "审批到哪", "查询")) {
            return "进度查询建议：可通过申请编号查询当前状态。状态包括待受理 审核中 待补正 已办结。系统会展示当前节点 审批人员 审批时间和审批意见。";
        }
        if (containsAny(text, "补正", "退回", "不合格", "重新提交")) {
            return "补正操作建议：工作人员退回不合格材料时需要填写补正意见。申请人重新上传材料后 申请状态会从待补正恢复为审核中 并进入补正材料复核节点。";
        }
        if (containsAny(text, "统计", "报表", "导出", "Excel", "excel")) {
            return "统计分析建议：统计分析模块支持按日期 分类和部门汇总申请数量 受理数量 办结数量以及平均办理时长。后端提供 /api/statistics/export 导出 Excel 报表。";
        }
        return "已收到问题：“" + question + "”。当前系统可围绕政务事项录入 分类发布 在线申请 状态跟踪 材料补正 审批流程 统计报表等内容提供咨询。请补充事项名称或申请编号 可以给出更精确的办理建议。";
    }

    private List<String> buildSuggestions(String question, String scene) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("这个事项需要哪些材料");
        suggestions.add("审批流程有几个节点");
        suggestions.add("如何查看申请状态");
        if (containsAny(question + scene, "统计", "报表")) {
            suggestions.add("怎样导出 Excel 报表");
        } else {
            suggestions.add("材料不合格怎么补正");
        }
        return suggestions;
    }

    private boolean containsAny(String text, String... words) {
        for (String word : words) {
            if (text.contains(word)) return true;
        }
        return false;
    }

    private void saveHistory(String question, String scene, String answer) {
        try {
            String item = LocalDateTime.now() + " | " + scene + " | Q=" + question + " | A=" + answer;
            redisTemplate.opsForList().leftPush(HISTORY_KEY, item);
            redisTemplate.opsForList().trim(HISTORY_KEY, 0, 99);
            redisTemplate.expire(HISTORY_KEY, 7, TimeUnit.DAYS);
        } catch (Exception ignored) {
            // AI 历史写入 Redis 失败不影响回答
        }
    }
}
