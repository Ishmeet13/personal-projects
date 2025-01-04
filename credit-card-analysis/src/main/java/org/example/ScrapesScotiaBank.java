package org.example; // Ensure the correct package is here

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScrapesScotiaBank {

    public static void scrapeScotiaBankCards(WebDriver driver, WebDriverWait wait, FileWriter csvWriter) {
        // Open the Scotiabank credit cards page
        driver.get("https://www.scotiabank.com/ca/en/personal/credit-cards/compare-cards.html");

        try {
            // Loop through all the pages
            boolean hasNextPage = true;
            while (hasNextPage) {
                // Handle potential pop-ups
                handlePopUp(driver, wait);

                // Wait until the card content loads
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("card-content")));

                // Get all the card sections on the current page
                List<WebElement> cards = driver.findElements(By.cssSelector(".card-content"));

                // Extract data from each card
                for (WebElement card : cards) {
                    try {
                        // Get the category (cleaned text from the callout element)
                        WebElement categoryElement = card.findElement(By.xpath("preceding-sibling::div[contains(@class, 'callout')]"));
                        String category = cleanText(categoryElement.getText().trim());

                        // Get the card type (cleaned text from the subtitle-1 element)
                        WebElement cardTypeElement = card.findElement(By.cssSelector(".subtitle-1"));
                        String cardType = cleanText(cardTypeElement.getText().trim());

                        // Get offers (concatenate all <p> elements within the card-content)
                        StringBuilder offersBuilder = new StringBuilder();
                        List<WebElement> offerElements = card.findElements(By.tagName("p"));
                        for (WebElement offerElement : offerElements) {
                            offersBuilder.append(cleanText(offerElement.getText().trim())).append(" ");
                        }
                        String offers = offersBuilder.toString().trim();

                        // Get the annual fee using a regular expression and add the dollar sign
                        String annualFee = "";
                        Pattern feePattern = Pattern.compile("Annual fee:\\s*\\$?(\\d+)");
                        Matcher feeMatcher = feePattern.matcher(offers);
                        if (feeMatcher.find()) {
                            annualFee = "$" + feeMatcher.group(1); // Add the dollar sign
                        }

                        // Get the interest rate for cash advances
                        String interestRate = "";
                        List<WebElement> paragraphs = card.findElements(By.tagName("p"));
                        for (WebElement paragraph : paragraphs) {
                            if (paragraph.getText().contains("Interest rates:")) {
                                List<WebElement> boldElements = paragraph.findElements(By.tagName("b"));
                                if (boldElements.size() > 1) {
                                    interestRate = boldElements.get(1).getText().trim();
                                }
                                break;
                            }
                        }

                        // Write to CSV including the bank name, category, card type, offers, annual fee, and interest rate
                        csvWriter.append(String.format("Scotiabank,%s,%s,%s,%s\n",
                                escapeCSV(cardType),
                                escapeCSV(annualFee),
                                escapeCSV(interestRate),
                                escapeCSV(offers)
                        ));

                    } catch (NoSuchElementException e) {
                        System.out.println("Error processing card: " + e.getMessage());
                        // Optional: Print the HTML of the card for debugging
                        System.out.println("Card HTML: " + card.getAttribute("outerHTML"));
                    }
                }

                // Check for the "Next" button to navigate to the next page (if applicable)
                try {
                    WebElement nextButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Next")));
                    nextButton.click(); // Click the "Next" button
                } catch (TimeoutException | NoSuchElementException e) {
                    System.out.println("No more pages to navigate.");
                    hasNextPage = false; // Exit the loop if no more pages
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    // Method to handle pop-ups
    private static void handlePopUp(WebDriver driver, WebDriverWait wait) {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alert.accept(); // Accept the pop-up
        } catch (TimeoutException e) {
            // No pop-up appeared
        }
    }

    // Method to clean unwanted characters from the scraped text
    private static String cleanText(String text) {
        // Remove unwanted characters like ®, *, ™, and others
        return text.replaceAll("®*™<>\\[\\]", "").replaceAll("\\s+", " ").trim();
    }

    // Method to escape CSV special characters
    private static String escapeCSV(String value) {
        if (value.contains("\"")) {
            value = value.replace("\"", "\"\"");
        }
        return "\"" + value + "\"";
    }
}
