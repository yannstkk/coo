package control.observer;

import control.Station;

/**
 * Observer interface for monitoring station events.
 */
public interface Observer {
    /**
     * Called when a station event occurs.
     * 
     * @param station the station that triggered the update
     * @param action  the action that occurred
     */
    void update(Station station, String action);
}