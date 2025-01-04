package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;

public class Main {

    public static void main(String[] args) {
        // Set up the WebDriver for the browser
        System.setProperty("webdriver.chrome.driver", "C:\\\\Users\\\\isaro\\\\Downloads\\\\chromedriver-win64 (3)\\\\chromedriver-win64\\\\chromedriver.exe");

        // Initialize WebDriver and WebDriverWait
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Create a shared CSV file writer
        try (FileWriter csvWriter = new FileWriter("combined_credit_cards.csv")) {
            // Write the header for the CSV file
            csvWriter.append("Bank Name,Card Name,Annual Fee,Purchase Interest Rate,Rewards\n");

            // Call each bank's scraper methods
            System.out.println("Starting National Bank scraper...");
            ScrapeNationalBank.scrapeNationalBankCards(driver, wait, csvWriter);

            // If there are other scrapers, invoke them similarly
            System.out.println("Starting RBC scraper...");
            RBCCardScraper.scrapeRBCCards(driver, wait, csvWriter);

            System.out.println("Starting CIBC scraper...");
            CIBCCardScraper.scrapeCIBCCards(driver, wait, csvWriter);

            System.out.println("Starting Scotiabank scraper...");
            ScrapesScotiaBank.scrapeScotiaBankCards(driver, wait, csvWriter);

            System.out.println("All scrapers completed. CSV file generated.");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the browser
            driver.quit();
        }
    }
}
