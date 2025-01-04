package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SpellChecker {

    private final VocabularyLoader vocabularyLoader;

    // Constructor that accepts a VocabularyLoader instance
    public SpellChecker(VocabularyLoader loader) {
        this.vocabularyLoader = loader;
    }

    // Method to spell-check and suggest corrections for an input word
    public String spellCheckAndSuggest(String input) {
        Set<String> vocabulary = vocabularyLoader.getVocabulary();
        List<String> suggestions = new ArrayList<>();
        String closestMatch = null;
        int minDistance = Integer.MAX_VALUE;

        // Check if the word exists in the vocabulary
        boolean isCorrect = vocabulary.contains(input.toLowerCase());

        if (!isCorrect) {
            // If the word is incorrect, provide suggestions and closest match
            for (String word : vocabulary) {
                if (word.startsWith(input.toLowerCase())) {
                    suggestions.add(word);
                }
                int distance = levenshteinDistance(input.toLowerCase(), word);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestMatch = word;
                }
            }

            // Build the result as a string
            StringBuilder result = new StringBuilder();
            result.append("The word \"" + input + "\" is incorrect.\n");

            if (!suggestions.isEmpty()) {
                result.append("Suggestions:\n");
                for (String suggestion : suggestions) {
                    result.append(suggestion).append("\n");
                }
            } else {
                result.append("No exact matches found.\n");
                if (closestMatch != null) {
                    result.append("Did you mean: ").append(closestMatch).append("?\n");
                }
            }

            return result.toString(); // Return the result as a String
        } else {
            return "The word \"" + input + "\" is correct!";
        }
    }

    // Calculate the Levenshtein distance between two words
    private int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1)
                    );
                }
            }
        }
        return dp[a.length()][b.length()];
    }
}
