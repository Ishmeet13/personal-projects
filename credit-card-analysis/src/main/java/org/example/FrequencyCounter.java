package org.example;

import java.util.*;
import javax.swing.*;

public class FrequencyCounter {
    private final VocabularyLoader vocabularyLoader;

    // Constructor accepts a VocabularyLoader instance
    public FrequencyCounter(VocabularyLoader loader) {
        this.vocabularyLoader = loader;
    }

    // Method to count the frequency of a user-input word
    public void countWordFrequency() {
        // Ask the user for a word to count
        String inputWord = JOptionPane.showInputDialog("Enter a word to count its frequency:");

        if (inputWord == null || inputWord.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "You must enter a word!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        inputWord = inputWord.trim().toLowerCase(); // Normalize the word (to lowercase)

        // Get the frequency map from the vocabulary loader
        Map<String, Integer> frequencyMap = vocabularyLoader.getFrequencyMap();

        // Check if the word exists in the frequency map
        int count = frequencyMap.getOrDefault(inputWord, 0);

        // Display the frequency count
        JOptionPane.showMessageDialog(null, "The word '" + inputWord + "' appears " + count + " times.", "Word Frequency", JOptionPane.INFORMATION_MESSAGE);
    }
}
