package com.bankly.vendura.inventory.transactions.product;

import com.bankly.vendura.inventory.product.model.ProductRepository;
import com.bankly.vendura.inventory.transactions.product.model.ProductTransactionDTO;
import com.bankly.vendura.inventory.transactions.product.model.ProductTransactionFactory;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/transaction/product")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductTransactionControllerV1 {

  private final ProductTransactionService productTransactionService;
  private final ProductRepository productRepository;

  /**
   * Get all transactions for a product
   *
   * @param productId Product ID
   * @param pageable Pagination parameters
   * @return Page of transactions
   */
  @GetMapping("/product/{productId}")
  @PreAuthorize("hasAnyRole('INVENTORY', 'ADMIN')")
  public ResponseEntity<Page<ProductTransactionDTO>> getTransactionsByProductId(
      @PathVariable("productId") String productId, Pageable pageable) {

    // Verify the product exists
    productRepository
        .findById(productId)
        .orElseThrow(
            () ->
                new EntityRetrieveException("Product not found", HttpStatus.NOT_FOUND, productId));

    // Get transactions
    Page<ProductTransactionDTO> transactions =
        productTransactionService
            .findTransactionsByProductId(productId, pageable)
            .map(ProductTransactionFactory::toDTO);

    return ResponseEntity.ok(transactions);
  }

  /**
   * Get all transactions in the system
   *
   * @param pageable Pagination parameters
   * @return Page of transactions
   */
  @GetMapping
  @PreAuthorize("hasAnyRole('INVENTORY', 'ADMIN')")
  public ResponseEntity<Page<ProductTransactionDTO>> getAllTransactions(Pageable pageable) {
    Page<ProductTransactionDTO> transactions =
        productTransactionService
            .findAllTransactions(pageable)
            .map(ProductTransactionFactory::toDTO);

    return ResponseEntity.ok(transactions);
  }
}
