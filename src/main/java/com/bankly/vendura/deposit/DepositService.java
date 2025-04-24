package com.bankly.vendura.deposit;

import com.bankly.vendura.deposit.model.DepositReceipt;
import com.bankly.vendura.deposit.model.DepositReceiptDTO;
import com.bankly.vendura.deposit.model.DepositReceiptFactory;
import com.bankly.vendura.deposit.model.DepositReceiptRepository;
import com.bankly.vendura.inventory.product.ProductService;
import com.bankly.vendura.inventory.product.model.Product;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
      position.setProduct(this.productService.getProductEntityById(position.getProduct().getId()));
    }
    depositReceipt.setId(generateId());

    return this.depositReceiptRepository.save(depositReceipt);
  }

  private String generateId() {
    String baseId;
    String fullEan13Id;
    do {
      baseId = generateRandomDigits(12);
      int checkDigit = calculateEan13CheckDigit(baseId);
      fullEan13Id = baseId + checkDigit;
    } while (this.depositReceiptRepository.existsById(fullEan13Id));
    return fullEan13Id;
  }

  private String generateRandomDigits(int length) {
    StringBuilder id = new StringBuilder();
    for (int i = 0; i < length; i++) {
      id.append((int) (Math.random() * 10));
    }
    return id.toString();
  }

  private int calculateEan13CheckDigit(String data) {
    if (data == null || data.length() != 12) {
        throw new IllegalArgumentException("Input data must be 12 digits long.");
    }

    int sumOdd = 0;
    int sumEven = 0;

    for (int i = 0; i < 12; i++) {
        int digit = Character.getNumericValue(data.charAt(i));
        if ((i + 1) % 2 == 0) { // even position (2, 4, ..., 12)
            sumEven += digit;
        } else { // odd position (1, 3, ..., 11)
            sumOdd += digit;
        }
    }

    int totalSum = sumOdd + (sumEven * 3);
    int remainder = totalSum % 10;
    return (remainder == 0) ? 0 : (10 - remainder);
  }

  public void deleteDepositReceipt(String id) {
    this.depositReceiptRepository.delete(this.getDepositReceiptById(id));
  }

  public List<DepositReceipt.Position> getDepositReceiptsByProductId(String productId) {
    Product product = this.productService.getProductEntityById(productId);
    return product.getConnectedProductsIndefinite().stream().map(positionsProduct -> new DepositReceipt.Position(0, positionsProduct)).collect(Collectors.toList());
  }
}
