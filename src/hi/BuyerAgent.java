import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class BuyerAgent extends Agent {

    @Override
    protected void setup() {
        // Example of how the agent might send a message
        System.out.println(getName() + " is ready to communicate.");

        // Send a message to another agent (e.g., SellerAgent)
        sendMessage("SellerAgent", "Hello, I'm BuyerAgent, I want to buy something.");
    }

    public void sendMessage(String recipientName, String messageContent) {
        // Create a new ACLMessage
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);  // You can use other types like ACLMessage.INFORM based on your needs
        msg.setContent(messageContent);  // Set the content of the message
        msg.addReceiver(getAID(recipientName));  // Use getAID to get the receiver's AID (Agent ID)

        // Send the message
        send(msg);
        System.out.println(getName() + " sent message to " + recipientName + ": " + messageContent);
    }

    @Override
    protected void takeDown() {
        System.out.println(getName() + " is shutting down.");
    }
}
