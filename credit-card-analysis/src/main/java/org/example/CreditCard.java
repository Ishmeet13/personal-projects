package org.example;

public class CreditCard {
    private String name;
    private String bankName;
    private double annualFee;
    private double interestRate;
    private String rewards;

    public CreditCard(String name, String bankName, double annualFee, double interestRate, String rewards) {
        this.name = name;
        this.bankName = bankName;
        this.annualFee = annualFee;
        this.interestRate = interestRate;
        this.rewards = rewards;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getBankName() {
        return bankName;
    }

    public double getAnnualFee() {
        return annualFee;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public String getRewards() {
        return rewards;
    }
}
