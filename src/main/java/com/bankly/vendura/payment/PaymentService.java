package com.bankly.vendura.payment;

import com.bankly.vendura.payment.giftcard.transaction.GiftCardTransactionService;
import com.bankly.vendura.payment.model.*;
import com.bankly.vendura.sale.model.Sale;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PaymentService {

  private final GiftCardTransactionService giftCardTransactionService;
  private final PaymentRepository paymentRepository;

  public Payment createPayment(Payment payment) {
    return this.paymentRepository.save(payment);
  }

  public boolean processGiftCardPayment(GiftCardPayment payment, Sale sale) {
    try {
      this.giftCardTransactionService.createTransaction(
          payment.getGiftCard(),
          payment.getAmount(),
          payment,
          payment.getIssuer(),
          "Automatic charge on giftcard due to payment TX#"
              + payment.getId()
              + " on SALE#"
              + sale.getId());
    } catch (IllegalArgumentException e) {
      return false;
    }

    payment.setStatus(Payment.Status.COMPLETED);
    this.paymentRepository.save(payment);
    return true;
  }

  public boolean handlePaymentsOnSale(Sale sale) {
    List<Payment> sortedPayments = sale.getPayments().stream().sorted(Comparator.comparingInt(Payment::getPaymentHierarchy)).collect(Collectors.toList());
    double remainingAmount = sale.calculateTotal();
    System.out.println("remainingAmount: " + remainingAmount);
    for (Payment payment : sortedPayments) {
      if (remainingAmount <= 0) {
        System.out.println("remainingAmount <= 0");
        sale.getPayments().remove(payment);
        this.paymentRepository.delete(payment);
      }

      boolean transactionSuccess = false;

      if (payment.getAmount() > remainingAmount) {
        payment.setAmount(remainingAmount);
      }

      if (payment instanceof CardPayment) {
        transactionSuccess = this.processCardPayment((CardPayment) payment);
      }

      if (payment instanceof GiftCardPayment) {
        transactionSuccess = this.processGiftCardPayment((GiftCardPayment) payment, sale);
      }

      if (payment instanceof CashPayment) {
        transactionSuccess = this.processCashPayment((CashPayment) payment);
        System.out.println(transactionSuccess + " succeeded");
      }

      if (!transactionSuccess) {
        revertAllTransactionsAndCancelSale(sale);
        return false;
      }

      remainingAmount -= payment.getAmount();
      payment.setStatus(Payment.Status.COMPLETED);
      this.paymentRepository.save(payment);
    }

    System.out.println("Remaining at the end of all payments: " + remainingAmount);
    if (remainingAmount != 0) {
      revertAllTransactionsAndCancelSale(sale);
      return false;
    }

    return true;
  }

  private boolean processCashPayment(CashPayment payment) {
    if (payment.getHanded() < payment.getAmount()) {
      payment.setStatus(Payment.Status.FAILED);
      payment.setReturned(payment.getHanded());
      this.paymentRepository.save(payment);
      return false;
    }

    payment.setReturned(payment.getHanded() - payment.getAmount());
    payment.setStatus(Payment.Status.COMPLETED);
    this.paymentRepository.save(payment);

    return true;
  }

  private boolean processCardPayment(CardPayment payment) {
    payment.setStatus(Payment.Status.COMPLETED);
    return true;
  }

  private void revertAllTransactionsAndCancelSale(Sale sale) {
    List<Payment> paymentsToRevert =
        sale.getPayments().stream().filter(p -> p.getStatus() == Payment.Status.COMPLETED).toList();

    for (Payment payment : paymentsToRevert) {
      this.revertPayment(payment);
    }
  }

  private void revertPayment(Payment payment) {
    payment.setStatus(Payment.Status.REVERTED);

    if (payment instanceof GiftCardPayment) {
      GiftCardPayment giftCardPayment = (GiftCardPayment) payment;
      this.giftCardTransactionService.createTransaction(
          giftCardPayment.getGiftCard(),
          -payment.getAmount(),
          giftCardPayment,
          payment.getIssuer(),
          "Revert transaction on giftcard due to a fail of fulfilling payment TX#"
              + payment.getId());
    }

    this.paymentRepository.save(payment);
  }
}
