package com.bankly.vendura.stats.model;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SummaryDTO {

    private double totalRevenue;
    private long totalTransactions;
    private double averageTransactionValue;
    private long totalItemsSold;
    private double totalDiscountAmount;
    private long totalReturns;
    private double totalReturnValue;

    @Getter
    @AllArgsConstructor
    public enum Period {
        DAY(1), WEEK(7), MONTH(30), YEAR(360);

        private int days;

        public static Period fromString(String period) {
            return switch (period.toUpperCase()) {
                case "DAY" -> DAY;
                case "WEEK" -> WEEK;
                case "MONTH" -> MONTH;
                case "YEAR" -> YEAR;
                default -> throw new IllegalArgumentException("Invalid period: " + period);
            };
        }
    }

}
