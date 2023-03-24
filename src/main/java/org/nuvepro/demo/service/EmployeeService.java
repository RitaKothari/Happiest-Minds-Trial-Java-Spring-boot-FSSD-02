package org.nuvepro.demo.service;

import org.nuvepro.demo.repository.EmployeeEntitiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    EmployeeEntitiesRepository repo;

    public boolean checkIfEmployeeExists(int employeeId){

        return repo.findById(employeeId).isPresent();
    }

//    public boolean checkIfEmployeeExistsforemail(String Emailid){
//
//        return repo.findByEmailEquals(Emailid).getEmail();
//    }
}
