package Mehdi;

public class Technician {
    public void repair(Vehicule vehicule) {
        vehicule.setState(State.PARKED);
        vehicule.setRepairIntervalsRemaining(0);
    }
}