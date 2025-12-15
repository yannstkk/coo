import java.util.ArrayList;
import java.util.List;

import control.Station;
import control.observer.Observer;

public class ObserverMock implements Observer{
 
     private List<String> notifications = new ArrayList<>();
        private int updateCount = 0;

        @Override
        public void update(Station station, String action) {
            updateCount++;
            notifications.add("Station " + station.getId() + ": " + action);
        }

        public int getUpdateCount() {
            return updateCount;
        }

        public List<String> getNotifications() {
            return notifications;
        }

        public void reset() {
            updateCount = 0;
            notifications.clear();
        }




}
