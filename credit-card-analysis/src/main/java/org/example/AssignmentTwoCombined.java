import java.io.*;
import java.util.*;

public class AssignmentTwoCombined {
    private static final Map<String, Integer> frequencyMap = new HashMap<>();
    private static final Set<String> vocabulary = new HashSet<>(); // Use Set to prevent duplicates
    private static final Map<String, Integer> searchFrequency = new HashMap<>();
    private static final Map<String, Integer> pageRanks = new HashMap<>(); // For page ranks

    public static void main(String[] args) {
        String csvFilePath ="combined_credit_cards.csv";
        loadVocabulary(csvFilePath);
        System.out.println("Vocabulary created with " + vocabulary.size() + " unique words.");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Choose a task:");
            System.out.println("1. Spell Checking and Word Completion");
            System.out.println("2. Frequency Count");
            System.out.println("3. Search Frequency");
            System.out.println("4. Page Ranking");
            System.out.println("5. Exit");

            String input = scanner.nextLine(); // Read input as a string
            int choice;
            try {
                choice = Integer.parseInt(input); // Parse the string to an integer
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
                continue; // Go back to the menu
            }

            switch (choice) {
                case 1 -> {
                    System.out.println("Enter a word or prefix:");
                    String word = scanner.nextLine();
                    spellCheckAndSuggest(word);
                }
                case 2 -> displayFrequencyCount();
                case 3 -> {
                    System.out.println("Enter a search query:");
                    String query = scanner.nextLine();
                    updateSearchFrequency(query);
                }
                case 4 -> pageRankingCalculation();

                case 5 -> {
                    System.out.println("Exiting the program.");
                    return;
                }
                default -> System.out.println("Invalid choice. Please enter a number between 1 and 5.");
            }
        }
    }

    // Load vocabulary and frequency map from CSV
    private static void loadVocabulary(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                for (String field : fields) {
                    String[] words = field.split("\\s+");
                    for (String word : words) {
                        String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                        if (!cleanWord.isEmpty()) {
                            vocabulary.add(cleanWord); // Add to Set
                            frequencyMap.put(cleanWord, frequencyMap.getOrDefault(cleanWord, 0) + 1);
                            pageRanks.put(cleanWord, 0); // Initialize rank to 0
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }

    // Combined spell-checking and word completion
    private static void spellCheckAndSuggest(String input) {
        List<String> suggestions = new ArrayList<>();
        String closestMatch = null;
        int minDistance = Integer.MAX_VALUE;

        for (String word : vocabulary) { // Iterate over unique words
            // Check if the word starts with the input prefix
            if (word.startsWith(input.toLowerCase())) {
                suggestions.add(word);
            }

            // Calculate Levenshtein distance for spell-checking
            int distance = levenshteinDistance(input.toLowerCase(), word);
            if (distance < minDistance) {
                minDistance = distance;
                closestMatch = word;
            }
        }

        if (!suggestions.isEmpty()) {
            System.out.println("Suggestions:");
            for (String suggestion : suggestions) {
                System.out.println(suggestion);
            }
        } else {
            System.out.println("No exact matches found.");
            if (closestMatch != null) {
                System.out.println("Did you mean: " + closestMatch + "?");
            }
        }
    }

    // Display word frequency count
    private static void displayFrequencyCount() {
        frequencyMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(10)
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }

    // Update and display search frequency
    private static void updateSearchFrequency(String query) {
        searchFrequency.put(query, searchFrequency.getOrDefault(query, 0) + 1);

        // Update page rank for the query
        if (pageRanks.containsKey(query)) {
            pageRanks.put(query, pageRanks.get(query) + 1);
        }

        System.out.println("Top searches:");
        searchFrequency.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }

    // Display page ranking using a max-heap
    private static void displayPageRanking() {
        PriorityQueue<Map.Entry<String, Integer>> maxHeap = new PriorityQueue<>(
                (e1, e2) -> e2.getValue().compareTo(e1.getValue())
        );
        maxHeap.addAll(pageRanks.entrySet());

        System.out.println("Top Ranked Pages:");
        int rank = 1;
        while (!maxHeap.isEmpty() && rank <= 10) { // Display top 10 pages
            Map.Entry<String, Integer> entry = maxHeap.poll();
            System.out.println(rank + ". " + entry.getKey() + " - Rank: " + entry.getValue());
            rank++;
        }
    }

    // Calculate Levenshtein distance
    private static int levenshteinDistance(String a, String b) {
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

    // Page Ranking Calculation
    private static void pageRankingCalculation() {
        PriorityQueue<Map.Entry<String, Integer>> maxHeap =
                new PriorityQueue<>((a, b) -> b.getValue().compareTo(a.getValue()));

        // Calculate ranks based on search frequency
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            String word = entry.getKey();
            int rank = searchFrequency.getOrDefault(word, 0); // Rank based on search frequency
            maxHeap.offer(Map.entry(word, rank));
        }

        System.out.println("Top Ranked Pages:");
        int count = 1;

        while (!maxHeap.isEmpty() && count <= 10) { // Display top 10 ranked pages
            Map.Entry<String, Integer> entry = maxHeap.poll();
            System.out.println(count + ". " + entry.getKey() + " - Rank: " + entry.getValue());
            count++;
        }
    }
}
