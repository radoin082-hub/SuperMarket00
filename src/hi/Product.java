import javax.swing.*;

import java.awt.*;

import static javax.swing.text.StyleConstants.setIcon;

// Supermarket Product class with basic details
class Product {
    private String name;
    private double price;
    private int stock;
    private String imageUrl;

    public Product(String name, double price, int stock, String imageUrl) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void decreaseStock(int quantity) {
        stock -= quantity;
    }

    public void increaseStock(int quantity) {
        stock += quantity;
    }

    @Override
    public String toString() {
        return name;
    }
}

// Custom renderer for product dropdown to display image and name
class ProductRenderer extends JLabel implements ListCellRenderer<Product> {
    @Override
    public Component getListCellRendererComponent(JList<? extends Product> list, Product value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.getName());
        setIcon(new ImageIcon(value.getImageUrl()));
        return this;
    }
}

