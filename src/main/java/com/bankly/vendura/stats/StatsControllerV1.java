package com.bankly.vendura.stats;

import com.bankly.vendura.stats.model.SummaryDTO;
import java.util.Date;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/stats")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsControllerV1 {

  private final StatsService statsService;

  @GetMapping("/summary")
  public ResponseEntity<SummaryDTO> getSummary(@RequestParam("period") String period) {
    return ResponseEntity.ok(this.statsService.getSummary(SummaryDTO.Period.fromString(period)));
  }

  @GetMapping("/paymentMethods")
  public ResponseEntity<HashMap<String, Double>> getPaymentMethods(
      @RequestParam("period") String period) {
    return ResponseEntity.ok(
        this.statsService.getPaymentMethodUsage(
            new Date(
                System.currentTimeMillis()
                    - (long) SummaryDTO.Period.fromString(period).getDays() * 24 * 60 * 60 * 1000),
            new Date()));
  }
}
