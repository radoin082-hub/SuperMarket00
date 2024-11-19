public class BuyerAgent extends SupermarketAgent {
    @Override
    protected void setup() {
        super.setup();
        // Simulate a communication with the Seller agent
        sendMessage("SellerAgent", "Hello, I want to buy some items.");
    }
}
