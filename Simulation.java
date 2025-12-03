package yani;
import Mehdi.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;



public class Simulation {


    
    private List<Station> stations = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private ControlCenter controlCenter;
    private Random random = new Random();
    private Technician technician = new Technician();
    private int nextVehiculeId = 0;
    private Colors colors = new Colors();





     public Simulation() {
        this(new RoundRobin());
    }

    public Simulation(Distribution distributionStrategy) {

        
        for (int i = 1; i <= 3; i++) {
            stations.add(new Station(i, random.nextInt(11) + 10)); 
        }


        for (Station s : stations) {
        int nbVehicules = random.nextInt(6) + 5;

        for (int i = 0; i < nbVehicules; i++) {
            double basePrice = 10.0;
            Vehicule v = new ClassicBicycle(nextVehiculeId++, State.PARKED, basePrice);

            if (random.nextBoolean()) {
                v = new Basket(v);  // Basket prend un Vehicule en paramètre → OK
            }

            s.parkVehicule(v);
        }
    }



        for (int i = 0; i < 10; i++) {
            User u = new User("Nom num " + i, "prenom num " + i, 100.0); // il faudrai creer des clients avec des balance varier
            users.add(u);
        }

        //controlCenter = new ControlCenter(stations, new RoundRobin());  donner la main a l'utilisateur de choisir la trategie 
        controlCenter = new ControlCenter(stations, distributionStrategy);

    }







    public void runSimulation() {


        int cycle = 1;
        while (true) {


            System.out.println(colors.getYellow() + "\n=== Début du cycle " + cycle + " ===" + colors.getReset());
            System.out.println(colors.getYellow() + "----------------------------------------" + colors.getReset());

            int numActions = random.nextInt(5) + 1;
            System.out.println("-- "+ numActions + ""+colors.getGreen()+" Actions ont été géneré pour ce cycle actions aléatoires --\n"+ colors.getReset());





            Set<User> alreadyActed = new HashSet<>();

            for (int i = 0; i < numActions; i++) {
                // On ne prend que les utilisateurs qui n'ont PAS encore agi ce cycle
                List<User> availableUsers = users.stream()
                        .filter(u -> !alreadyActed.contains(u))
                        .collect(Collectors.toList());

                if (availableUsers.isEmpty()) break; // plus personne disponible

                User u = availableUsers.get(random.nextInt(availableUsers.size()));
                alreadyActed.add(u);

                Station s = stations.get(random.nextInt(stations.size()));

                if (u.getRentedVehicule() == null) {
                    u.rent(s);
                } else {
                    u.park(s);
                }
            } // on a fais ca pour eviter que la meme personne ne fasse plus d'une action par cycle 




            System.out.println("\nVoici le l'etat des stations aprés ce cycle : \n");
            for (Station s : stations) {
                System.out.println("Station dont l'id est " + s.getId() + " est Occupé par = " + s.getNbOccupiedSlot() + " velos pour une capacite de : " + s.getCapacity());
            }





            // System.out.println("Vérification des vols...");

            for (Station s : stations) {
                s.verifyStolen();
            }




            // System.out.println("Mise à jour des compteurs vide/plein...");

            for (Station s : stations) {
                s.incrementEmptyFullCounters();
            }



            
            // System.out.println("Vérification de la redistribution...");


            controlCenter.checkAndRedistribute();


            // System.out.println("Vérification des réparations...");



            boolean repairTriggered = false;

            for (Station s : stations) {

                for (Slot slot : s.getSlotList()) {


                    if (slot.getIsOccupied()) {
                        Vehicule v = slot.getActualVehicule();
                        if (v.getLocationNb() > 5 && v.getVehiculeState() == State.PARKED) { 

                            System.out.println("Velo dont l'ID est " + v.getId() + " se trouvant dans la station " + s.getId() + " : vient d'avoir " + v.getLocationNb() + " il vas donc en reparation ");
                            v.setState(State.UNDER_REPAIR);

                            v.setRepairIntervalsRemaining(1);
                            repairTriggered = true;





                        } // dans un cycle le velo est mis en raparation ensuite dans le cycle suivant il est effectivement reparer

                        else if (v.getVehiculeState() == State.UNDER_REPAIR) {



                            int remaining = v.getRepairIntervalsRemaining();

                            // System.out.println("le Velo dont l'ID " + v.getId() + " se trouvant dans la station " + s.getId() + " : Temps de réparation restant = " + remaining);
                            if (remaining > 0) {
                                v.setRepairIntervalsRemaining(remaining - 1); }



                            if (remaining <= 1) {

                                technician.repair(v);

                                System.out.println("Velo dont l'ID est " + v.getId() + " se trouvant dans la station " + s.getId() + " est Réparé ! ");
                            }

                            repairTriggered = true;
                        }
                    }





                }
            }

            if (!repairTriggered) {
                System.out.println("Aucune réparation en cours ou déclenchée.");
            }

            System.out.println(colors.getBlue() + "----------------------------------------" + colors.getReset());
            System.out.println("Fin du cycle "+cycle+"\n\n\n\n");


            cycle++;




            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}