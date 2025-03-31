package com.medcare.ui;
import com.medcare.model.MedicalService;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.Duration;

//adaugarea si editarea serviciilor medicale
public class MedicalServiceDialog extends JDialog
{
    private final JTextField nameField;
    private final JTextField priceField;
    private final JTextField durationField;

    private boolean confirmed = false;
    private MedicalService medicalService;

    public MedicalServiceDialog(JFrame parent) {
        this(parent, null);
    }

    public MedicalServiceDialog(JFrame parent, MedicalService medicalService)
    {
        super(parent, medicalService == null ? "Add Medical Service" : "Edit Medical Service", true);
        setSize(400, 200);
        setLocationRelativeTo(parent);

        this.medicalService = medicalService;

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Price:"), gbc);

        gbc.gridx = 1;
        priceField = new JTextField(20);
        panel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Duration (minutes):"), gbc);

        gbc.gridx = 1;
        durationField = new JTextField(20);
        panel.add(durationField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);

        if (medicalService != null)
        {
            nameField.setText(medicalService.getName());
            priceField.setText(medicalService.getPrice().toString());
            durationField.setText(String.valueOf(medicalService.getDuration().toMinutes()));
        }

        okButton.addActionListener(e -> {
            if (validateInputs())
            {
                saveMedicalService();
                confirmed = true;
                dispose();
            }
        });
        cancelButton.addActionListener(e -> dispose());
        add(panel);
    }

    private boolean validateInputs()
    {
        if (nameField.getText().trim().isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Please enter a name", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try
        {
            new BigDecimal(priceField.getText());
        }
        catch (NumberFormatException e)
        {
            JOptionPane.showMessageDialog(this, "Please enter a valid price", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try
        {
            int duration = Integer.parseInt(durationField.getText());
            if (duration <= 0)
            {
                JOptionPane.showMessageDialog(this, "Duration must be positive", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        catch (NumberFormatException e)
        {
            JOptionPane.showMessageDialog(this, "Please enter a valid duration in minutes", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    private void saveMedicalService()
    {
        if (medicalService == null)
        {
            medicalService = new MedicalService();
        }

        medicalService.setName(nameField.getText().trim());
        medicalService.setPrice(new BigDecimal(priceField.getText()));
        medicalService.setDuration(Duration.ofMinutes(Integer.parseInt(durationField.getText())));
    }
    public boolean isConfirmed() {
        return confirmed;
    }
    public MedicalService getMedicalService() {
        return medicalService;
    }
}