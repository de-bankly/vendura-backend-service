package com.bankly.vendura.inventory.promotion.model;

import com.bankly.vendura.inventory.product.model.Product;
import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PromotionRepository extends MongoRepository<Promotion, String> {

  List<Promotion> findAllByProductAndBeginBeforeAndEndAfter(
      Product product, Date beginBefore, Date endAfter);
}
