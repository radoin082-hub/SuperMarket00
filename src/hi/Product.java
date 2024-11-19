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
