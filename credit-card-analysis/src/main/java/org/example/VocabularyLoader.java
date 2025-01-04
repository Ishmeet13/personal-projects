package org.example;

import java.util.*;

public class VocabularyLoader {

    private final Map<String, Integer> frequencyMap = new HashMap<>();
    private final Set<String> vocabulary = new HashSet<>();

    // Method to load vocabulary and frequency from a list of credit cards
    public void loadVocabularyFromCreditCards(List<CreditCard> cards) {
        for (CreditCard card : cards) {
            String[] words = (card.getName() + " " + card.getBankName() + " " + card.getRewards()).split("\\s+");
            for (String word : words) {
                String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                if (!cleanWord.isEmpty()) {
                    vocabulary.add(cleanWord);
                    frequencyMap.put(cleanWord, frequencyMap.getOrDefault(cleanWord, 0) + 1);
                }
            }
        }
    }

    // Getter for vocabulary set
    public Set<String> getVocabulary() {
        return vocabulary;
    }

    // Getter for frequency map
    public Map<String, Integer> getFrequencyMap() {
        return frequencyMap;
    }

    // Get the size of the vocabulary
    public int getVocabularySize() {
        return vocabulary.size();
    }
}
