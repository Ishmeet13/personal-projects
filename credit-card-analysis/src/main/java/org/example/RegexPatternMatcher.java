package org.example;

import javax.swing.*;
import java.util.List;
import java.util.regex.*;
import java.util.stream.Collectors;

public class RegexPatternMatcher {

    public static void findPatternInRewards(List<CreditCard> cards) {
        String patternInput = JOptionPane.showInputDialog("Enter a regex pattern:");
        if (patternInput == null || patternInput.trim().isEmpty()) return;

        Pattern pattern;
        try {
            pattern = Pattern.compile(patternInput);
        } catch (PatternSyntaxException e) {
            JOptionPane.showMessageDialog(null, "Invalid regex pattern! Please check the syntax.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String[]> matches = cards.stream()
                .filter(card -> pattern.matcher(card.getRewards()).find())  // Match only in rewards
                .map(card -> new String[]{card.getName(), "Match Found"})
                .collect(Collectors.toList());

        if (matches.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No matches found for the given pattern.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JTable table = new JTable(matches.toArray(new String[0][0]), new String[]{"Card Name", "Match"});
            JOptionPane.showMessageDialog(null, new JScrollPane(table), "Pattern Match Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
