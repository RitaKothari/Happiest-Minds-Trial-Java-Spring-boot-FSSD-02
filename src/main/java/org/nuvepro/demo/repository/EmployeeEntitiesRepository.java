package org.nuvepro.demo.repository;

import org.nuvepro.demo.controller.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.nuvepro.demo.controller.EmployeeEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface EmployeeEntitiesRepository extends JpaRepository<EmployeeEntity, Integer> {

}
