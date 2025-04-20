package com.bankly.vendura.deposit;

import com.bankly.vendura.deposit.model.DepositReceipt;
import com.bankly.vendura.deposit.model.DepositReceiptDTO;
import com.bankly.vendura.deposit.model.DepositReceiptFactory;
import com.bankly.vendura.deposit.model.DepositReceiptRepository;
import com.bankly.vendura.inventory.product.ProductService;
import com.bankly.vendura.inventory.product.model.Product;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DepositService {

  private final DepositReceiptRepository depositReceiptRepository;
  private final ProductService productService;

  public Page<DepositReceipt> getAllDepositReceipts(Pageable pageable) {
    return this.depositReceiptRepository.findAll(pageable);
  }

  public DepositReceipt getDepositReceiptById(String id) {
    return this.depositReceiptRepository
        .findById(id)
        .orElseThrow(
            () ->
                new EntityRetrieveException("Deposit receipt not found", HttpStatus.NOT_FOUND, id));
  }

  public DepositReceipt createDepositReceipt(DepositReceiptDTO depositReceiptDTO) {
    DepositReceipt depositReceipt = DepositReceiptFactory.toEntity(depositReceiptDTO);
    for (DepositReceipt.Position position : depositReceipt.getPositions()) {
      System.out.println("A" + position.getProduct().getId());
      position.setProduct(this.productService.getProductEntityById(position.getProduct().getId()));
    }
    depositReceipt.setId(generateId());

    return this.depositReceiptRepository.save(depositReceipt);
  }

  private String generateId() {
    StringBuilder id;
    do {
      id = new StringBuilder();
      for (int i = 0; i < 10; i++) {
        id.append((int) (Math.random() * 10));
      }
    } while (this.depositReceiptRepository.existsById(id.toString()));
    return id.toString();
  }

  public void deleteDepositReceipt(String id) {
    this.depositReceiptRepository.delete(this.getDepositReceiptById(id));
  }

  public List<DepositReceipt.Position> getDepositReceiptsByProductId(String productId) {
    Product product = this.productService.getProductEntityById(productId);
    return product.getConnectedProductsIndefinite().stream().map(positionsProduct -> new DepositReceipt.Position(0, positionsProduct)).collect(Collectors.toList());
  }
}
