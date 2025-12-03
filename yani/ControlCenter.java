package yani;

import java.util.List;



public class ControlCenter implements Observer {

    private List<Station> stations;
    private Distribution distributionStrategy;
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";



    public ControlCenter(List<Station> stations, Distribution distributionStrategy) {

        this.stations = stations;
        this.distributionStrategy = distributionStrategy;


        for (Station s : stations) {
            s.attach(this);

        }
    }



    @Override
    public void update(Station station, String action) {

        System.out.println("Notification: Station " + station.getId() + " - Action: " + action);
    }

    public void checkAndRedistribute() {



        List<Station> stationsToRedistribute = new java.util.ArrayList<>();

        
        for (Station s : stations) {
            if (s.needsRedistribution()) {
                stationsToRedistribute.add(s);
            }
        }




        if (!stationsToRedistribute.isEmpty()) {
            System.out.println(GREEN+"Redistribution déclenchée pour les stations : " + stationsToRedistribute.stream().map(s -> s.getId()).toList()+RESET);
            distributionStrategy.distribute(stations); 

            

        }
    }
}