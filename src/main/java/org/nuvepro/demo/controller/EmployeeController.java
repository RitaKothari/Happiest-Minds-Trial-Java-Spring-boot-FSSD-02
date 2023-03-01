package org.nuvepro.demo.controller;

import org.nuvepro.demo.repository.EmployeeEntitiesRepository;
import org.nuvepro.demo.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.text.SimpleDateFormat;

@RestController
public class EmployeeController {

    @Autowired
    EmployeeEntitiesRepository repo;

    @Autowired
    EmployeeService service;


    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @GetMapping("employees/{employeeId}")
    public ResponseEntity getEmployeeDetailsById(@PathVariable(value = "employeeId") int employeeId){

        GetEmployeeResponse employeeResponse = new GetEmployeeResponse();
        if(employeeId<1000){
            employeeResponse.setResponseMessage("Invalid Id Provided");

            employeeResponse.setEmployeeId(employeeId);
            return new ResponseEntity(employeeResponse, HttpStatus.BAD_REQUEST);
        }
        if(service.checkIfEmployeeExists(employeeId)){

            logger.info("Employee exists. Fetching the employee details for the id: " + employeeId);

            EmployeeEntity employeeDBEntities = repo.findById(employeeId).get();

            return new ResponseEntity<EmployeeEntity>(employeeDBEntities, HttpStatus.OK);
        } else {

            logger.info("Employee doesn't exist. Check the emp id :" + employeeId);

            employeeResponse.setResponseMessage("Employee doesn't exist!! Check the employee id.");

            employeeResponse.setEmployeeId(employeeId);

            return new ResponseEntity<GetEmployeeResponse>(employeeResponse, HttpStatus.BAD_REQUEST);

        }
    }

}




