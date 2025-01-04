package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;  // Add this import
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class CreditCardGUI extends JFrame {

    private JTextField annualFeeField;
    private JTextField interestRateField;
    private JTextField rewardsField;
    private JTable resultTable;
    private JTextArea outputArea;
    private List<CreditCard> cards;
    private VocabularyLoader vocabularyLoader;
    private SearchTracker searchTracker;
    private JComboBox<String> featuresMenu;
    private JButton selectButton;
    private JTabbedPane tabbedPane; // For the tabs
    private JPanel resultTab, featuresTab; // Separate panels for results and additional features

    public CreditCardGUI() {
        setTitle("Credit Card Finder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700); // Increased size for better layout
        setLayout(new BorderLayout(10, 10));

        // Initialize helper classes
        vocabularyLoader = new VocabularyLoader();
        searchTracker = new SearchTracker();

        // Load cards from the CSV file
        String csvFile = "combined_credit_cards.csv";
        cards = loadCards(csvFile);

        if (cards.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data found in the CSV file.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // Populate vocabulary from credit card data
        vocabularyLoader.loadVocabularyFromCreditCards(cards);

        // Create the menu bar with "Additional Features" menu
        createMenuBar();

        // Create the main input panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Search Criteria"));

        inputPanel.add(new JLabel("Maximum Annual Fee:"));
        annualFeeField = new JTextField();
        inputPanel.add(annualFeeField);

        inputPanel.add(new JLabel("Maximum Interest Rate (APR):"));
        interestRateField = new JTextField();
        inputPanel.add(interestRateField);

        inputPanel.add(new JLabel("Rewards Keyword:"));
        rewardsField = new JTextField();
        inputPanel.add(rewardsField);

        JButton recommendButton = new JButton("Recommend Cards");
        recommendButton.setBackground(new Color(0, 153, 76));
        recommendButton.setForeground(Color.WHITE);
        recommendButton.addActionListener(e -> recommendCards());
        inputPanel.add(recommendButton);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetInputs());
        inputPanel.add(resetButton);

        JButton showAllButton = new JButton("Show All Cards");
        showAllButton.addActionListener(e -> displayCards(cards));
        inputPanel.add(showAllButton);

        JButton hideButton = new JButton("Hide Data");
        hideButton.addActionListener(e -> hideData());
        inputPanel.add(hideButton);

        // Create the result table
        resultTable = new JTable(new DefaultTableModel(
                new Object[]{"Card Name", "Bank Name", "Annual Fee", "Interest Rate", "Rewards"}, 0));
        resultTable.setRowHeight(25); // Improved row height
        resultTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        JScrollPane tableScrollPane = new JScrollPane(resultTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Credit Card Results"));

        // Create output area
        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setBorder(BorderFactory.createTitledBorder("Feature Output"));
        outputArea.setBackground(new Color(245, 245, 245));
        JScrollPane outputScrollPane = new JScrollPane(outputArea);

        // Create the JTabbedPane
        tabbedPane = new JTabbedPane();

        // Results Tab
        resultTab = new JPanel(new BorderLayout());
        resultTab.add(inputPanel, BorderLayout.NORTH);
        resultTab.add(tableScrollPane, BorderLayout.CENTER);
        resultTab.add(outputScrollPane, BorderLayout.SOUTH);

        // Additional Features Tab
        featuresTab = new JPanel(new BorderLayout());
        addFeaturesMenu();
        featuresTab.add(outputScrollPane, BorderLayout.CENTER);

        // Add tabs to the tabbed pane
        tabbedPane.addTab("Results", resultTab);
        tabbedPane.addTab("Additional Features", featuresTab);

        // Layout: Add tabbed pane to the center
        add(tabbedPane, BorderLayout.CENTER);

        setLocationRelativeTo(null); // Center window
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu featuresMenu = new JMenu("Additional Features");

        JMenuItem spellCheckMenuItem = new JMenuItem("Spell Checking and Word Completion");
        spellCheckMenuItem.addActionListener(e -> performSpellCheck());
        featuresMenu.add(spellCheckMenuItem);

        JMenuItem frequencyCountMenuItem = new JMenuItem("Frequency Counter");
        frequencyCountMenuItem.addActionListener(e -> displayFrequencyCount());
        featuresMenu.add(frequencyCountMenuItem);

        JMenuItem searchTrackerMenuItem = new JMenuItem("Search Tracker");
        searchTrackerMenuItem.addActionListener(e -> updateSearchTracker());
        featuresMenu.add(searchTrackerMenuItem);

        JMenuItem pageRankMenuItem = new JMenuItem("Page Rank");
        pageRankMenuItem.addActionListener(e -> displayPageRanking());
        featuresMenu.add(pageRankMenuItem);

        // Data Validation Menu Item
        JMenuItem dataValidationMenuItem = new JMenuItem("Data Validation");
        dataValidationMenuItem.addActionListener(e -> performDataValidation());
        featuresMenu.add(dataValidationMenuItem);

        // Regex Pattern Matching Menu Item
        JMenuItem regexPatternMenuItem = new JMenuItem("Regex Pattern Matching");
        regexPatternMenuItem.addActionListener(e -> performRegexPatternMatching());
        featuresMenu.add(regexPatternMenuItem);

        menuBar.add(featuresMenu);
        setJMenuBar(menuBar);
    }

    private void addFeaturesMenu() {
        String[] features = {
                "Spell Checking and Word Completion",
                "Frequency Counter",
                "Search Tracker",
                "Page Rank",
                "Data Validation",
                "Regex Pattern Matching"
        };

        // Create JComboBox for selecting features
        featuresMenu = new JComboBox<>(features);
        featuresMenu.addActionListener(e -> selectButton.setEnabled(true));

        // Create a select button that is initially disabled
        selectButton = new JButton("Select");
        selectButton.setEnabled(false);
        selectButton.addActionListener(e -> handleFeatureSelection());

        JPanel featurePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        featurePanel.setBorder(BorderFactory.createTitledBorder("Additional Features"));
        featurePanel.add(featuresMenu);
        featurePanel.add(selectButton);

        featuresTab.add(featurePanel, BorderLayout.NORTH);
    }

    private void handleFeatureSelection() {
        String selectedFeature = (String) featuresMenu.getSelectedItem();
        switch (selectedFeature) {
            case "Spell Checking and Word Completion":
                performSpellCheck();
                break;
            case "Frequency Counter":
                displayFrequencyCount();
                break;
            case "Search Tracker":
                updateSearchTracker();
                break;
            case "Page Rank":
                displayPageRanking();
                break;
            case "Data Validation":
                performDataValidation();
                break;
            case "Regex Pattern Matching":
                performRegexPatternMatching();
                break;
        }
    }

    // ** New methods to handle Data Validation and Regex Matching **

    private void performDataValidation() {
        List<String[]> validationResults = DataValidation.validateCardData(cards);

        // If no errors, show a message
        if (validationResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All card data is valid!", "Validation Results", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create a new window for displaying the results
        JFrame validationFrame = new JFrame("Data Validation Results");
        validationFrame.setSize(800, 600); // Size of the results window
        validationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close
        validationFrame.setLocationRelativeTo(this); // Center it on the main window

        // Create a JTable to display the results
        String[] columns = {"Bank Name", "Annual Fee", "Valid/Invalid"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        // Loop through the validation results and add them to the table model
        for (String[] result : validationResults) {
            String bankName = result[0];  // Bank name
            String annualFee = result[1]; // Annual fee
            String status = result[2]; // Validation status (Valid/Invalid)

            // Add row to the table
            model.addRow(new Object[]{bankName, annualFee, status});
        }

        // Create the JTable with the data model
        JTable validationTable = new JTable(model);
        validationTable.setRowHeight(30); // Set row height for better readability
        validationTable.getColumnModel().getColumn(0).setPreferredWidth(200); // Set column width
        validationTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        validationTable.getColumnModel().getColumn(2).setPreferredWidth(100);

        // Add color coding to rows based on validation status
        validationTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Check validation status and color code rows
                String status = (String) table.getValueAt(row, 2);
                if (status != null && status.equals("Invalid")) {
                    c.setBackground(Color.RED); // Red for invalid data
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(table.getBackground()); // Default background for valid entries
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        });

        // Add the table to a scroll pane (in case there are too many rows)
        JScrollPane scrollPane = new JScrollPane(validationTable);
        validationFrame.add(scrollPane, BorderLayout.CENTER);

        // Add a button to close the window
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> validationFrame.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        validationFrame.add(buttonPanel, BorderLayout.SOUTH);

        validationFrame.setVisible(true); // Show the validation window
    }

    private void performRegexPatternMatching() {
        RegexPatternMatcher.findPatternInRewards(cards);
    }
    private void performSpellCheck() {
        String input = JOptionPane.showInputDialog(this, "Enter a word to check:");
        if (input != null && !input.trim().isEmpty()) {
            SpellChecker spellChecker = new SpellChecker(vocabularyLoader);
            String suggestions = spellChecker.spellCheckAndSuggest(input);
            outputArea.append("Spell Check Results:\n" + suggestions + "\n\n");
        }
    }

    private void displayFrequencyCount() {
        FrequencyCounter counter = new FrequencyCounter(vocabularyLoader);
        counter.countWordFrequency(); // Implement your frequency counting logic
    }

    private void updateSearchTracker() {
        String query = JOptionPane.showInputDialog(this, "Enter a search query:");
        if (query != null && !query.trim().isEmpty()) {
            searchTracker.updateSearchFrequency(query);
        }

        String searchResults = searchTracker.getTopSearches();
        outputArea.append(searchResults + "\n\n");
    }
    private List<File> getHtmlFilesFromCrawledDataFolder() {
        List<File> htmlFiles = new ArrayList<>();
        File folder = new File("E:\\cc_final\\Assignment1\\src\\main\\resources\\crawled data"); // Specify the path to your crawled data folder

        // Check if the folder exists and is a directory
        if (folder.exists() && folder.isDirectory()) {
            // Recursively find HTML files in the folder and its subdirectories
            findHtmlFiles(folder, htmlFiles);
        } else {
            JOptionPane.showMessageDialog(this, "Crawled data folder not found.", "Error", JOptionPane.ERROR_MESSAGE);
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

    private void displayPageRanking() {
        // Get the list of HTML files from the crawled data folder
        List<File> htmlFiles = getHtmlFilesFromCrawledDataFolder();
        Map<String, Integer> pageWordCounts = new HashMap<>();

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

        // Append to the output area in the GUI
        outputArea.append(rankingOutput.toString() + "\n\n");
    }


    private void recommendCards() {
        try {
            double maxFee = parseDoubleOrDefault(annualFeeField.getText(), -1);
            double maxInterestRate = parseDoubleOrDefault(interestRateField.getText(), -1);
            String rewardsKeyword = rewardsField.getText().toLowerCase().trim();

            List<CreditCard> recommendedCards = cards.stream()
                    .filter(card -> maxFee == -1 || card.getAnnualFee() <= maxFee)
                    .filter(card -> maxInterestRate == -1 || card.getInterestRate() <= maxInterestRate)
                    .filter(card -> rewardsKeyword.isEmpty() || card.getRewards().toLowerCase().contains(rewardsKeyword))
                    .collect(Collectors.toList());

            if (recommendedCards.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No cards matched your criteria.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
            displayCards(recommendedCards);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check your values.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetInputs() {
        annualFeeField.setText("");
        interestRateField.setText("");
        rewardsField.setText("");
    }

    private void displayCards(List<CreditCard> cards) {
        DefaultTableModel tableModel = (DefaultTableModel) resultTable.getModel();
        tableModel.setRowCount(0); // Clear existing rows

        for (CreditCard card : cards) {
            tableModel.addRow(new Object[]{
                    card.getName(),
                    card.getBankName(),
                    card.getAnnualFee(),
                    card.getInterestRate(),
                    card.getRewards()
            });
        }
    }

    private void hideData() {
        outputArea.setText(""); // Clear output area
        DefaultTableModel tableModel = (DefaultTableModel) resultTable.getModel();
        tableModel.setRowCount(0); // Clear the table data
    }


    private List<CreditCard> loadCards(String csvFile) {
        List<CreditCard> cardsList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            reader.readLine(); // Skip header line
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5) {
                    double annualFee = parseDoubleOrDefault(data[2], -1);  // Use the same method to parse
                    double interestRate = parseDoubleOrDefault(data[3], -1);  // Use the same method to parse

                    CreditCard card = new CreditCard(
                            data[0], // Card Name
                            data[1], // Bank Name
                            annualFee,  // Annual Fee
                            interestRate,  // Interest Rate
                            data[4] // Rewards
                    );
                    cardsList.add(card);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cardsList;
    }

    private double parseDoubleOrDefault(String value, double defaultValue) {
        try {
            if (value != null && !value.equals("N/A")) {
                return Double.parseDouble(value);
            }
        } catch (NumberFormatException e) {
            // Log or handle the exception as needed
        }
        return defaultValue;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WelcomePage welcomePage = new WelcomePage();
            welcomePage.setVisible(true);
        });
    }
}

class WelcomePage extends JFrame {
    public WelcomePage() {
        setTitle("Welcome to Credit Card Recommendation System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set the background image
        setContentPane(new JLabel(new ImageIcon("background.jpg")));
        getContentPane().setLayout(new BorderLayout());

        // Create the welcome text
        JLabel welcomeLabel = new JLabel("Hey! Welcome to the Credit Card Recommendation System");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setHorizontalAlignment(SwingConstants.LEFT); // Align text to the left
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Create the Continue button
        JButton continueButton = new JButton("Continue");
        continueButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        continueButton.setBackground(new Color(0, 153, 76));
        continueButton.setForeground(Color.WHITE);
        continueButton.setFocusPainted(false);
        continueButton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        continueButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        continueButton.addActionListener(e -> {
            setVisible(false);
            CreditCardGUI app = new CreditCardGUI();
            app.setVisible(true);
        });

        // Panel for the welcome text and button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(welcomeLabel, BorderLayout.WEST); // Add text to the bottom-left
        bottomPanel.add(continueButton, BorderLayout.EAST); // Add button to the bottom-right

        // Add the bottom panel to the frame
        add(bottomPanel, BorderLayout.SOUTH);
    }
}