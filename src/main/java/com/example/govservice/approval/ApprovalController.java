package com.example.govservice.approval;

import com.example.govservice.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approval")
public class ApprovalController {
    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping("/flows")
    public ApiResponse<List<Map<String, Object>>> flows(@RequestParam(required = false) String keyword,
                                                        @RequestParam(required = false) String status) {
        return ApiResponse.ok(approvalService.flows(keyword, status));
    }

    @GetMapping("/flows/{id}")
    public ApiResponse<Map<String, Object>> getFlow(@PathVariable String id) {
        return ApiResponse.ok(approvalService.getFlow(id));
    }

    @PostMapping("/flows")
    public ApiResponse<Map<String, Object>> createFlow(@RequestBody Map<String, Object> body) {
        return ApiResponse.ok(approvalService.createFlow(body));
    }

    @PutMapping("/flows/{id}")
    public ApiResponse<Map<String, Object>> updateFlow(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ApiResponse.ok(approvalService.updateFlow(id, body));
    }

    @DeleteMapping("/flows/{id}")
    public ApiResponse<Void> deleteFlow(@PathVariable String id) {
        approvalService.deleteFlow(id);
        return ApiResponse.ok("删除成功", null);
    }

    @PostMapping("/flows/{flowId}/nodes")
    public ApiResponse<Map<String, Object>> createNode(@PathVariable String flowId, @RequestBody Map<String, Object> body) {
        return ApiResponse.ok(approvalService.createNode(flowId, body));
    }

    @PutMapping("/flows/{flowId}/nodes/{nodeId}")
    public ApiResponse<Map<String, Object>> updateNode(@PathVariable String flowId, @PathVariable String nodeId, @RequestBody Map<String, Object> body) {
        return ApiResponse.ok(approvalService.updateNode(flowId, nodeId, body));
    }

    @DeleteMapping("/flows/{flowId}/nodes/{nodeId}")
    public ApiResponse<Map<String, Object>> deleteNode(@PathVariable String flowId, @PathVariable String nodeId) {
        return ApiResponse.ok(approvalService.deleteNode(flowId, nodeId));
    }

    @GetMapping("/records")
    public ApiResponse<List<Map<String, Object>>> records(@RequestParam(required = false) String applicationId,
                                                          @RequestParam(required = false) String flowId) {
        return ApiResponse.ok(approvalService.records(applicationId, flowId));
    }

    @PostMapping("/records")
    public ApiResponse<Map<String, Object>> createRecord(@RequestBody Map<String, Object> body) {
        return ApiResponse.ok(approvalService.createRecord(body));
    }
}
