package yani;

import Mehdi.*;
import java.util.ArrayList;
import java.util.List;




public class Station {
    private int id;
    private int capacity;
    private List<Slot> slotList = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>();
    private int emptyIntervals = 0;
    private int fullIntervals = 0;
    private int IntervalsOfTheft = 0; 
     private Colors colors = new Colors();


    public Station(int id, int capacity) {

        this.id = id;
        
        this.capacity = capacity;
        
        
        for (int i = 0; i < capacity; i++) {
            slotList.add(new Slot(i));
        }
    }

    //////////////////////////////////////////////

    public void attach(Observer observer) {
        observers.add(observer);
    }

    private void notifyObservers(String action) {

        for (Observer o : observers) {
            o.update(this, action);
        }
    }

    //////////////////////// on fait s'abonner un observeur  




    public boolean isEmpty() {
        return getNbOccupiedSlot() == 0; }



    public boolean isFull() {
        return getNbOccupiedSlot() == capacity; }




        
    public int getNbOccupiedSlot() {

        int count = 0;

        for (Slot slot : slotList) {
            if (slot.getIsOccupied()) count++;
        }

        return count; 
    } //Retourne le nombre d'emplacements occupé






    public int getCapacity() {
        return capacity;
    }




    private void resetCountersIfChanged() {
        if (!isEmpty()) emptyIntervals = 0;
        if (!isFull()) fullIntervals = 0;
        if (getNbOccupiedSlot() != 1) IntervalsOfTheft = 0;
    }



    public int getId() {
        return id;
    }


    
    public List<Slot> getSlotList() {
        return slotList;
    }





















    public void parkVehicule(Vehicule vehicule) {

        if (!isFull() && vehicule.getVehiculeState() != State.UNDER_REPAIR && vehicule.getVehiculeState() != State.STOLEN) {


            for (Slot slot : slotList) {

                if (!slot.getIsOccupied()) {

                    slot.setActualVehicule(vehicule);
                    slot.setIsOccupied(true);
                    vehicule.setState(State.PARKED);
                    notifyObservers("park");
                    resetCountersIfChanged();

                    System.out.println(colors.getOrange() + "le Vélo dont l'ID " + vehicule.getId() 
                    + " vient d'être garé à la station " + this.id 
                    + " --> son nombre de locations totales reste : " + vehicule.getLocationNb() + colors.getReset());

                    break;
                }
            }
        }
    } // Ajoute un véhicule dans un emplacement libre.






    public Vehicule rentVehicule() {

    if (!isEmpty()) {

        for (Slot slot : slotList) {

            if (slot.getIsOccupied() && slot.getActualVehicule().getVehiculeState() == State.PARKED) {

                Vehicule v = slot.getActualVehicule(); //retourne le velo qui garee a cet emplacement
                slot.setActualVehicule(null);
                slot.setIsOccupied(false);
                v.setState(State.IN_USE);
                v.incrementLocationNb();



                System.out.println(colors.getBlue()+"le Vélo dont l'ID " + v.getId() + " viens d'etre loué --> son nombre de location totale vaut : " + v.getLocationNb()+colors.getReset());
                notifyObservers("rent");
                resetCountersIfChanged();
                return v;
            }
        }
    }
    return null;
}

    public Vehicule removeVehiculeForRedistribution() {

        if (!isEmpty()) {

            for (Slot slot : slotList) {

                if (slot.getIsOccupied() && slot.getActualVehicule().getVehiculeState() == State.PARKED) {

                    Vehicule v = slot.getActualVehicule();
                    slot.setActualVehicule(null);
                    slot.setIsOccupied(false);
                    notifyObservers("redistribute_remove");
                    resetCountersIfChanged();


                    return v;
                }
            }
        }
        return null;
    }

    public void verifyStolen() {

        

        if (getNbOccupiedSlot() == 1) {
            Slot slot = slotList.stream().filter(Slot::getIsOccupied).findFirst().orElse(null);
            // cette ligne cherche et retourne le seul emplacement occupée restant dans la satation

            if (slot != null && slot.getActualVehicule().getVehiculeState() == State.PARKED) {

                IntervalsOfTheft++;

                System.out.println(colors.getRed()+"la station dont l'id est " + id + " : vient d'avoir un velo rester seul pendant 1 interval de temps "+
                " le Compteur de vol a donc été incrementé, il vaut = " + IntervalsOfTheft+colors.getReset());
                
                if (IntervalsOfTheft >= 2) {

                    Vehicule v = slot.getActualVehicule();
                    System.out.println(colors.getRed()+"la Station dont l'id est " + id + " vient de se faire volé Vélo : " + v.getId() + " !"+colors.getReset());
                    
                    
                    
                    v.setState(State.STOLEN);
                    slot.setActualVehicule(null);
                    slot.setIsOccupied(false);
                    notifyObservers("stolen");
                    IntervalsOfTheft = 0;


                }


            } else {
                IntervalsOfTheft = 0;

                System.out.println(colors.getPurple()+"Station dont l'id est " + id + " : a un Vélo seul mais pas geré (a été retiré ), pas de vol possible."+colors.getReset());
            }
        } else {

            IntervalsOfTheft = 0;

            System.out.println(colors.getPurple()+"Station dont l'id est " + id + " a " + getNbOccupiedSlot() + " emplacement occupé elle n'est donc pas élegible au vole"+colors.getReset());
        }
    }// ici les intervales de temps son successif, c'est pour ca qu'on remet direct a 0






    public void incrementEmptyFullCounters() {

        if (isEmpty()) {
            emptyIntervals++;
        
        } else { emptyIntervals = 0; }


        if (isFull()) {
            fullIntervals++;
        } else { fullIntervals = 0; }


        // System.out.println("Station dont l'id est " + id + " est vide depuis = " + emptyIntervals + " interval, sinon est pleine depuis = " + fullIntervals + " interval, elle est occupe par = " + getNbOccupiedSlot() + "velos, et a pour capacite : " + getCapacity());
    }// incrémente les compteurs vide ou plein





    public boolean needsRedistribution() {
        return emptyIntervals > 2 || fullIntervals > 2;
    }






    
}