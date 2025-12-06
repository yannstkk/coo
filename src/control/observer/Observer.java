package control.observer;

import control.Station;

public interface Observer {
    void update(Station station, String action);
    // "rent", "park", "stolen" pour notifier
}
