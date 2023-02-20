package com.cg.api;


import com.cg.exception.DataInputException;
import com.cg.exception.EmailExistsException;
import com.cg.exception.ResourceNotFoundException;
import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.LocationRegion;
import com.cg.model.dto.CustomerDTO;
import com.cg.model.dto.DepositDTO;
import com.cg.model.dto.LocationRegionDTO;
import com.cg.service.customer.ICustomerService;
import com.cg.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerAPI {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private AppUtils appUtils;


    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {

        List<CustomerDTO> customerDTOS = customerService.findALlCustomerDTO();

        return new ResponseEntity<>(customerDTOS, HttpStatus.OK);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getById(@PathVariable Long customerId) {

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            throw new ResourceNotFoundException("Customer not found !");
        }

        Customer customer = customerOptional.get();

        return new ResponseEntity<>(customer.toCustomerDTO(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> doCreate(@Validated @RequestBody CustomerDTO customerDTO, BindingResult bindingResult) {

        new CustomerDTO().validate(customerDTO, bindingResult);

//        LocationRegionDTO locationRegionDTO = customerDTO.getLocationRegion();

//        new LocationRegionDTO().validate(locationRegionDTO, bindingResult);

        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }

        Optional<Customer> customerOptional = customerService.findByEmail(customerDTO.getEmail());

        if (customerOptional.isPresent()) {
            throw new EmailExistsException("Email entered is existed");
        }

        customerDTO.setId(null);
        customerDTO.getLocationRegion().setId(null);
        Customer customer = customerDTO.toCustomer();

        customerService.save(customer);

        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }

    @PostMapping("/deposit/{customerId}")
    public ResponseEntity<?> doDeposit(@PathVariable Long customerId, @RequestBody DepositDTO depositDTO) {

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            throw new DataInputException("Customer not valid");
        }

        Deposit deposit = depositDTO.toDeposit(customerOptional.get());

        customerService.deposit(deposit);

        Customer newCustomer = deposit.getCustomer();
        CustomerDTO newCustomerDTO = newCustomer.toCustomerDTO();

        return new ResponseEntity<>(newCustomerDTO, HttpStatus.OK);
    }
}
