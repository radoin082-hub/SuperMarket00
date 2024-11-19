public class CashierAgent extends SupermarketAgent {
    @Override
    protected void setup() {
        super.setup();
        // Simulate a communication with the Buyer and Seller agents
        sendMessage("BuyerAgent", "Please proceed to checkout.");
    }
}
