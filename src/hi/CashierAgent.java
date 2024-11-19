import jade.lang.acl.ACLMessage;

public class CashierAgent extends SupermarketAgent {
    @Override
    protected void setup() {
        super.setup();
        // Simulate a communication with the Buyer agent
        sendMessageToBuyer("Please proceed to checkout.");
    }

    // Define the sendMessage method directly here for CashierAgent
    public void sendMessageToBuyer(String messageContent) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setContent(messageContent);
        msg.addReceiver(getAID("BuyerAgent"));
        send(msg);
        System.out.println(getName() + " sent message to BuyerAgent: " + messageContent);
    }
}
