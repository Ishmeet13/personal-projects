package org.example;

import java.util.*;

public class SearchTracker {

    private final Map<String, Integer> searchFrequency;

    public SearchTracker() {
        this.searchFrequency = new HashMap<>();
    }

    // Update the search frequency for a given query
    public void updateSearchFrequency(String query) {
        searchFrequency.put(query, searchFrequency.getOrDefault(query, 0) + 1);
    }

    // Method to return top search results as a formatted string
    public String getTopSearches() {
        StringBuilder result = new StringBuilder("Top Searches:\n");
        searchFrequency.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // Sort in descending order
                .limit(10) // Limit to top 10
                .forEach(entry -> result.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n"));
        return result.toString();
    }

    // Get the search frequency map
    public Map<String, Integer> getSearchFrequency() {
        return searchFrequency;
    }
}
