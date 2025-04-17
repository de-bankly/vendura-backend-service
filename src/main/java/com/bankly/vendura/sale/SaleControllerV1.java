package com.bankly.vendura.sale;

import com.bankly.vendura.sale.model.SaleDTO;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/v1/sale")
public class SaleControllerV1 {

  private final SaleService saleService;

  @PostMapping
  public ResponseEntity<?> submitSaleToProcess(@RequestBody SaleDTO saleDTO) {
    //try {
      return ResponseEntity.ok(this.saleService.submitSaleToProcess(saleDTO));
    //} catch (IllegalArgumentException e) {
    //  return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    //}
  }
}
