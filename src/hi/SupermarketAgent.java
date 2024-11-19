import jade.core.Agent;
import javax.swing.JTextArea;

public class SupermarketAgent extends Agent {
    private JTextArea agentCommunicationArea;

    @Override
    protected void setup() {
        // Get the passed JTextArea for communication
        Object[] args = getArguments();
        if (args != null && args.length > 0 && args[0] instanceof JTextArea) {
            agentCommunicationArea = (JTextArea) args[0];
        }

        if (agentCommunicationArea != null) {
            agentCommunicationArea.append(getName() + " is ready.\n");
        } else {
            System.out.println("Communication area not initialized.");
        }
    }

    @Override
    protected void takeDown() {
        if (agentCommunicationArea != null) {
            agentCommunicationArea.append(getName() + " is shutting down.\n");
        }
    }
}
