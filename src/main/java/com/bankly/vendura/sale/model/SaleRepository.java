package com.bankly.vendura.sale.model;

import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SaleRepository extends MongoRepository<Sale, String> {

  List<Sale> findAllByDateAfter(Date date);

  List<Sale> findAllByDateBeforeAndDateAfter(Date dateBefore, Date dateAfter);
}
