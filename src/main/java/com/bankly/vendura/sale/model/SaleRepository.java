package com.bankly.vendura.sale.model;

import lombok.Data;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface SaleRepository extends MongoRepository<Sale, String> {

    List<Sale> findAllByDateAfter(Date date);
    List<Sale> findAllByDateBeforeAndDateAfter(Date dateBefore, Date dateAfter);

}
