import org.junit.jupiter.api.Test;

import control.Station;
import control.User;
import exceptions.CannotParkException;
import vehicle.ClassicBicycle;
import vehicle.Vehicule;
import vehicle.state.InUseState;
import vehicle.state.ParkedState;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User user;
    private Station station;

    @BeforeEach
    public void setUp() throws CannotParkException {

        user = new User("Jean", "Dupont", 50.0);
        station = new Station(1, 10);

        for (int i = 0; i < 3; i++) {
            ClassicBicycle v = new ClassicBicycle(10.0);
            station.parkVehicule(v);
        }

    }

    @Test
    public void testUserCreation() {
        assertNull(user.getRentedVehicule());
    }

    @Test
    public void testRentVehicule() throws IllegalStateException, CannotParkException {
        user.rent(station);

        assertNotNull(user.getRentedVehicule());
        assertEquals(40.0, user.getBalance()); // 50 - 10
        assertTrue(user.getRentedVehicule().getVehiculeState() instanceof InUseState);
    }

    @Test
    public void testRentWithInsufficientBalance() throws IllegalStateException, CannotParkException {

        User poorUser = new User("Paul", "Martin", 5.0);
        poorUser.rent(station);

        assertNull(poorUser.getRentedVehicule());

        assertEquals(5.0, poorUser.getBalance());
    } // a voir ??

    @Test
    public void testCannotRentTwice() throws IllegalStateException, CannotParkException {
        user.rent(station);
        Vehicule firstVehicule = user.getRentedVehicule();

        user.rent(station);

        assertSame(firstVehicule, user.getRentedVehicule());
        assertEquals(40.0, user.getBalance());

    }

    @Test
    public void testParkVehicule() throws CannotParkException {
        user.rent(station);
        Vehicule rentedVehicule = user.getRentedVehicule();

        user.park(station);

        assertNull(user.getRentedVehicule());
        assertTrue(rentedVehicule.getVehiculeState() instanceof ParkedState);
    }

    @Test
    public void testRentFromEmptyStation() throws IllegalStateException, CannotParkException {
        Station emptyStation = new Station(2, 5);

        user.rent(emptyStation);

        assertNull(user.getRentedVehicule());
        assertEquals(50.0, user.getBalance());
    }

    @Test
    public void testParkToFullStation() throws CannotParkException {
        Station fullStation = new Station(3, 3);
        for (int i = 0; i < 3; i++) {
            fullStation.parkVehicule(new ClassicBicycle(10.0));
        }

        user.rent(station);
        Vehicule rentedVehicule = user.getRentedVehicule();

        user.park(fullStation);

        assertSame(rentedVehicule, user.getRentedVehicule());

        assertTrue(rentedVehicule.getVehiculeState() instanceof InUseState);
    }
}