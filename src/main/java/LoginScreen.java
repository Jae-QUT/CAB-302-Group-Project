import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginScreen extends JFrame {

    // --- logger ---
    private static final Logger LOGGER = Logger.getLogger(LoginScreen.class.getName());

    // --- essential UI fields you actually need as instance state ---
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JCheckBox rememberMeCheckBox = new JCheckBox("Remember me");
    private final JCheckBox showPasswordCheckBox = new JCheckBox("Show");

    // Buttons can stay as fields so you can add listeners from outside
    private final JButton signInButton = new JButton("Sign in");
    private final JButton createAccountButton = new JButton("Create account");
    private final JButton forgotPasswordButton = new JButton("Forgot password?");

    // store once; fixes the old UIManager.getDefaults().getChar(...) issue
    private final char defaultEchoChar;

    public LoginScreen() {
        setTitle("PokéMath — Sign in");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 340);
        setLocationRelativeTo(null);

        // ---- layout ----
        JPanel root = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 12, 8, 12);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel heading = new JLabel("Welcome to PokéMath");
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 26f));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 3; g.anchor = GridBagConstraints.WEST;
        root.add(heading, g);

        g.gridwidth = 1; g.anchor = GridBagConstraints.EAST;
        g.gridx = 0; g.gridy = 1; root.add(new JLabel("Email / Username"), g);
        g.gridx = 1; g.gridy = 1; g.weightx = 1; g.anchor = GridBagConstraints.CENTER;
        root.add(usernameField, g);

        g.weightx = 0; g.gridx = 0; g.gridy = 2; g.anchor = GridBagConstraints.EAST;
        root.add(new JLabel("Password"), g);
        g.gridx = 1; g.gridy = 2; g.anchor = GridBagConstraints.CENTER;
        root.add(passwordField, g);

        g.gridx = 1; g.gridy = 3; g.anchor = GridBagConstraints.WEST;
        root.add(rememberMeCheckBox, g);
        g.gridx = 2; g.gridy = 3; g.anchor = GridBagConstraints.WEST;
        root.add(showPasswordCheckBox, g);

        g.gridx = 0; g.gridy = 4; g.anchor = GridBagConstraints.EAST;
        root.add(createAccountButton, g);
        g.gridx = 1; g.gridy = 4; g.anchor = GridBagConstraints.WEST;
        root.add(signInButton, g);

        styleLinkButton(forgotPasswordButton);
        g.gridx = 1; g.gridy = 5; g.anchor = GridBagConstraints.CENTER;
        root.add(forgotPasswordButton, g);

        add(root);

        // save default echo char once (important for toggling)
        defaultEchoChar = passwordField.getEchoChar();

        // show/hide behavior
        showPasswordCheckBox.addActionListener(e -> {
            try {
                if (showPasswordCheckBox.isSelected()) {
                    passwordField.setEchoChar((char) 0);    // show characters
                } else {
                    passwordField.setEchoChar(defaultEchoChar); // mask again
                }
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Failed to toggle password visibility", ex);
            }
        });
    }

    private static void styleLinkButton(JButton b) {
        b.setForeground(new Color(0, 102, 204));
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
    }

    // ===== Getters / Setters (suppressed 'unused' to keep Problems panel clean) =====
    @SuppressWarnings("unused")
    public String getUsername() { return usernameField.getText().trim(); }

    @SuppressWarnings("unused")
    public String getPassword() { return new String(passwordField.getPassword()); }

    @SuppressWarnings("unused")
    public boolean isRememberMeChecked() { return rememberMeCheckBox.isSelected(); }

    @SuppressWarnings("unused")
    public boolean isShowPasswordChecked() { return showPasswordCheckBox.isSelected(); }

    @SuppressWarnings("unused")
    public void setUsername(String username) { usernameField.setText(username); }

    @SuppressWarnings("unused")
    public void setPassword(String password) { passwordField.setText(password); }

    @SuppressWarnings("unused")
    public void setRememberMeChecked(boolean b) { rememberMeCheckBox.setSelected(b); }

    @SuppressWarnings("unused")
    public void setShowPasswordChecked(boolean b) {
        showPasswordCheckBox.setSelected(b);
        passwordField.setEchoChar(b ? (char) 0 : defaultEchoChar);
    }

    // ===== Safe hooks to wire DB/service logic from your app code =====
    @SuppressWarnings("unused")
    public void addSignInAction(ActionListener l) { signInButton.addActionListener(l); }

    @SuppressWarnings("unused")
    public void addCreateAccountAction(ActionListener l) { createAccountButton.addActionListener(l); }

    @SuppressWarnings("unused")
    public void addForgotPasswordAction(ActionListener l) { forgotPasswordButton.addActionListener(l); }

    // Demo main (you can remove it in your app)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
