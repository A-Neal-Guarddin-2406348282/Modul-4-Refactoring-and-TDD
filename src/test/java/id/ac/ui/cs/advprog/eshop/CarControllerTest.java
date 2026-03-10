package id.ac.ui.cs.advprog.eshop;

import id.ac.ui.cs.advprog.eshop.controller.CarController;
import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
class CarControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CarService carService;

    @Test
    void testCreateCarPage() throws Exception {
        mockMvc.perform(get("/car/createCar"))
                .andExpect(status().isOk())
                .andExpect(view().name("createCar"))
                .andExpect(model().attributeExists("car"));
    }

    @Test
    void testCreateCarPost() throws Exception {
        mockMvc.perform(post("/car/createCar")
                        .param("carName", "Avanza")
                        .param("carColor", "Black")
                        .param("carQuantity", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:listCar"));

        verify(carService).create(any(Car.class));
    }

    @Test
    void testCarListPage() throws Exception {
        Car car = new Car();
        car.setCarId("car-1");
        car.setCarName("Civic");
        car.setCarColor("White");
        car.setCarQuantity(3);

        when(carService.findAll()).thenReturn(List.of(car));

        mockMvc.perform(get("/car/listCar"))
                .andExpect(status().isOk())
                .andExpect(view().name("carList"))
                .andExpect(model().attributeExists("cars"));

        verify(carService).findAll();
    }

    @Test
    void testEditCarPage() throws Exception {
        Car car = new Car();
        car.setCarId("car-1");
        car.setCarName("Jazz");
        car.setCarColor("Red");
        car.setCarQuantity(1);

        when(carService.findById("car-1")).thenReturn(car);

        mockMvc.perform(get("/car/editCar/car-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("editCar"))
                .andExpect(model().attributeExists("car"));

        verify(carService).findById("car-1");
    }

    @Test
    void testEditCarPost() throws Exception {
        mockMvc.perform(post("/car/editCar")
                        .param("carId", "car-1")
                        .param("carName", "Updated Car")
                        .param("carColor", "Blue")
                        .param("carQuantity", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:listCar"));

        verify(carService).update(eq("car-1"), any(Car.class));
    }

    @Test
    void testDeleteCarPost() throws Exception {
        mockMvc.perform(post("/car/deleteCar")
                        .param("carId", "car-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:listCar"));

        verify(carService).deleteCarById("car-1");
    }
}