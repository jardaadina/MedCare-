package com.medcare.ui;
import com.medcare.model.Receptionist;
import javax.swing.*;
import java.awt.*;

//adaugarea si editarea receptionistilor
public class ReceptionistDialog extends JDialog
{
    private final JTextField nameField;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private boolean confirmed = false;
    private Receptionist receptionist;
    private boolean isEditing;

    public ReceptionistDialog(JFrame parent) {
        this(parent, null);
    }

    public ReceptionistDialog(JFrame parent, Receptionist receptionist)
    {
        super(parent, receptionist == null ? "Add Receptionist" : "Edit Receptionist", true);
        setSize(400, 200);
        setLocationRelativeTo(parent);

        this.receptionist = receptionist;
        this.isEditing = receptionist != null;

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
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

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

        if (isEditing)
        {
            nameField.setText(receptionist.getName());
            usernameField.setText(receptionist.getUsername());
            passwordField.setText("");
        }

        okButton.addActionListener(e -> {
            if (validateInputs())
            {
                saveReceptionist();
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
        if (usernameField.getText().trim().isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Please enter a username", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!isEditing && passwordField.getPassword().length == 0)
        {
            JOptionPane.showMessageDialog(this, "Please enter a password", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void saveReceptionist()
    {
        if (receptionist == null)
        {
            receptionist = new Receptionist();
        }
        receptionist.setName(nameField.getText().trim());
        receptionist.setUsername(usernameField.getText().trim());
        if (passwordField.getPassword().length > 0)
        {
            String password = new String(passwordField.getPassword());
            receptionist.setPassword(password);
        }
    }
    public boolean isConfirmed() {
        return confirmed;
    }
    public Receptionist getReceptionist() {
        return receptionist;
    }
}