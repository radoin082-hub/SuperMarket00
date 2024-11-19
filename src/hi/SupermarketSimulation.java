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

            // Start agents and pass JTextArea for communication
            startAgent("BuyerAgent", buyerArea);
            startAgent("SellerAgent", sellerArea);
            startAgent("CashierAgent", cashierArea);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAgent(String agentName, JTextArea communicationArea) {
        try {
            AgentController agentController = container.createNewAgent(agentName, SupermarketAgent.class.getName(), new Object[] { communicationArea });
            agentController.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeInventory() {
        inventory = new HashMap<>();
        inventory.put("Apples", new Product("Apples", 1.50, 50, "C://Users//radoi//Downloads//icons8-apple-100.png"));
        inventory.put("Bananas", new Product("Bananas", 0.99, 60, "C:/Users/radoi/Downloads/icons8-banana-64.png"));
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
                    // إضافة المنتج إلى السلة
                    cart.put(selectedProduct.getName(), cart.getOrDefault(selectedProduct.getName(), 0) + quantity);
                    selectedProduct.decreaseStock(quantity);
                    totalPrice += selectedProduct.getPrice() * quantity;
                    totalLabel.setText("Total: $" + totalPrice);
                    cartArea.append(selectedProduct.getName() + " x" + quantity + "\n");

                    // تحديث منطقة الشاري
                    buyerArea.append("Added " + quantity + " " + selectedProduct.getName() + " to cart.\n");
                    buyerArea.append("Remaining stock: " + selectedProduct.getStock() + " items.\n");
                    sellerArea.append("Sold " + quantity + " " + selectedProduct.getName() + ".\n");
                } else {
                    buyerArea.append("Not enough stock."+ ".\n");
                }
            } catch (NumberFormatException ex) {
                buyerArea.append("Please enter a valid quantity."+ ".\n");
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
                buyerArea.append("Removed " + quantity + " " + selectedProduct.getName() + " from cart.\n");
                sellerArea.append("Returned " + quantity + " " + selectedProduct.getName() + ".\n");
            } else {
                buyerArea.append("Item not found in cart."+ ".\n");
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
                    cashierArea.append("Checkout complete. Change: $" + change + "\n");
                    sellerArea.append("Received payment of $" + amountPaid + ".\n");
                } else {
                    cashierArea.append("Insufficient funds for checkout."+ ".\n");
                }
            } catch (NumberFormatException ex) {
                cashierArea.append("Please enter a valid amount."+ ".\n");
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
