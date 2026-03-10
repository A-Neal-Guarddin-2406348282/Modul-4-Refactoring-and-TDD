package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class CarRepositoryTest {

    private CarRepository carRepository;
    private Car car1;
    private Car car2;

    @BeforeEach
    void setUp() {
        carRepository = new CarRepository();

        car1 = new Car();
        car1.setCarId("car-1");
        car1.setCarName("Avanza");
        car1.setCarColor("Black");
        car1.setCarQuantity(2);

        car2 = new Car();
        car2.setCarId("car-2");
        car2.setCarName("Civic");
        car2.setCarColor("White");
        car2.setCarQuantity(3);
    }

    @Test
    void testCreateWithExistingId() {
        Car result = carRepository.create(car1);

        assertSame(car1, result);
        assertEquals("car-1", result.getCarId());
    }

    @Test
    void testCreateWithNullIdGeneratesId() {
        Car car = new Car();
        car.setCarName("Jazz");
        car.setCarColor("Red");
        car.setCarQuantity(1);

        Car result = carRepository.create(car);

        assertNotNull(result.getCarId());
        assertEquals("Jazz", result.getCarName());
    }

    @Test
    void testFindAll() {
        carRepository.create(car1);
        carRepository.create(car2);

        Iterator<Car> iterator = carRepository.findAll();

        assertTrue(iterator.hasNext());
        assertEquals(car1, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(car2, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void testFindByIdIfFound() {
        carRepository.create(car1);
        carRepository.create(car2);

        Car result = carRepository.findById("car-2");

        assertNotNull(result);
        assertEquals("car-2", result.getCarId());
        assertEquals("Civic", result.getCarName());
    }

    @Test
    void testFindByIdIfNotFound() {
        carRepository.create(car1);

        Car result = carRepository.findById("missing");

        assertNull(result);
    }

    @Test
    void testUpdateIfFound() {
        carRepository.create(car1);

        Car updatedCar = new Car();
        updatedCar.setCarName("Updated Avanza");
        updatedCar.setCarColor("Blue");
        updatedCar.setCarQuantity(5);

        Car result = carRepository.update("car-1", updatedCar);

        assertNotNull(result);
        assertEquals("car-1", result.getCarId());
        assertEquals("Updated Avanza", result.getCarName());
        assertEquals("Blue", result.getCarColor());
        assertEquals(5, result.getCarQuantity());
    }

    @Test
    void testUpdateIfNotFound() {
        Car updatedCar = new Car();
        updatedCar.setCarName("Updated");
        updatedCar.setCarColor("Green");
        updatedCar.setCarQuantity(10);

        Car result = carRepository.update("missing", updatedCar);

        assertNull(result);
    }

    @Test
    void testDeleteIfFound() {
        carRepository.create(car1);
        carRepository.create(car2);

        carRepository.delete("car-1");

        assertNull(carRepository.findById("car-1"));
        assertNotNull(carRepository.findById("car-2"));
    }

    @Test
    void testDeleteIfNotFound() {
        carRepository.create(car1);

        carRepository.delete("missing");

        assertNotNull(carRepository.findById("car-1"));
    }
}