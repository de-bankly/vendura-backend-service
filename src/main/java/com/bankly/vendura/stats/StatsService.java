package com.bankly.vendura.stats;

import com.bankly.vendura.sale.SaleControllerV1;
import com.bankly.vendura.sale.SaleService;
import com.bankly.vendura.sale.model.Sale;
import com.bankly.vendura.sale.model.SaleRepository;
import com.bankly.vendura.stats.model.SummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private SaleRepository saleRepository;

    public SummaryDTO getSummary(SummaryDTO.Period period) {
        Date date = new Date(System.currentTimeMillis() - (period.getDays() * 24 * 60 * 60 * 1000L));

        List<Sale> allSales = this.saleRepository.findAllByDateAfter(date);
        List<Sale> sales = allSales.stream().filter(sale -> sale.calculateTotal() > 0).toList();
        List<Sale> returns = allSales.stream().filter(sale -> sale.calculateTotal() < 0).toList();

        SummaryDTO summaryDTO = SummaryDTO.builder()
                .totalRevenue(sales.stream().mapToDouble(Sale::calculateTotal).sum())
                .totalTransactions(sales.size())
                .averageTransactionValue(sales.stream().mapToDouble(Sale::calculateTotal).average().orElse(0))
                .totalItemsSold(sales.stream().mapToLong(Sale::getAmountOfItems).sum())
                .totalDiscountAmount(sales.stream().mapToDouble(Sale::getTotalDiscount).sum())
                .totalReturns(returns.size())
                .totalReturnValue(returns.stream().mapToDouble(Sale::calculateTotal).average().orElse(0))
                .build();
        return summaryDTO;
    }

}
