import jade.core.Agent;

public class SupermarketAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println(getLocalName() + " is ready!");
    }
}
