import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.regex.*;
import java.awt.event.*;
import javax.swing.text.*;
import javax.swing.text.Highlighter.HighlightPainter;
    

public class TextEditor {
    private JFrame frame;
    private JTextArea textArea;
    private boolean isDarkMode = false;
    private JButton highlightButton;
    private JScrollPane scrollPane;
    private static final Color BACKGROUND_LIGHT = Color.WHITE;
    private static final Color TEXT_LIGHT = Color.BLACK;
    private static final Color BACKGROUND_DARK = new Color(30, 30, 30); // Dark gray background
    private static final Color TEXT_DARK = Color.WHITE;
    private JTextPane textPane;


    public static void main(String[] args) {
        TextEditor textEditor = new TextEditor();
        SwingUtilities.invokeLater(() -> new TextEditor());
        textEditor.frame.setVisible(true);
    }

    public TextEditor() {
        frame = new JFrame("Text Editor");
        frame.setSize(720, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setRows(50);
        textArea.setColumns(110);

        textPane = new JTextPane();
        textPane.setPreferredSize(new Dimension(700, 700));

        scrollPane = new JScrollPane(textArea);

        addSyntaxHighlighting();

        StyleContext styleContext = new StyleContext();
        DefaultStyledDocument document = new DefaultStyledDocument(styleContext);

        JMenuBar menuBar = new JMenuBar();
        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem darkModeMenuItem = new JCheckBoxMenuItem("Dark Mode");
        darkModeMenuItem.addActionListener(e -> toggleDarkMode());
        viewMenu.add(darkModeMenuItem);
        menuBar.add(viewMenu);
        frame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
            }
        });
        fileMenu.add(newMenuItem);
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(frame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    String text = null;
                    try {
                        text = readFile(file);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    textArea.setText(text);
                }
            }
        });
        fileMenu.add(openMenuItem);
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showSaveDialog(frame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        writeFile(file, textArea.getText());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        //button function of highlight
        highlightButton = new JButton("Highlight");
        highlightButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                highlightSelectedText();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(highlightButton);
        frame.add(buttonPanel, BorderLayout.NORTH);
        
        fileMenu.add(saveMenuItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        frame.add(scrollPane);
        //frame.add(textArea);

        addSyntaxHighlighting();
    }

    private String readFile(File file) throws IOException {
        String text = "";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            text += line + "\n";
        }
        reader.close();
        return text;
    }

    private void writeFile(File file, String text) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(text);
        writer.close();
    }

    private void addSyntaxHighlighting() {
        //JTextPane textPane = null;
        StyledDocument doc = textPane.getStyledDocument();

        Style keywordStyle = doc.addStyle("Keyword", null);
        StyleConstants.setForeground(keywordStyle, Color.BLUE);

        Style stringStyle = doc.addStyle("String", null);
        StyleConstants.setForeground(stringStyle, Color.GREEN);

        Style commentStyle = doc.addStyle("Comment", null);
        StyleConstants.setForeground(commentStyle, Color.GRAY);

        Pattern[] patterns = {
                Pattern.compile("\\b(if|else|while|for|class|public|private)\\b"),
                Pattern.compile("\".*?\""),
                Pattern.compile("//.*")
        };

        try {
            String content = textPane.getText();
            doc.remove(0, doc.getLength());
            doc.insertString(0, content, null);

            for (Pattern pattern : patterns) {
                Matcher matcher = pattern.matcher(content);
                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();
                    Style style;

                    if (pattern.pattern().equals("\\b(if|else|while|for|class|public|private)\\b")) {
                        style = keywordStyle;
                    } else if (pattern.pattern().equals("\".*?\"")) {
                        style = stringStyle;
                    } else {
                        style = commentStyle;
                    }

                    doc.setCharacterAttributes(start, end - start, style, false);
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

    }
    

    //method to make td go dark mode
    private void toggleDarkMode() {
        Color newBackground = isDarkMode ? BACKGROUND_LIGHT : BACKGROUND_DARK;
        Color newText = isDarkMode ? TEXT_LIGHT : TEXT_DARK;

        textArea.setBackground(newBackground);
        textArea.setForeground(newText);
        //frame.getJMenuBar().setBackground(newBackground); // Update menu bar background

        isDarkMode = !isDarkMode;
    }

    //method for highlighting text
    private void highlightSelectedText() {
        Highlighter highlighter = textArea.getHighlighter();
        HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);

        int selectionStart = textArea.getSelectionStart();
        int selectionEnd = textArea.getSelectionEnd();

        try {
            highlighter.addHighlight(selectionStart, selectionEnd, painter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
