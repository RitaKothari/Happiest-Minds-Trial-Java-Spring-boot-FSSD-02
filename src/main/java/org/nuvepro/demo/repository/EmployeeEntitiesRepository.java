package org.nuvepro.demo.repository;

import org.nuvepro.demo.controller.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.nuvepro.demo.controller.EmployeeEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface EmployeeEntitiesRepository extends JpaRepository<EmployeeEntity, Integer> {

    List<EmployeeEntity> findByDepartment(String department);

    EmployeeEntity findByEmailEquals(String email);

    List<EmployeeEntity> findByNameContains(String name);

    List<EmployeeEntity> findByNameEquals(String name);

    List<EmployeeEntity> findByNameIs(String name);

    List<EmployeeEntity> findByNameIgnoreCase(String name);


    List<EmployeeEntity> findByName(String name);



    List<EmployeeEntity> findByCityContains(String city);

    long countByDepartmentEquals(String department);

    List<EmployeeEntity> findByHireDateBetween(Date hireDateStart, Date hireDateEnd);

    long countByCityContains(String city);

    long countByNameEquals(String name);

    boolean existsByIdEquals(long id);




    List<EmployeeEntity> findByHireDateLessThanEqual(Date hireDate);

    List<EmployeeEntity> findByHireDateGreaterThanEqual(Date hireDate);

    List<EmployeeEntity> findByHireDateEquals(Date hireDate);

    List<EmployeeEntity> findByHireDateLessThan(Date hireDate);

    List<EmployeeEntity> findByHireDateGreaterThan(Date hireDate);

    long countByHireDateLessThan(Date hireDate);

    long countByHireDateLessThanEqual(Date hireDate);

    long countByHireDateEquals(Date hireDate);


    long countByHireDateGreaterThan(Date hireDate);

    long countByHireDateGreaterThanEqual(Date hireDate);

    long countByHireDateBetween(Date hireDateStart, Date hireDateEnd);

    long deleteByIdEquals(long id);

    EmployeeEntity findByIdEquals(long id);


















}
