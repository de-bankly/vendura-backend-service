package com.bankly.vendura.payment;

import com.bankly.vendura.inventory.product.ProductService;
import com.bankly.vendura.inventory.product.model.Product;
import com.bankly.vendura.inventory.product.model.ProductDTO;
import com.bankly.vendura.payment.model.CashPayment;
import com.bankly.vendura.payment.model.Payment;
import com.bankly.vendura.payment.model.PaymentRepository;
import com.bankly.vendura.sale.model.Sale;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LogicalPaymentTest {

  private final ProductService productService;
  private final PaymentRepository paymentRepository;
  private final PaymentService paymentService;

  private Product testProduct;
  private Sale testSale;
  private final Set<Payment> createdPayments = new HashSet<>();

  @BeforeAll
  public void prepareTests() {
    this.testProduct =
        this.productService.create(
            ProductDTO.builder()
                .name("Test Product1")
                .priceHistories(
                    Arrays.asList(new ProductDTO.PriceHistoryDTO(new Date(), 5, 10, null)))
                .build());

    this.testSale = new Sale();
    this.testSale.getPositions().add(new Sale.Position(testProduct, 2, 0));
  }

  @AfterAll
  public void cleanUp() {
    this.productService.delete(this.testProduct.getId());
    this.paymentRepository.deleteAll(this.createdPayments);
  }

  @Test
  public void testFailureOnNoPaymentAtAll() {
    this.testSale.getPayments().clear();
    Assertions.assertFalse(this.paymentService.handlePaymentsOnSale(this.testSale));
  }

  @Test
  public void testFailureOnLowPayment() {
    this.testSale.getPayments().clear();
    Payment payment = new CashPayment(new Date(), 10, null, 10, Payment.Status.PENDING);
    this.createdPayments.add(payment);
    this.testSale.getPayments().add(payment);
    Assertions.assertFalse(this.paymentService.handlePaymentsOnSale(this.testSale));
  }
}
