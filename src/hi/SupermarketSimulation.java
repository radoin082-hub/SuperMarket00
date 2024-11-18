import jade.core.*;
import jade.core.behaviours.*;
import jade.wrapper.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
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
    private JButton addToCartButton, checkoutButton, removeItemButton;
    private JLabel totalLabel, balanceLabel;
    private JTextField amountPaidField;
    private HashMap<String, Product> inventory;
    private HashMap<String, Integer> cart;
    private double totalPrice = 0.0;

    // JADE Container
    private jade.core.Runtime runtime;
    private AgentContainer container;

    // Add a new JTextArea to display JADE agent communication
    private JTextArea jadeStatusArea;

    // Constructor to initialize everything
    public SupermarketSimulation() {
        initializeJADE();
        initializeInventory();
        initializeGUI();
    }

    // Initialize JADE runtime
    private void initializeJADE() {
        try {
            // Create the JADE runtime
            runtime = jade.core.Runtime.instance();
            Profile profile = new ProfileImpl();
            container = runtime.createMainContainer(profile);

            // Start Buyer, Seller, and Cashier agents
            startAgent("BuyerAgent");
            startAgent("SellerAgent");
            startAgent("CashierAgent");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Start a JADE agent
    private void startAgent(String agentName) {
        try {
            AgentController agentController = container.createNewAgent(agentName, SupermarketAgent.class.getName(), new Object[]{});
            agentController.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Initialize the product inventory
    private void initializeInventory() {
        inventory = new HashMap<>();
        inventory.put("Apples", new Product("Apples", 1.50, 50, "C://Users//radoi//Downloads//icons8-apple-100.png"));
        inventory.put("Bananas", new Product("Bananas", 0.99, 60, "C://Users//radoi//Downloads//icons8-banana-64.png"));
        inventory.put("Milk", new Product("Milk", 2.49, 30, "resources/milk.jpg"));
        inventory.put("Bread", new Product("Bread", 1.99, 40, "resources/bread.jpg"));
        inventory.put("Eggs", new Product("Eggs", 3.99, 20, "resources/eggs.jpg"));
        inventory.put("Chocolate", new Product("Chocolate", 4.99, 15, "resources/chocolate.jpg"));
        inventory.put("Orange Juice", new Product("Orange Juice", 3.49, 25, "resources/orange_juice.jpg"));
        inventory.put("Cereal", new Product("Cereal", 2.99, 50, "resources/cereal.jpg"));

        cart = new HashMap<>();
    }

    // Initialize the GUI layout
    private void initializeGUI() {
        frame = new JFrame("Supermarket Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Set up the top panel for product selection and quantity input
        JPanel topPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Product Selection"));
        productDropdown = new JComboBox<>(inventory.values().toArray(new Product[0]));
        quantityField = new JTextField();

        // Use a custom renderer to display product image and name in the dropdown
        productDropdown.setRenderer(new ProductRenderer());

        topPanel.add(new JLabel("Select Product:"));
        topPanel.add(productDropdown);
        topPanel.add(new JLabel("Enter Quantity:"));
        topPanel.add(quantityField);

        // Center panel for cart display and buttons
        JPanel centerPanel = new JPanel(new BorderLayout());
        cartArea = new JTextArea(15, 40);
        cartArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(cartArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for actions (Add to Cart, Remove, Checkout)
        JPanel bottomPanel = new JPanel();
        addToCartButton = new JButton("Add to Cart");
        removeItemButton = new JButton("Remove Item");
        checkoutButton = new JButton("Checkout");
        totalLabel = new JLabel("Total: $0.00");

        // New panel for amount paid and remaining balance
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

        // New panel to display JADE communication status
        JPanel jadePanel = new JPanel();
        jadePanel.setLayout(new BorderLayout());
        jadeStatusArea = new JTextArea(8, 40);
        jadeStatusArea.setEditable(false);
        jadeStatusArea.setBorder(BorderFactory.createTitledBorder("JADE Agent Communication"));
        jadePanel.add(new JScrollPane(jadeStatusArea), BorderLayout.CENTER);

        // Add action listeners
        addToCartButton.addActionListener(new AddToCartActionListener());
        removeItemButton.addActionListener(new RemoveItemActionListener());
        checkoutButton.addActionListener(new CheckoutActionListener());

        // Assemble the frame
        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(jadePanel, BorderLayout.EAST); // Add the JADE panel to the East of the frame

        frame.setVisible(true);
    }

    // Add product to the cart
    private class AddToCartActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Product selectedProduct = (Product) productDropdown.getSelectedItem();
            int quantity;

            try {
                quantity = Integer.parseInt(quantityField.getText());
                if (quantity <= 0) throw new NumberFormatException();

                if (selectedProduct.getStock() >= quantity) {
                    // Update cart and inventory
                    cart.put(selectedProduct.getName(), cart.getOrDefault(selectedProduct.getName(), 0) + quantity);
                    selectedProduct.decreaseStock(quantity);

                    // Update total price
                    totalPrice += selectedProduct.getPrice() * quantity;

                    // Update cart display
                    updateCartDisplay();

                    // Update JADE panel with action
                    jadeStatusArea.append("Added " + quantity + " " + selectedProduct.getName() + " to cart.\n");
                } else {
                    JOptionPane.showMessageDialog(frame, "Insufficient stock for " + selectedProduct.getName());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid quantity.");
            }
        }
    }

    // Remove item from the cart
    private class RemoveItemActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Product selectedProduct = (Product) productDropdown.getSelectedItem();

            if (cart.containsKey(selectedProduct.getName())) {
                int quantityInCart = cart.get(selectedProduct.getName());
                // Update total price and inventory
                totalPrice -= selectedProduct.getPrice() * quantityInCart;
                selectedProduct.increaseStock(quantityInCart);

                // Remove item from the cart
                cart.remove(selectedProduct.getName());

                // Update cart display
                updateCartDisplay();

                // Update JADE panel with action
                jadeStatusArea.append("Removed " + quantityInCart + " " + selectedProduct.getName() + " from cart.\n");
            } else {
                JOptionPane.showMessageDialog(frame, "Product not in cart.");
            }
        }
    }

    // Handle checkout process
    private class CheckoutActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Your cart is empty.");
                return;
            }

            // Get amount paid and calculate remaining balance
            try {
                double amountPaid = Double.parseDouble(amountPaidField.getText());
                double remainingBalance = amountPaid - totalPrice;

                balanceLabel.setText("Remaining: $" + String.format("%.2f", remainingBalance));

                if (remainingBalance < 0) {
                    JOptionPane.showMessageDialog(frame, "Insufficient funds! Please pay the total amount.");
                } else {
                    // Proceed with agent communication
                    simulateAgents();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid amount.");
            }
        }
    }

    // Update cart display
    private void updateCartDisplay() {
        cartArea.setText("");
        for (String productName : cart.keySet()) {
            cartArea.append(productName + " (x" + cart.get(productName) + ")\n");
        }
        totalLabel.setText("Total: $" + String.format("%.2f", totalPrice));
    }

    // Simulate agent communication
    private void simulateAgents() {
        System.out.println("BuyerAgent: Placing order...");
        for (String product : cart.keySet()) {
            int quantity = cart.get(product);
            System.out.println("SellerAgent: Checking stock for " + product + " (x" + quantity + ")");
        }
        System.out.println("CashierAgent: Processing payment...");

        // Update JADE panel with agent actions
        jadeStatusArea.append("BuyerAgent: Placing order...\n");
        for (String product : cart.keySet()) {
            int quantity = cart.get(product);
            jadeStatusArea.append("SellerAgent: Checking stock for " + product + " (x" + quantity + ")\n");
        }
        jadeStatusArea.append("CashierAgent: Processing payment...\n");
    }

    // Run the simulation
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SupermarketSimulation());
    }
}
