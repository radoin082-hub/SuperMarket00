public class SellerAgent extends SupermarketAgent {
    @Override
    protected void setup() {
        super.setup();
        // Simulate a communication with the Buyer agent
        sendMessage("BuyerAgent", "I have products available for you.");
    }
}
