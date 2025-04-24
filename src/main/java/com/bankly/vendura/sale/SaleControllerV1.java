package com.bankly.vendura.sale;

import com.bankly.vendura.sale.model.SaleDTO;
import com.bankly.vendura.sale.model.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/v1/sale")
public class SaleControllerV1 {

  private final SaleService saleService;
  private final SaleRepository saleRepository;
  private final DynamicSaleFactory dynamicSaleFactory;

  @GetMapping
  public ResponseEntity<Page<SaleDTO>> findAll(Pageable pageable) {
    return ResponseEntity.ok(
        this.saleRepository.findAll(pageable).map(this.dynamicSaleFactory::toDTO));
  }

  @PostMapping
  public ResponseEntity<?> submitSaleToProcess(@RequestBody SaleDTO saleDTO) {
    // try {
    return ResponseEntity.ok(this.saleService.submitSaleToProcess(saleDTO));
    // } catch (IllegalArgumentException e) {
    //  return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    // }
  }
}
