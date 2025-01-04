package org.example;

import java.util.ArrayList;
import java.util.List;

public class DataValidation {

    public static List<String[]> validateCardData(List<CreditCard> cards) {
        List<String[]> results = new ArrayList<>();

        for (CreditCard card : cards) {
            // Validate Annual Fee
            String annualFeeStr = card.getAnnualFee() >= 0 ? String.valueOf(card.getAnnualFee()) : "Invalid";
            String annualFeeStatus = card.getAnnualFee() >= 0 ? "Valid" : "Invalid";

            // Validate Interest Rate
            String interestRateStr = card.getInterestRate() >= 0 ? String.valueOf(card.getInterestRate()) : "Invalid";
            String interestRateStatus = card.getInterestRate() >= 0 ? "Valid" : "Invalid";

            // Validate Rewards (non-empty and no special characters)
            String rewardsStr = card.getRewards();
            String rewardsStatus = rewardsStr != null && !rewardsStr.trim().isEmpty() && rewardsStr.matches("[a-zA-Z0-9 ]*") ? "Valid" : "Invalid";

            // Add validation status
            results.add(new String[]{
                    card.getName(),
                    annualFeeStr, annualFeeStatus,
                    interestRateStr, interestRateStatus,
                    rewardsStr, rewardsStatus
            });
        }

        return results;
    }
}
