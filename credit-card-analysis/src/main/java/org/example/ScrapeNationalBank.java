package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ScrapeNationalBank {

    public static void scrapeNationalBankCards(WebDriver driver, WebDriverWait wait, FileWriter csvWriter) {
        try {
            // Navigate to the National Bank credit cards page
            driver.get("https://www.bmo.com/main/personal/credit-cards/all-cards/");

            // Wait for the cards section to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".css-1h8a5bm")));

            // Get all card sections
            List<WebElement> cards = driver.findElements(By.cssSelector(".css-1h8a5bm"));

            // Process each card
            for (WebElement card : cards) {
                String cardName = "N/A", annualFee = "N/A", purchaseInterestRate = "N/A", rewards = "N/A";

                // Extract card details
                try {
                    cardName = card.findElement(By.cssSelector(".css-1x3x2ym.e7r50om0 h2.css-0 span[role='text']"))
                            .getText().replaceAll("[®*™<>\\[\\]()]", "").trim();
                } catch (NoSuchElementException e) {
                    System.out.println("Card name not found.");
                }

                try {
                    annualFee = card.findElement(By.cssSelector(".css-1x3x2ym.e7r50om0 > span")).getText().trim();
                } catch (NoSuchElementException e) {
                    System.out.println("Annual fee not found for card: " + cardName);
                }

                try {
                    purchaseInterestRate = card.findElement(By.cssSelector(".css-1x3x2ym.e7r50om0 > span[data-meta-api*='purchaseInterestRate']"))
                            .getText().trim();
                } catch (NoSuchElementException e) {
                    System.out.println("Purchase interest rate not found for card: " + cardName);
                }

                try {
                    rewards = card.findElement(By.cssSelector("span[data-meta-api*='welcomeOffer']")).getText().trim();
                } catch (NoSuchElementException e) {
                    System.out.println("Rewards information not found for card: " + cardName);
                }

                // Write card details to CSV
                csvWriter.append(String.format("\"Bank of Montreal\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        cardName.replace("\"", "\"\""), annualFee.replace("\"", "\"\""),
                        purchaseInterestRate.replace("\"", "\"\""), rewards.replace("\"", "\"\"")));
            }

        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error scraping National Bank cards: " + e.getMessage());
        }
    }
}
