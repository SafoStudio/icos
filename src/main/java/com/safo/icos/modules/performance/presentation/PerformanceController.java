package com.safo.icos.modules.performance.presentation;

import com.safo.icos.modules.performance.application.OkrService;
import com.safo.icos.modules.performance.application.PerformanceService;
import com.safo.icos.modules.performance.domain.model.Objective;
import com.safo.icos.modules.performance.domain.model.PerformanceReview;
import com.safo.icos.security.context.IcosUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceController {

    private final OkrService okrService;
    private final PerformanceService performanceService;

    @PostMapping("/okrs")
    public ResponseEntity<Objective> createObjective(
            @AuthenticationPrincipal IcosUserPrincipal principal,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(okrService.createObjective(principal.getUserId(), title, description, startDate, endDate));
    }

    @PostMapping("/okrs/{objectiveId}/key-results")
    public ResponseEntity<?> addKeyResult(
            @PathVariable UUID objectiveId,
            @RequestParam String title,
            @RequestParam BigDecimal target,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) BigDecimal weight) {
        return ResponseEntity.ok(okrService.addKeyResult(objectiveId, title, target, unit, weight));
    }

    @PatchMapping("/key-results/{id}/progress")
    public ResponseEntity<?> updateProgress(@PathVariable UUID id, @RequestParam BigDecimal value) {
        return ResponseEntity.ok(okrService.updateKeyResultProgress(id, value));
    }

    @PostMapping("/reviews/{id}/finalize")
    public ResponseEntity<PerformanceReview> finalizeReview(
            @AuthenticationPrincipal IcosUserPrincipal principal,
            @PathVariable UUID id,
            @RequestParam Integer rating,
            @RequestParam String summary) {
        return ResponseEntity.ok(performanceService.finalizeReview(id, rating, summary));
    }
}
