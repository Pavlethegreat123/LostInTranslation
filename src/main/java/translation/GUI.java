package translation;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.*;

public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // set premade classes
            Translator translator = new JSONTranslator();
            CountryCodeConverter countryConv = new CountryCodeConverter();
            LanguageCodeConverter langConv   = new LanguageCodeConverter();

            // language panel
            JPanel languagePanel = new JPanel();
            languagePanel.add(new JLabel("Language:"), 0);
            languagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            // builda language names and a map back to codes
            java.util.List<String> languageNames = new ArrayList<>();
            Map<String,String> languageNameToCode = new HashMap<>();
            for (String code : translator.getLanguageCodes()) {
                String name = langConv.fromLanguageCode(code.toLowerCase());
                if (name != null) {
                    languageNames.add(name);
                    languageNameToCode.put(name, code);
                }
            }
            Collections.sort(languageNames);

            JComboBox<String> languageCombo = new JComboBox<>(languageNames.toArray(new String[0]));
            languageCombo.setMaximumRowCount(12);
            languagePanel.add(languageCombo, 1);

            // translation panel
            JPanel translationPanel = new JPanel();
            translationPanel.add(new JLabel("Translation:"), 0);
            JLabel resultLabel = new JLabel();
            translationPanel.add(resultLabel, 1);
            translationPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            // country panel
            JPanel countryPanel = new JPanel();
            countryPanel.setLayout(new BorderLayout(0, 0));
            countryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            // builds country names and a map back to codes
            java.util.List<String> countryNames = new ArrayList<>();
            Map<String,String> countryNameToCode = new HashMap<>();
            for (String code : translator.getCountryCodes()) {
                String name = countryConv.fromCountryCode(code.toUpperCase());
                if (name != null) {
                    countryNames.add(name);
                    countryNameToCode.put(name, code);
                }
            }
            Collections.sort(countryNames);

            JList<String> countryList = new JList<>(countryNames.toArray(new String[0]));
            countryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            countryList.setBorder(BorderFactory.createEmptyBorder());

            JScrollPane scrollPane = new JScrollPane(countryList);
            countryPanel.add(scrollPane, BorderLayout.CENTER);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBorder(null);

            // updates translation when either control changes
            Runnable refresh = () -> {
                String countryName  = countryList.getSelectedValue();
                String languageName = (String) languageCombo.getSelectedItem();
                if (countryName == null || languageName == null) {
                    resultLabel.setText("");
                    return;
                }
                String countryCode  = countryNameToCode.get(countryName);
                String languageCode = languageNameToCode.get(languageName);
                String t = translator.translate(countryCode, languageCode);
                resultLabel.setText(t != null ? t : "—");
            };

            languageCombo.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) refresh.run();
            });

            countryList.addListSelectionListener(new ListSelectionListener() {
                @Override public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) refresh.run();
                }
            });

            // assembles all panels
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(languagePanel);
            mainPanel.add(translationPanel);
            mainPanel.add(countryPanel);
            Dimension langPref = languagePanel.getPreferredSize();
            languagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, langPref.height));
            Dimension transPref = translationPanel.getPreferredSize();
            translationPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, transPref.height));
            countryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

            // default frame settings
            JFrame frame = new JFrame("Country Name Translator");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
