package org.nuvepro.demo.controller;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import javax.persistence.*;
import org.hibernate.annotations.Parameter;


@Entity
@Data
@Table(name = "Employee")
public class EmployeeEntity {

    @Id
    @GeneratedValue(generator ="employee_id" ,strategy = GenerationType.IDENTITY)
    @Column(name="id",length = 10)
    @SequenceGenerator(name="employee_id",sequenceName = "id",initialValue = 1000,allocationSize =1)
    private Integer id;

    @Column(name = "name",length = 100)
    private String name;

    @Column(name = "department",length = 100)
    private String department;

    @Column(name = "HireDate", nullable = false)
    private Date hireDate;

    @Column(name = "age",length = 2)
    private int age;

    @Column(name = "sex",length = 6)
    private String sex;

    @Column(name = "city",length = 50)
    private String city;

    @Column(name = "phone",length = 20)
    private String phone;

    @Column(name = "email",length = 50)
    private String email;

}
