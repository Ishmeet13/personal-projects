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

public class RBCCardScraper {

    public static void scrapeRBCCards(WebDriver driver, WebDriverWait wait, FileWriter csvWriter) throws IOException {
        driver.get("https://www.rbcroyalbank.com/credit-cards/all-credit-cards.html#all-cards");

        handlePopUps(driver, wait);

        boolean hasNextPage = true;
        while (hasNextPage) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".grid-one-third.card-result")));
            List<WebElement> cards = driver.findElements(By.cssSelector(".grid-one-third.card-result"));

            for (WebElement card : cards) {
                try {
                    String cardType = cleanText(card.findElement(By.cssSelector(".callout-inner p.text-center")).getText().trim());
                    String annualFee = extractAnnualFee(card);
                    String purchaseRate = extractRate(card, 2);
                    String cashAdvanceRate = extractRate(card, 3);
                    StringBuilder offers = new StringBuilder();
                    for (WebElement offer : card.findElements(By.cssSelector(".disc-list li"))) {
                        offers.append(cleanText(offer.getText())).append(" ");
                    }

                    // Write the data in the updated sequence
                    csvWriter.append(String.format("RBC Bank,%s,%s,%s,%s\n",
                            escapeCSV(cardType), escapeCSV(annualFee), escapeCSV(purchaseRate), escapeCSV(offers.toString())));

                } catch (NoSuchElementException e) {
                    System.out.println("Error processing card: " + e.getMessage());
                }
            }

            try {
                WebElement nextButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Next")));
                nextButton.click();
            } catch (Exception e) {
                hasNextPage = false;
            }
        }
    }

    private static String extractAnnualFee(WebElement card) {
        try {
            return cleanText(card.findElements(By.cssSelector(".row .col-xs-5 p")).get(0).getText());
        } catch (IndexOutOfBoundsException | NoSuchElementException e) {
            return "N/A";
        }
    }

    private static String extractRate(WebElement card, int index) {
        try {
            return cleanText(card.findElements(By.cssSelector(".row .col-xs-5 p")).get(index).getText());
        } catch (IndexOutOfBoundsException | NoSuchElementException e) {
            return "N/A";
        }
    }

    private static void handlePopUps(WebDriver driver, WebDriverWait wait) {
        try {
            WebElement popUpCloseButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".popup-close-button-selector")));
            popUpCloseButton.click();
        } catch (Exception e) {
            System.out.println("No pop-up appeared.");
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
