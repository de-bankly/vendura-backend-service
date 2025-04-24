package com.bankly.vendura.stats;

import com.bankly.vendura.inventory.product.model.Product;
import com.bankly.vendura.inventory.product.model.ProductDTO;
import com.bankly.vendura.sale.model.TopSellingProductsDTO;
import com.bankly.vendura.stats.model.SummaryDTO;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

  @GetMapping("/topSellingProducts")
  public ResponseEntity<TopSellingProductsDTO> getTopSellingProducts(
      @RequestParam(value = "period", required = false, defaultValue = "MONTH") String period,
      @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
    return ResponseEntity.ok(
        this.statsService.getProductsTopSelling(SummaryDTO.Period.fromString(period), limit));
  }
}
