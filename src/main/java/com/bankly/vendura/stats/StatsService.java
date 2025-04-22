package com.bankly.vendura.stats;

import com.bankly.vendura.inventory.product.model.Product;
import com.bankly.vendura.payment.model.*;
import com.bankly.vendura.sale.model.Sale;
import com.bankly.vendura.sale.model.SaleRepository;
import com.bankly.vendura.stats.model.SummaryDTO;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsService {

  private final SaleRepository saleRepository;
  private final PaymentRepository paymentRepository;

  public SummaryDTO getSummary(SummaryDTO.Period period) {
    Date date = new Date(System.currentTimeMillis() - (period.getDays() * 24 * 60 * 60 * 1000L));

    List<Sale> allSales = this.saleRepository.findAllByDateAfter(date);
    List<Sale> sales = allSales.stream().filter(sale -> sale.calculateTotal() > 0).toList();
    List<Sale> returns = allSales.stream().filter(sale -> sale.calculateTotal() < 0).toList();

    SummaryDTO summaryDTO =
        SummaryDTO.builder()
            .totalRevenue(sales.stream().mapToDouble(Sale::calculateTotal).sum())
            .totalTransactions(sales.size())
            .averageTransactionValue(
                sales.stream().mapToDouble(Sale::calculateTotal).average().orElse(0))
            .totalItemsSold(sales.stream().mapToLong(Sale::getAmountOfItems).sum())
            .totalDiscountAmount(sales.stream().mapToDouble(Sale::getTotalDiscount).sum())
            .totalReturns(returns.size())
            .totalReturnValue(
                returns.stream().mapToDouble(Sale::calculateTotal).average().orElse(0))
            .build();
    return summaryDTO;
  }

  public HashMap<String, Double> getTurnoverTrend(SummaryDTO.Period period) {
    Date date = new Date();

    // create a list with all the dates within the period
    List<Date> dates = new ArrayList<>();
    for (int i = 0; i < period.getDays(); i++) {
      dates.add(new Date(System.currentTimeMillis() - (i * 24 * 60 * 60 * 1000L)));
    }

    HashMap<String, Double> turnoverTrend = new HashMap<>();

    for (Date date1 : dates) {
      double saleTotal =
          this.saleRepository.findAllByDateBeforeAndDateAfter(date, date1).stream()
              .mapToDouble(Sale::calculateTotal)
              .sum();
      turnoverTrend.put(date1.toString(), saleTotal);
      date = date1;
    }

    return turnoverTrend;
  }

  public HashMap<String, Double> getPaymentMethodUsage(Date begin, Date end) {
    List<Payment> payments =
        this.paymentRepository.findAllByTimestampBeforeAndTimestampAfter(end, begin);

    HashMap<String, Double> paymentMethodUsage = new HashMap<>();

    for (PaymentDTO.Type value : PaymentDTO.Type.values()) {
      if (value == PaymentDTO.Type.CARD) {
        paymentMethodUsage.put(
            value.name(),
            payments.stream()
                .filter(payment -> payment instanceof CardPayment)
                .mapToDouble(Payment::getAmount)
                .sum());
      }
      if (value == PaymentDTO.Type.CASH) {
        paymentMethodUsage.put(
            value.name(),
            payments.stream()
                .filter(payment -> payment instanceof CashPayment)
                .mapToDouble(Payment::getAmount)
                .sum());
      }
      if (value == PaymentDTO.Type.GIFTCARD) {
        paymentMethodUsage.put(
            value.name(),
            payments.stream()
                .filter(payment -> payment instanceof GiftCardPayment)
                .mapToDouble(Payment::getAmount)
                .sum());
      }
    }

    return paymentMethodUsage;
  }

  public HashMap<String, Double> getTurnoverByProductSorted(int limit) {
    HashMap<Product, Double> turnoverByProduct = new HashMap<>();

    this.saleRepository.findAll().stream()
        .flatMap(sale -> sale.getPositions().stream())
        .forEach(
            position -> {
              turnoverByProduct.put(
                  position.getProduct(),
                  position.getPositionTotal()
                      + turnoverByProduct.getOrDefault(position.getProduct(), 0.0));
            });

    return turnoverByProduct.entrySet().stream()
        .sorted((set, set2) -> set.getValue().compareTo(set2.getValue()))
        .limit(limit)
        .collect(
            Collectors.toMap(
                e -> e.getKey().getId(), Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }



}
