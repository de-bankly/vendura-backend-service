package com.bankly.vendura.sale;

import com.bankly.vendura.inventory.product.ProductService;
import com.bankly.vendura.inventory.product.model.Product;
import com.bankly.vendura.inventory.product.model.ProductDTO;
import com.bankly.vendura.payment.PaymentService;
import com.bankly.vendura.payment.giftcard.GiftCardService;
import com.bankly.vendura.payment.model.CashPayment;
import com.bankly.vendura.sale.model.Sale;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Date;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SalePaymentTest {


}
