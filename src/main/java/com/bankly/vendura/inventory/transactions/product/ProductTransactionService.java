package com.bankly.vendura.inventory.transactions.product;

import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.inventory.product.model.Product;
import com.bankly.vendura.inventory.transactions.product.model.ProductTransactable;
import com.bankly.vendura.inventory.transactions.product.model.ProductTransaction;
import com.bankly.vendura.inventory.transactions.product.model.ProductTransactionRepository;
import com.bankly.vendura.inventory.transactions.product.model.StockAggregationResult;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductTransactionService {

  private final MongoTemplate mongoTemplate;
  private final ProductTransactionRepository productTransactionRepository;

  public void createTransaction(
      Product product,
      long quantity,
      ProductTransactable transactionCause,
      User issuer,
      String message) {
    ProductTransaction productTransaction =
        ProductTransaction.builder()
            .product(product)
            .quantity(quantity)
            .transactionCause(transactionCause)
            .transactionType(transactionCause.getTransactionType())
            .issuer(issuer)
            .message(message)
            .timestamp(new Date())
            .build();
    this.productTransactionRepository.save(productTransaction);
  }

  public long calculateCurrentStock(Product product) {
    return this.calculateCurrentStock(product.getId());
  }

  public long calculateCurrentStock(String productId) {
    Aggregation aggregation =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("product._id").is(productId)),
            Aggregation.group("product._id").sum("quantity").as("quantity"));

    ProductTransaction result =
        this.mongoTemplate
            .aggregate(aggregation, ProductTransaction.class, ProductTransaction.class)
            .getUniqueMappedResult();

    return result == null ? 0 : result.getQuantity();
  }
}
