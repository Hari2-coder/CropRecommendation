import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class CropRecommendationApp extends JFrame {

    private JSlider phSpinner;
    private JSpinner tempSpinner;
    private JSpinner rainSpinner;
    private JComboBox<String> seasonCombo;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel status;

    public static void main(String[] args) {
        // Native OS look & feel
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        // Load data
        CropRecommender.loadCropsFromCSV("crops.csv");

        // Start UI
        SwingUtilities.invokeLater(() -> new CropRecommendationApp().setVisible(true));
    }

    public CropRecommendationApp() {
        super("🌱 Crop Recommendation System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 600);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(12, 12));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(12, 12, 12, 12));

        // Header
        JLabel title = new JLabel("Welcome! Enter details to get crop recommendations.");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        // LEFT: Form (fixed width)
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new TitledBorder("Input Factors"));
        form.setPreferredSize(new Dimension(360, 0)); // keeps alignment neat
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;

    
        // Soil pH
        addLabel(form, gc, 0, "Soil pH:");
        phSpinner = new JSlider( 0, 8, 7); 
        phSpinner.setMajorTickSpacing(2);
        phSpinner.setMinorTickSpacing(2);
        phSpinner.setPaintTicks(true);
        phSpinner.setPaintLabels(true);
        phSpinner.setToolTipText("Soil pH level");
        addField(form, gc, 0, phSpinner);






        // Temperature
        addLabel(form, gc, 1, "Temperature (°C):");
        tempSpinner = new JSpinner(new SpinnerNumberModel(25, -10, 60, 1));
        tempSpinner.setToolTipText("Average ambient temperature");
        addField(form, gc, 1, tempSpinner);

        // Rainfall
        addLabel(form, gc, 2, "Rainfall (mm):");
        rainSpinner = new JSpinner(new SpinnerNumberModel(100, 0, 500, 5));
        rainSpinner.setToolTipText("Approx. seasonal/annual rainfall");
        addField(form, gc, 2, rainSpinner);

        // Season
        addLabel(form, gc, 3, "Season:");
        seasonCombo = new JComboBox<>(new String[]{"Any", "Winter", "Summer", "Kharif", "Rabi"});
        addField(form, gc, 3, seasonCombo);

        // Button
        JButton recommend = new JButton("Recommend Crops");
        recommend.setFocusable(false);
        recommend.addActionListener(e -> onRecommend());
        gc.gridx = 0; gc.gridy = 4; gc.gridwidth = 2;
        form.add(recommend, gc);

        add(form, BorderLayout.WEST);

        // RIGHT: Results table
        tableModel = new DefaultTableModel(
                new String[]{"Crop", "Season", "pH Range", "Temp (°C)", "Rain (mm)", "Details"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(24);
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(380);

        JScrollPane tableScroll = new JScrollPane(table);
        JPanel results = new JPanel(new BorderLayout());
        results.setBorder(new TitledBorder("Recommendations"));
        results.add(tableScroll, BorderLayout.CENTER);
        add(results, BorderLayout.CENTER);
        

        // Status bar
        status = new JLabel("Ready.");
        status.setBorder(new EmptyBorder(6, 2, 0, 2));
        add(status, BorderLayout.SOUTH);
    }

    private void addLabel(JPanel panel, GridBagConstraints gc, int row, String text) {
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1;
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setPreferredSize(new Dimension(150, 24));
        panel.add(lbl, gc);
    }

    private void addField(JPanel panel, GridBagConstraints gc, int row, JComponent field) {
        gc.gridx = 1; gc.gridy = row; gc.gridwidth = 1;
        panel.add(field, gc);
    }

    private void onRecommend() {
        double ph   = ((Number) phSpinner.getValue()).doubleValue();
        double temp = ((Number) tempSpinner.getValue()).doubleValue();
        int rain    = ((Number) rainSpinner.getValue()).intValue();
        String season = (String) seasonCombo.getSelectedItem();

        List<CropRecommender.Crop> recs = CropRecommender.recommend(ph, temp, rain, season);

        tableModel.setRowCount(0);
        if (recs.isEmpty()) {
            status.setText("No suitable crops found. Try adjusting inputs.");
            JOptionPane.showMessageDialog(this,
                    "No suitable crops found for the given conditions.\nTry widening ranges.",
                    "No Match", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (CropRecommender.Crop c : recs) {
            tableModel.addRow(new Object[]{
                    c.name, c.season,
                    String.format("%.1f–%.1f", c.minPH, c.maxPH),
                    String.format("%.0f–%.0f", c.minTemp, c.maxTemp),
                    c.minRain + "–" + c.maxRain,
                    c.details
            });
        }
        status.setText("Found " + recs.size() + " crop(s). You can sort by any column");
        
    } }  