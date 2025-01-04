package org.example;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class PageRanker {

    private String crawledDataFolderPath;

    public PageRanker(String crawledDataFolderPath) {
        this.crawledDataFolderPath = crawledDataFolderPath;
    }

    public void displayPageRanking() {
        // Get the list of HTML files from the crawled data folder
        List<File> htmlFiles = getHtmlFilesFromCrawledDataFolder();

        if (htmlFiles.isEmpty()) {
            System.out.println("No HTML files found in the crawled data folder.");
            return;
        }

        Map<String, Integer> pageWordCounts = new HashMap<>();

        // Count words in each HTML file
        for (File file : htmlFiles) {
            int wordCount = countWordsInFile(file);
            pageWordCounts.put(file.getPath(), wordCount); // Store the file path and its word count
        }

        // Sort the pages by word count and get the top 10
        List<Map.Entry<String, Integer>> sortedPages = pageWordCounts.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toList());

        // Prepare the output
        StringBuilder rankingOutput = new StringBuilder("Top 10 HTML Pages by Word Count:\n");
        for (Map.Entry<String, Integer> entry : sortedPages) {
            rankingOutput.append(entry.getKey()).append(": ").append(entry.getValue()).append(" words\n");
        }

        // Print out the ranking
        System.out.println(rankingOutput.toString());
    }

    private List<File> getHtmlFilesFromCrawledDataFolder() {
        List<File> htmlFiles = new ArrayList<>();
        File folder = new File(crawledDataFolderPath); // Use the path passed to the constructor

        // Check if the folder exists and is a directory
        if (folder.exists() && folder.isDirectory()) {
            // Recursively find HTML files in the folder and its subdirectories
            findHtmlFiles(folder, htmlFiles);
        } else {
            System.out.println("Crawled data folder not found.");
        }
        return htmlFiles;
    }

    private void findHtmlFiles(File folder, List<File> htmlFiles) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursively search in subdirectories
                    findHtmlFiles(file, htmlFiles);
                } else if (file.getName().toLowerCase().endsWith(".html")) {
                    // Add HTML files to the list
                    htmlFiles.add(file);
                }
            }
        }
    }

    private int countWordsInFile(File file) {
        int wordCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into words and count them
                String[] words = line.split("\\W+"); // Split by non-word characters
                wordCount += words.length;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordCount;
    }
}
