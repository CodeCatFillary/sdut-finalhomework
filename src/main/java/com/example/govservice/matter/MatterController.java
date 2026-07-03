package com.example.govservice.matter;

import com.example.govservice.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matters")
public class MatterController {
    private final MatterService matterService;

    public MatterController(MatterService matterService) {
        this.matterService = matterService;
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(@RequestParam(required = false) String category,
                                                       @RequestParam(required = false) String status,
                                                       @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(matterService.list(category, status, keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> get(@PathVariable String id) {
        return ApiResponse.ok(matterService.get(id));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        return ApiResponse.ok(matterService.create(body));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ApiResponse.ok(matterService.update(id, body));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        matterService.delete(id);
        return ApiResponse.ok("删除成功", null);
    }

    @PostMapping("/{id}/publish")
    public ApiResponse<Map<String, Object>> publish(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ApiResponse.ok(matterService.publish(id, body));
    }

    @GetMapping("/categories")
    public ApiResponse<List<Map<String, Object>>> categories() {
        return ApiResponse.ok(matterService.categories());
    }

    @PostMapping("/categories")
    public ApiResponse<Map<String, Object>> createCategory(@RequestBody Map<String, Object> body) {
        return ApiResponse.ok(matterService.createCategory(body));
    }
}
