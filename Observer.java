package yani;

public interface Observer {
    void update(Station station, String action); 
    // "rent", "park", "stolen" pour notifier
}
