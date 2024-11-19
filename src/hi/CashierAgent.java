import jade.core.Agent;

public class CashierAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println(getLocalName() + " is ready to process payments!");
    }
}
