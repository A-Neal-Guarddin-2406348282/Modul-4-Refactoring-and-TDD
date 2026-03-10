package id.ac.ui.cs.advprog.eshop;

import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.repository.CarRepository;
import id.ac.ui.cs.advprog.eshop.service.CarServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    CarRepository carRepository;

    @InjectMocks
    CarServiceImpl carService;

    @Test
    void testCreate() {
        Car car = new Car();
        car.setCarId("car-1");
        car.setCarName("Avanza");
        car.setCarColor("Black");
        car.setCarQuantity(2);

        Car result = carService.create(car);

        assertSame(car, result);
        verify(carRepository).create(car);
    }

    @Test
    void testFindAll() {
        Car car1 = new Car();
        car1.setCarId("car-1");
        car1.setCarName("Civic");

        Car car2 = new Car();
        car2.setCarId("car-2");
        car2.setCarName("Jazz");

        Iterator<Car> carIterator = Arrays.asList(car1, car2).iterator();
        when(carRepository.findAll()).thenReturn(carIterator);

        List<Car> result = carService.findAll();

        assertEquals(2, result.size());
        assertEquals("car-1", result.get(0).getCarId());
        assertEquals("car-2", result.get(1).getCarId());
        verify(carRepository).findAll();
    }

    @Test
    void testFindAllWhenEmpty() {
        when(carRepository.findAll()).thenReturn(List.<Car>of().iterator());

        List<Car> result = carService.findAll();

        assertEquals(0, result.size());
        verify(carRepository).findAll();
    }

    @Test
    void testFindByIdWhenFound() {
        Car car = new Car();
        car.setCarId("car-1");
        car.setCarName("Brio");

        when(carRepository.findById("car-1")).thenReturn(car);

        Car result = carService.findById("car-1");

        assertSame(car, result);
        verify(carRepository).findById("car-1");
    }

    @Test
    void testFindByIdWhenNotFound() {
        when(carRepository.findById("missing")).thenReturn(null);

        Car result = carService.findById("missing");

        assertNull(result);
        verify(carRepository).findById("missing");
    }

    @Test
    void testUpdate() {
        Car updatedCar = new Car();
        updatedCar.setCarId("car-1");
        updatedCar.setCarName("Updated Car");
        updatedCar.setCarColor("Blue");
        updatedCar.setCarQuantity(5);

        carService.update("car-1", updatedCar);

        verify(carRepository).update("car-1", updatedCar);
    }

    @Test
    void testDeleteCarById() {
        carService.deleteCarById("car-1");

        verify(carRepository).delete("car-1");
    }
}