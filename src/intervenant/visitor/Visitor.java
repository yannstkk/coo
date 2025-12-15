package intervenant.visitor;

import vehicle.Vehicule;
import exceptions.IllegalStateException;

/**
 * Visitor interface for performing operations on vehicles.
 */
public interface Visitor {
    /**
     * Visits a vehicle to perform an operation
     * 
     * @param v the vehicle to visit
     * @throws IllegalStateException if an illegal state occurs during visit
     */
    public void visit(Vehicule v) throws IllegalStateException;
}