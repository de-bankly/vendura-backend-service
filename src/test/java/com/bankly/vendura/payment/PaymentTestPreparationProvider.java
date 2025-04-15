package com.bankly.vendura.payment;

import com.bankly.vendura.inventory.product.ProductService;
import com.bankly.vendura.payment.model.PaymentRepository;
import com.bankly.vendura.sale.model.Sale;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PaymentTestPreparationProvider {

    private final ProductService productService;
    private final PaymentRepository paymentRepository;

    @Getter
    @AllArgsConstructor
    public static class TestObjects {

        private final Sale testSale;

    }

}
