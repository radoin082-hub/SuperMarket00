import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javax.swing.JTextArea;

public class SupermarketAgent extends Agent {

    private JTextArea agentCommunicationArea;

    @Override
    protected void setup() {
        String agentName = getLocalName();
        agentCommunicationArea.append(agentName + " is ready!\n");

        addBehaviour(new ReceiveMessageBehaviour());
    }

    private class ReceiveMessageBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = receive(mt);
            if (msg != null) {
                String sender = msg.getSender().getLocalName();
                String content = msg.getContent();
                agentCommunicationArea.append(sender + " says: " + content + "\n");
            } else {
                block();
            }
        }
    }

    public void sendMessage(String receiverName, String messageContent) {
        AID receiverAID = new AID(receiverName, AID.ISLOCALNAME);
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(receiverAID);
        msg.setContent(messageContent);
        send(msg);
    }

    public void setCommunicationArea(JTextArea area) {
        this.agentCommunicationArea = area;
    }
}
