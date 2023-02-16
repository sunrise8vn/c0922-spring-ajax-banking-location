package com.cg.api;


import com.cg.model.Customer;
import com.cg.model.LocationRegion;
import com.cg.model.dto.CustomerDTO;
import com.cg.model.dto.LocationRegionDTO;
import com.cg.service.customer.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerAPI {

    @Autowired
    private ICustomerService customerService;


    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {

        List<CustomerDTO> customerDTOS = customerService.findALlCustomerDTO();

        return new ResponseEntity<>(customerDTOS, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Customer> doCreate(@RequestBody CustomerDTO customerDTO) {
        customerDTO.setId(null);
        customerDTO.getLocationRegion().setId(null);
        Customer customer = customerDTO.toCustomer();

        customerService.save(customer);

        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }
}
