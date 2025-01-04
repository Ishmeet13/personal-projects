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

public class CIBCCardScraper {

    public static void scrapeCIBCCards(WebDriver driver, WebDriverWait wait, FileWriter csvWriter) throws IOException {
        driver.get("https://www.cibc.com/en/personal-banking/credit-cards/all-credit-cards.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("product-title-link")));
        List<WebElement> cards = driver.findElements(By.cssSelector(".result"));

        for (WebElement card : cards) {
            try {
                String cardName = cleanText(card.findElement(By.cssSelector(".product-title-link.marketingLink")).getText());
                String annualFee = extractValue(card, ".//div[contains(text(), 'Annual fee')]/following-sibling::div[@class='product-rate-value']/b");
                String purchaseInterestRate = extractValue(card, ".//div[contains(text(), 'Purchase interest rate')]/following-sibling::div[@class='product-rate-value']/b/span");
                String cashInterestRate = extractCashInterestRate(card);
                String rewards = extractRewards(card);

                csvWriter.append(String.format("CIBC Bank,%s,%s,%s,%s\n",
                        escapeCSV(cardName), escapeCSV(annualFee), escapeCSV(purchaseInterestRate), escapeCSV(rewards)));
            } catch (NoSuchElementException e) {
                System.out.println("Error processing card: " + e.getMessage());
            }
        }
    }

    private static String extractCashInterestRate(WebElement card) {
        try {
            // Locate the <p> tag that contains "non-Quebec residents" text
            List<WebElement> rateElements = card.findElements(By.cssSelector(".product-rate-value p"));
            for (WebElement rateElement : rateElements) {
                if (rateElement.getText().toLowerCase().contains("non-quebec residents")) {
                    return cleanText(rateElement.findElement(By.cssSelector("b span")).getText());
                }
            }
        } catch (NoSuchElementException e) {
            return "N/A";
        }
        return "N/A";
    }

    private static String extractValue(WebElement card, String xpath) {
        try {
            return cleanText(card.findElement(By.xpath(xpath)).getText());
        } catch (NoSuchElementException e) {
            return "N/A";
        }
    }

    private static String extractRewards(WebElement card) {
        try {
            // Extract rewards using the CSS class "product-description"
            return cleanText(card.findElement(By.cssSelector(".product-description")).getText());
        } catch (NoSuchElementException e) {
            return "N/A";
        }
    }

    private static String cleanText(String text) {
        return text.replaceAll("[®*™<>\\[\\]()]", "").replaceAll("\\s+", " ").trim();
    }

    private static String escapeCSV(String value) {
        if (value.contains("\"")) {
            value = value.replace("\"", "\"\"");
        }
        return "\"" + value + "\"";
    }
}
