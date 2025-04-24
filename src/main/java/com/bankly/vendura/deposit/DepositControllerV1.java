package com.bankly.vendura.deposit;

import com.bankly.vendura.deposit.model.DepositReceiptDTO;
import com.bankly.vendura.deposit.model.DepositReceiptFactory;
import com.bankly.vendura.utilities.ValidationGroup;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/depositreceipt")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DepositControllerV1 {

  private final DepositService depositService;

  @GetMapping
  public ResponseEntity<Page<DepositReceiptDTO>> getAllDepositReceipts(Pageable pageable) {
    return ResponseEntity.ok(
        this.depositService.getAllDepositReceipts(pageable).map(DepositReceiptFactory::toDTO));
  }

  @GetMapping("/{id}")
  public ResponseEntity<DepositReceiptDTO> getDepositReceiptById(@PathVariable("id") String id) {
    return ResponseEntity.ok(
        DepositReceiptFactory.toDTO(this.depositService.getDepositReceiptById(id)));
  }

  @GetMapping("/positions/{productId}")
  public ResponseEntity<List<DepositReceiptDTO.PositionDTO>> getDepositReceiptsByProductId(
      @PathVariable("productId") String productId) {
    return ResponseEntity.ok(
        this.depositService.getDepositReceiptsByProductId(productId).stream()
            .map(DepositReceiptFactory::toDTO)
            .toList());
  }

  @PostMapping
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<DepositReceiptDTO> createDepositReceipt(
      @RequestBody @Validated(ValidationGroup.Create.class) DepositReceiptDTO depositReceiptDTO) {
    return ResponseEntity.ok(
        DepositReceiptFactory.toDTO(this.depositService.createDepositReceipt(depositReceiptDTO)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<Void> deletePromotion(@PathVariable("id") String id) {
    this.depositService.deleteDepositReceipt(id);
    return ResponseEntity.noContent().build();
  }
}
