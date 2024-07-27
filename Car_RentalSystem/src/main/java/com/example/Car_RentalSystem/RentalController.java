package com.example.Car_RentalSystem;
import com.example.Car_RentalSystem.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/rentals")
public class RentalController {

    @Autowired
    private CarService carService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RentalService rentalService;

    @GetMapping("/available-cars")
    public String viewAvailableCars(Model model) {
        List<Car> availableCars = carService.findAll().stream()
                .filter(Car::isAvailable)
                .toList();
        model.addAttribute("cars", availableCars);
        return "available-cars";
    }
    @GetMapping("returns")
    public String getReturns(Model model){
        List<Rental>list =rentalService.findAll();
        model.addAttribute("returns",list);
        return "returns";
    }

    @GetMapping("/rent/{carId}")
    public String rentCarForm(@PathVariable Long carId, Model model) {
        Car car = carService.findById(carId);
        List<Customer> customers = customerService.getAllCustomers();
        model.addAttribute("car", car);
        model.addAttribute("customers", customers);
        model.addAttribute("rental", new Rental());
        return "rent-car";
    }

    @PostMapping("/rent")
    public String rentCar(@ModelAttribute Rental rental, @RequestParam Long carId, @RequestParam Long customerId) {
        Car car = carService.findById(carId);
        Customer customer= customerService.getCustomerById(customerId);
        rental.setCar(car);
        rental.setCustomer(customer);
        rental.setRentalDate(LocalDate.now());
        rentalService.save(rental);
        car.setAvailable(false);
        carService.save(car);
        return "redirect:/rentals/available-cars";
    }

    @GetMapping("/return/{rentalId}")
    public String returnCarForm(@PathVariable Long rentalId, Model model) {
        Rental rental = rentalService.findById(rentalId);
        model.addAttribute("rental", rental);
        return "return-car";
    }

    @PostMapping("/return")
    public String returnCar(@RequestParam Long rentalId) {
        Rental rental = rentalService.findById(rentalId);
        Car car = rental.getCar();
        car.setAvailable(true);
        carService.save(car);
        rental.setReturnDate(LocalDate.now());
        rentalService.save(rental);
        return "redirect:/rentals/available-cars";
    }
}

