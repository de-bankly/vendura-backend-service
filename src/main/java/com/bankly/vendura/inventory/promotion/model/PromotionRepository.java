package com.bankly.vendura.inventory.promotion.model;

import com.bankly.vendura.inventory.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface PromotionRepository extends MongoRepository<Promotion, String> {

    List<Promotion> findAllByProductAndBeginBeforeAndEndAfter(Product product, Date beginBefore, Date endAfter);

}
