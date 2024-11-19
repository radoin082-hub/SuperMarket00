import jade.core.*;
import jade.wrapper.*;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class SupermarketSimulation {
    private JFrame frame;
    private JComboBox<Product> productDropdown;
    private JTextField quantityField;
    private JTextArea cartArea;
    private JButton addToCartButton, removeItemButton, checkoutButton;
    private JLabel totalLabel, balanceLabel;
    private JTextField amountPaidField;
    private HashMap<String, Product> inventory;
    private HashMap<String, Integer> cart;
    private double totalPrice = 0.0;

    private jade.core.Runtime runtime;
    private AgentContainer container;
    private JTextArea buyerArea, sellerArea, cashierArea;

    public SupermarketSimulation() {
        initializeJADE();
        initializeInventory();
        initializeGUI();
    }

    private void initializeJADE() {
        try {
            runtime = jade.core.Runtime.instance();
            Profile profile = new ProfileImpl();
            container = runtime.createMainContainer(profile);

            startAgent("BuyerAgent");
            startAgent("SellerAgent");
            startAgent("CashierAgent");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAgent(String agentName) {
        try {
            AgentController agentController = container.createNewAgent(agentName, SupermarketAgent.class.getName(), new Object[]{});
            agentController.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeInventory() {
        inventory = new HashMap<>();
        inventory.put("Apples", new Product("Apples", 1.50, 50, "C://path_to_image/apple.png"));
        inventory.put("Bananas", new Product("Bananas", 0.99, 60, "C://path_to_image/banana.png"));
        inventory.put("Milk", new Product("Milk", 2.49, 30, "C://path_to_image/milk.png"));
        inventory.put("Bread", new Product("Bread", 1.99, 40, "C://path_to_image/bread.png"));
        inventory.put("Eggs", new Product("Eggs", 3.99, 20, "C://path_to_image/eggs.png"));
        cart = new HashMap<>();
    }

    private void initializeGUI() {
        frame = new JFrame("Supermarket Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);

        // Product selection panel
        JPanel topPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Product Selection"));
        productDropdown = new JComboBox<>(inventory.values().toArray(new Product[0]));
        quantityField = new JTextField();
        productDropdown.setRenderer(new ProductRenderer());

        topPanel.add(new JLabel("Select Product:"));
        topPanel.add(productDropdown);
        topPanel.add(new JLabel("Enter Quantity:"));
        topPanel.add(quantityField);

        // Cart area panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        cartArea = new JTextArea(15, 40);
        cartArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(cartArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel bottomPanel = new JPanel();
        addToCartButton = new JButton("Add to Cart");
        removeItemButton = new JButton("Remove Item");
        checkoutButton = new JButton("Checkout");
        totalLabel = new JLabel("Total: $0.00");
        JPanel paymentPanel = new JPanel();
        paymentPanel.add(new JLabel("Amount Paid:"));
        amountPaidField = new JTextField(10);
        balanceLabel = new JLabel("Remaining: $0.00");
        paymentPanel.add(amountPaidField);
        paymentPanel.add(balanceLabel);
        bottomPanel.add(addToCartButton);
        bottomPanel.add(removeItemButton);
        bottomPanel.add(checkoutButton);
        bottomPanel.add(totalLabel);
        bottomPanel.add(paymentPanel);

        // 3 Areas for communication (Buyer, Seller, Cashier)
        JPanel jadePanel = new JPanel();
        jadePanel.setLayout(new GridLayout(3, 1, 10, 10));

        buyerArea = createCommunicationArea("Buyer Communication");
        sellerArea = createCommunicationArea("Seller Communication");
        cashierArea = createCommunicationArea("Cashier Communication");

        jadePanel.add(new JScrollPane(buyerArea));
        jadePanel.add(new JScrollPane(sellerArea));
        jadePanel.add(new JScrollPane(cashierArea));

        // Action listeners
        addToCartButton.addActionListener(new AddToCartActionListener());
        removeItemButton.addActionListener(new RemoveItemActionListener());
        checkoutButton.addActionListener(new CheckoutActionListener());

        // Final setup
        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(jadePanel, BorderLayout.EAST);
        frame.setVisible(true);
    }

    private JTextArea createCommunicationArea(String title) {
        JTextArea textArea = new JTextArea(8, 40);
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createTitledBorder(title));
        return textArea;
    }

    // Action Listeners
    private class AddToCartActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Product selectedProduct = (Product) productDropdown.getSelectedItem();
            int quantity;
            try {
                quantity = Integer.parseInt(quantityField.getText());
                if (quantity <= 0) throw new NumberFormatException();
                if (selectedProduct.getStock() >= quantity) {
                    cart.put(selectedProduct.getName(), cart.getOrDefault(selectedProduct.getName(), 0) + quantity);
                    selectedProduct.decreaseStock(quantity);
                    totalPrice += selectedProduct.getPrice() * quantity;
                    totalLabel.setText("Total: $" + totalPrice);
                    cartArea.append(selectedProduct.getName() + " x" + quantity + "\n");

                    // Update the buyer's communication area
                    buyerArea.append("Added " + quantity + " " + selectedProduct.getName() + " to cart.\n");
                } else {
                    JOptionPane.showMessageDialog(frame, "Not enough stock.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid quantity.");
            }
        }
    }


    private class RemoveItemActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Product selectedProduct = (Product) productDropdown.getSelectedItem();
            if (cart.containsKey(selectedProduct.getName())) {
                int quantity = cart.get(selectedProduct.getName());
                cart.remove(selectedProduct.getName());
                totalPrice -= selectedProduct.getPrice() * quantity;
                totalLabel.setText("Total: $" + totalPrice);
                cartArea.setText("");
                cart.forEach((key, value) -> cartArea.append(key + " x" + value + "\n"));

                // Update the buyer's communication area
                buyerArea.append("Removed " + quantity + " " + selectedProduct.getName() + " from cart.\n");
            } else {
                JOptionPane.showMessageDialog(frame, "Item not found in cart.");
            }
        }
    }


    private class CheckoutActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double amountPaid = Double.parseDouble(amountPaidField.getText());
                if (amountPaid >= totalPrice) {
                    double change = amountPaid - totalPrice;
                    balanceLabel.setText("Remaining: $" + change);
                    totalPrice = 0;
                    cart.clear();
                    updateCartDisplay();

                    // Update the cashier's communication area
                    cashierArea.append("Checkout complete. Change: $" + change + "\n");
                } else {
                    JOptionPane.showMessageDialog(frame, "Insufficient funds for checkout.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid amount.");
            }
        }
    }


    private void updateCartDisplay() {
        cartArea.setText("");
        for (String productName : cart.keySet()) {
            int quantity = cart.get(productName);
            cartArea.append(productName + " x" + quantity + "\n");
        }
        totalLabel.setText("Total: $" + totalPrice);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SupermarketSimulation::new);
    }
}
