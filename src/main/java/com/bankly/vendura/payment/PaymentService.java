package com.bankly.vendura.payment;

import com.bankly.vendura.payment.giftcard.model.GiftCard;
import com.bankly.vendura.payment.giftcard.transaction.GiftCardTransactionService;
import com.bankly.vendura.payment.model.*;
import com.bankly.vendura.sale.model.Sale;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class  PaymentService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

  private final GiftCardTransactionService giftCardTransactionService;
  private final PaymentRepository paymentRepository;

  /**
   * Save a payment to the database
   *
   * @param payment to be saved
   */
  public void createPayment(Payment payment) {
    this.paymentRepository.save(payment);
  }

  /**
   * Process a payment with a gift card and convert discount cards to fixed amount if needed
   *
   * @param payment to be processed
   * @param sale to be transacted on
   * @param remainingBalance as a result of previous payment processing regarding the same sale
   * @return whether the transaction was successful or not
   */
  public void processGiftCardPayment(GiftCardPayment payment, Sale sale, double remainingBalance) {
    LOGGER.debug(
        "Processing gift card payment {} with gift card {} for sale {}, remaining balance is {} €",
        payment.getId(),
        sale.getId(),
        payment.getGiftCard().getId(),
        remainingBalance);
      if (payment.getGiftCard().getExpirationDate().before(new Date())) {
        throw new IllegalArgumentException("Giftcard is expired!");
      }
      if (payment.getGiftCard().getType() == GiftCard.Type.DISCOUNT_CARD) {
      // round the following to two decimals
      double amount =
          Math.min(
              (double) Math.round(remainingBalance * payment.getGiftCard().getDiscountPercentage())
                  / 100,
              remainingBalance);
      payment.setAmount(amount);
      LOGGER.debug(
          "Converted discount card to fixed amount not greater than remaining balance: {}",
          payment.getAmount());
    }

    try {
      LOGGER.debug(
          "Creating transaction on gift card {} for payment {}",
          payment.getGiftCard().getId(),
          payment.getId());
      this.giftCardTransactionService.createTransaction(
          payment.getGiftCard(),
          -payment.getAmount(),
          payment,
          payment.getIssuer(),
          "Automatic charge on giftcard due to payment TX#"
              + payment.getId()
              + " on SALE#"
              + sale.getId());
    } catch (IllegalArgumentException e) {
      LOGGER.debug(
          "Transaction on gift card {} failed for payment {}",
          payment.getGiftCard().getId(),
          payment.getId());
      throw e;
    }

    payment.setStatus(Payment.Status.COMPLETED);
    this.paymentRepository.save(payment);
  }

  public boolean handlePaymentsOnSale(Sale sale) {
    StringBuilder errorBuilder = new StringBuilder();
    List<Payment> sortedPayments =
        sale.getPayments().stream()
            .sorted(Comparator.comparingInt(Payment::getPaymentHierarchy))
            .collect(Collectors.toList());
    double remainingAmount = sale.calculateTotal();
    System.out.println("remainingAmount: " + remainingAmount);
    for (Payment payment : sortedPayments) {
      if (remainingAmount <= 0) {
        System.out.println("remainingAmount <= 0");
        sale.getPayments().remove(payment);
        this.paymentRepository.delete(payment);
      }
      if (payment.getAmount() > remainingAmount) {
        payment.setAmount(remainingAmount);
      }

      boolean transactionSuccess = false;

      if (payment.getAmount() > remainingAmount) {
        payment.setAmount(remainingAmount);
      }

      if (payment instanceof CardPayment) {
        transactionSuccess = this.processCardPayment((CardPayment) payment);
      }

      if (payment instanceof GiftCardPayment) {
        try {
          this.processGiftCardPayment((GiftCardPayment) payment, sale, remainingAmount);
          transactionSuccess = true;
        } catch (IllegalArgumentException e) {
          errorBuilder
              .append("Payment ")
              .append(payment.getId())
              .append(" with giftcard")
              .append(((GiftCardPayment) payment).getGiftCard().getId())
              .append(" failed: ")
              .append(e.getMessage())
              .append("; ");
        }
      }

      if (payment instanceof CashPayment) {
        try {
          this.processCashPayment((CashPayment) payment);
          transactionSuccess = true;
        } catch (IllegalArgumentException e) {
          errorBuilder.append(
              "Payment " + payment.getId() + " with cash failed: " + e.getMessage() + "; ");
        }
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
      throw new IllegalArgumentException(
          "Payment processing failed: remaining amount is not zero: "
              + remainingAmount
              + " €; "
              + errorBuilder);
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
          payment.getAmount(),
          giftCardPayment,
          payment.getIssuer(),
          "Revert transaction on giftcard due to a fail of fulfilling payment TX#"
              + payment.getId());
    }

    this.paymentRepository.save(payment);
  }
}
