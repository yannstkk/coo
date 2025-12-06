package intervenant.visitor;

import vehicle.Vehicule;

public interface Visitor {
    public void visit(Vehicule v) throws IllegalStateException;
}