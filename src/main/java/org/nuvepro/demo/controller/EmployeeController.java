package org.nuvepro.demo.controller;

import org.nuvepro.demo.repository.EmployeeEntitiesRepository;
import org.nuvepro.demo.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.time.Month;

import static java.lang.Character.isDigit;
import static javax.swing.UIManager.get;

@RestController
public class EmployeeController {

    @Autowired
    EmployeeEntitiesRepository repo;

    @Autowired
    EmployeeService service;


    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);


    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("employees/login")
    public ResponseEntity getAdminCredentials(@RequestBody UserAuthentication user){
        UserAuthenticatationResponse response=new UserAuthenticatationResponse();

        GetEmployeeResponse employeeResponse = new GetEmployeeResponse();
        if(user.username.isEmpty()){
            response.setResponseMessage("Username not provided");
            response.setStatus("username");
            return new ResponseEntity(response,HttpStatus.BAD_REQUEST);
        }else if(user.password.isEmpty()){
            response.setResponseMessage("Password not provided");
            response.setStatus("password");
            return new ResponseEntity(response,HttpStatus.BAD_REQUEST);
        }else if(user.username.isEmpty() && user.password.isEmpty()){
            response.setResponseMessage("Password and username are not provided");
            response.setStatus("passwordandusername");
            return new ResponseEntity(response,HttpStatus.BAD_REQUEST);
        }
        if(user.username.equals("root") && user.password.equals("Root123$")){
            response.setResponseMessage("Login successful");
            response.setStatus("Success");
            return new ResponseEntity(response,HttpStatus.OK);

        } else {
            response.setResponseMessage("Invalid Credentials provided");
            response.setStatus("Request Rejected");
            return new ResponseEntity(response,HttpStatus.BAD_REQUEST);


        }
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("addEmployee")
    public ResponseEntity addEmployeeDetails(@RequestBody EmployeeEntity employeeEntity){

        AddEmployeeResponse employeeResponse = new AddEmployeeResponse();
        if(employeeEntity.getAge()>99 || employeeEntity.getAge()<18){
            employeeResponse.setResponseMessage("Provide a valid age");
            employeeResponse.setStatus("age");
            return new ResponseEntity(employeeResponse,HttpStatus.BAD_REQUEST);

        }

        String[] emailValidity=employeeEntity.getEmail().split("@");



        //name,email,hiredate
        if(employeeEntity.getName().isEmpty()){
            employeeResponse.setResponseMessage("Check name");
            employeeResponse.setStatus("name");
            return new ResponseEntity(employeeResponse,HttpStatus.BAD_REQUEST);
        }else if(employeeEntity.getEmail().isEmpty()){
            employeeResponse.setResponseMessage("Check email");
            employeeResponse.setStatus("email");
            return new ResponseEntity(employeeResponse,HttpStatus.BAD_REQUEST);
        }else if( employeeEntity.getHireDate()==null){
            employeeResponse.setResponseMessage("Check hiredate");
            employeeResponse.setStatus("hiredate");
            return new ResponseEntity(employeeResponse,HttpStatus.BAD_REQUEST);
        }else if(employeeEntity.getEmail().charAt(0)=='@'|| employeeEntity.getEmail().charAt(0)==' '
        || isDigit(employeeEntity.getEmail().charAt(0))){
            employeeResponse.setResponseMessage("Invalid email");
            employeeResponse.setStatus("email");
            return new ResponseEntity(employeeResponse,HttpStatus.BAD_REQUEST);
        }
        String emailLowercase=employeeEntity.getEmail().toLowerCase();
        System.out.println(emailLowercase);
        employeeEntity.setEmail(emailLowercase);

        List<EmployeeEntity> Employees= repo.findAll();

        boolean emailexist=false;
        int emailfoundid=0;
        for(int i=0;i<Employees.size();i++){
            if(employeeEntity.getEmail().equals(Employees.get(i).getEmail())){
                emailexist=true;
                emailfoundid=Employees.get(i).getId();
                break;
            }
        }


        if(!emailexist) {

            EmployeeEntity empEntity = new EmployeeEntity();

            empEntity = repo.save(employeeEntity);

            employeeResponse.setEmployeeId(empEntity.getId());
            employeeResponse.setResponseMessage("Employee created successfully");

            employeeResponse.setStatus("Success");

            HttpHeaders headers = new HttpHeaders();

            headers.add("Status", "Success");
            return new ResponseEntity(employeeResponse, headers, HttpStatus.CREATED);
        }else {
            employeeResponse.setEmployeeId(emailfoundid);
            employeeResponse.setResponseMessage("Employee email already exists");

            employeeResponse.setStatus("Request Rejected");

            HttpHeaders headers = new HttpHeaders();
            return new ResponseEntity(employeeResponse, headers, HttpStatus.BAD_REQUEST);
        }

    }

    @CrossOrigin(origins = "http://localhost:8080")
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

    @PostMapping("employees/employeeByHireDate")
    public ResponseEntity getEmployeesByHireDate(@RequestBody DateFilter dateFilter){
        EmployeeResponse employeeResponse = new EmployeeResponse();
        logger.info(String.valueOf(dateFilter.getStartDate()));
        java.sql.Timestamp ts1=new Timestamp(dateFilter.getStartDate().getTime());

        java.sql.Timestamp ts2=null;
        if(dateFilter.getEndDate()!=null) {
            ts2 = new Timestamp(dateFilter.getEndDate().getTime());
        }
        String condition = dateFilter.getCondition();


        System.out.println(ts1);
        System.out.println(ts2);
        logger.info("date1 : "+ts1);
        logger.info("date2 : "+ts2);
        logger.info("condition : "+ condition);



        List<EmployeeEntity> employeeByHireDate = new ArrayList<>();
        if(condition.equalsIgnoreCase("Between")){

            logger.info("Searching for Employees Hired between");
            if(ts1 != null && ts2 != null && ts1.before(ts2)){

                employeeByHireDate = repo.findByHireDateBetween(ts1, ts2);

            } else if (ts1 != null && ts2 != null && ts2.before(ts1)) {

                employeeByHireDate = repo.findByHireDateBetween(ts2, ts1);
            }
            if(employeeByHireDate.size()>0){
                return new ResponseEntity<>(employeeByHireDate,HttpStatus.OK);
            }else{
                employeeResponse.setResponseMessage("No Records were found for HireDate "+condition+" "+ts1+" and "
                        +ts2);
                return new ResponseEntity(employeeResponse,HttpStatus.BAD_REQUEST);
            }
        }else if(condition.equalsIgnoreCase("Less than")){
            if(ts1 != null)

                employeeByHireDate = repo.findByHireDateLessThan(ts1);

        } else if(condition.equalsIgnoreCase("Less than equal")){
            if(ts1 != null)

                employeeByHireDate = repo.findByHireDateLessThanEqual(ts1);

        } else if(condition.equalsIgnoreCase("Greater than equal")){
            if(ts1 != null)

                employeeByHireDate = repo.findByHireDateGreaterThanEqual(ts1);

        } else if(condition.equalsIgnoreCase("Greater than")){
            if(ts1 != null)

                employeeByHireDate = repo.findByHireDateGreaterThan(ts1);

        }else if (condition.equalsIgnoreCase("Equals")) {
            if (ts1 != null)

                employeeByHireDate = repo.findByHireDateEquals(ts1);

        }

        if(employeeByHireDate.size() > 0){

            return new ResponseEntity(employeeByHireDate,HttpStatus.OK);

        } else{
            employeeResponse.setResponseMessage("No records were found for hiredate "+condition +" " +ts1);

            return new ResponseEntity(employeeResponse,HttpStatus.BAD_REQUEST);
        }


    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("employees/department")
    public ResponseEntity getEmployeeDetailsByDepartment(@RequestParam(value = "departmentName") String department){

        List<EmployeeEntity> employeeByDepartment = repo.findByDepartment(department);
        EmployeeResponse employeeResponse=new EmployeeResponse();
        if(employeeByDepartment.size() > 0){
            logger.info("Matching employee records available for the department: " + department);

            return new ResponseEntity(employeeByDepartment,HttpStatus.OK);
        } else {
            employeeResponse.setResponseMessage("Failed to find matching employee for the department: "+department);
            logger.info("Failed to find matching employee for the department: " + department);

            return new ResponseEntity(employeeResponse,HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("employees/name")
    public  ResponseEntity getEmployeeDetailsByName(@RequestParam(value = "name") String name,
                                                          @RequestParam(value = "exactMatch") boolean exactMatch){

        EmployeeResponse employeeResponse=new EmployeeResponse();
        List<EmployeeEntity> employeeByName = new ArrayList<>();
        if(exactMatch){

            List<EmployeeEntity> employees = repo.findAll();

            for(int i=0; i<employees.size(); i++){

                if(employees.get(i).getName().equals(name))
                    employeeByName.add(employees.get(i));
            }

        } else {

            employeeByName = repo.findByNameContains(name);
        }

        if(employeeByName.size() > 0){

            logger.info("Matching employee records available for the name: " + name);
            return new ResponseEntity(employeeByName,HttpStatus.OK);
        } else {
            logger.info("Failed to get employee records for the name: " + name);
            employeeResponse.setResponseMessage("Failed to get employee records for the name: " + name);
            return new ResponseEntity(employeeResponse,HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("employees/city")
    public  ResponseEntity getEmployeeDetailsByCity(@RequestParam(value = "city") String city,
                                                          @RequestParam(value = "exactMatch") boolean exactMatch) {


        EmployeeResponse employeeResponse=new EmployeeResponse();
        List<EmployeeEntity> employeeByCity = new ArrayList<>();
        if (exactMatch) {

            List<EmployeeEntity> employees = repo.findAll();

            for(int i=0; i<employees.size(); i++){

                if(employees.get(i).getCity().equals(city))
                    employeeByCity.add(employees.get(i));
            }

        } else {

            employeeByCity = repo.findByCityContains(city);
        }

        if(employeeByCity.size() > 0) {

            logger.info("Matching employee records available for the city: " + city);
            return new ResponseEntity(employeeByCity,HttpStatus.OK);
        } else {
            employeeResponse.setResponseMessage("Failed to get employee records for the city: " + city);
            logger.info("Failed to get employee records for the city: " + city);
            return new ResponseEntity(employeeResponse,HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("employees/employeeCount")
    public ResponseEntity getMaxEmployeeId() {
        EmployeeResponse employeeResponse=new EmployeeResponse();
        employeeResponse.setResponseMessage("Total count of employees: " + repo.count());
        return new ResponseEntity( employeeResponse, HttpStatus.OK);


    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("employees/getAllEmployees")
    public ResponseEntity getAllEmployees() {

        return new ResponseEntity(repo.findAll(), HttpStatus.OK);
    }


    @GetMapping("employees/employeeCountByDepartment")
    public ResponseEntity getEmployeeCountByDepartment(@RequestParam (value = "department") String department) {
        EmployeeResponse employeeResponse=new EmployeeResponse();
        if(repo.countByDepartmentEquals(department) > 0){
            employeeResponse.setResponseMessage("Total count of employee : " +  + repo.countByDepartmentEquals(department) +
                    " for department : " + department);
            return new ResponseEntity(employeeResponse, HttpStatus.OK);

        } else {
            employeeResponse.setResponseMessage("Department not found");
            return new ResponseEntity(employeeResponse,HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("employees/employeeCountByCity")
    public ResponseEntity getMaxCountOfCity(@RequestParam (value = "city") String city,
                                                    @RequestParam (value = "exactMatch") boolean exactMatch) {
        EmployeeResponse employeeResponse=new EmployeeResponse();

        long countOfCity = 0;

        if(exactMatch){
            List<EmployeeEntity> employee = repo.findAll();

            for(int i=0; i < employee.size(); i++) {
                if (employee.get(i).getCity().equals(city))
                    countOfCity++;
            }
        } else {
            countOfCity = repo.countByCityContains(city);
        }

        if(countOfCity > 0) {
            employeeResponse.setResponseMessage("Total count of employee for city " + city + " : "
                    + countOfCity);
            return new ResponseEntity(employeeResponse, HttpStatus.OK);
        }else {
            employeeResponse.setResponseMessage("No employee found for city " +  city);
            return new ResponseEntity(employeeResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("employees/employeeCountByName")
    public ResponseEntity getCountOfEmployeeByName(@RequestParam (value = "name") String name) {

         EmployeeResponse employeeResponse=new EmployeeResponse();

        if(repo.countByNameEquals(name) > 0){
            employeeResponse.setResponseMessage("Total count of employee for name " + name + " : "
                   + repo.countByNameEquals(name));
            return new ResponseEntity(employeeResponse, HttpStatus.OK);

        } else {
            employeeResponse.setResponseMessage("No employee found for name " +  name);
            return new ResponseEntity(employeeResponse,HttpStatus.BAD_REQUEST);
        }


    }





    @PostMapping("employees/countEmployeeByHireDate")
    public ResponseEntity countEmployeeByHireDate(@RequestBody DateFilter dateFilter){
        EmployeeResponse employeeResponse=new EmployeeResponse();
        java.sql.Timestamp ts1=new Timestamp(dateFilter.getStartDate().getTime());
        Date d1=dateFilter.getStartDate();
//        System.out.println(d1.getMonth());
        java.sql.Timestamp ts2=null;
        if(dateFilter.getEndDate()!=null) {
            ts2 = new Timestamp(dateFilter.getEndDate().getTime());
        }
        String condition = dateFilter.getCondition();


        System.out.println(ts1);
        Date newdate2=new Date(ts1.getTime());
        System.out.println(newdate2 );


        logger.info("date1 : "+ts1);
        logger.info("date2 : "+ts2);
        logger.info("condition : "+ condition);




        long countemployeeByHireDate = 0;
        if(condition.equalsIgnoreCase("Between")){

            logger.info("Searching for count of Employees Hired between");

            if(ts1 != null && ts2 != null && ts1.before(ts2)){

                countemployeeByHireDate = repo.countByHireDateBetween(ts1, ts2);

            } else if (ts1 != null && ts2 != null && ts2.before(ts1)) {

                countemployeeByHireDate = repo.countByHireDateBetween(ts2, ts1);
            }
            if(countemployeeByHireDate>0){
                employeeResponse.setResponseMessage("Count for the Hiredate :"+condition+" "+
                        ts1+" and "+ts2+" is "+countemployeeByHireDate);
                return new ResponseEntity(employeeResponse,HttpStatus.OK);
            }else{
                employeeResponse.setResponseMessage("Count for the Hiredate :"+condition+" "+
                        ts1+" and "+ts2+" is "+countemployeeByHireDate);
                return new ResponseEntity(employeeResponse,HttpStatus.BAD_REQUEST);
            }
        }else if(condition.equalsIgnoreCase("Less than")){

            countemployeeByHireDate=repo.countByHireDateLessThan(ts1);

        } else if(condition.equalsIgnoreCase("Less than equal")){
            countemployeeByHireDate=repo.countByHireDateLessThanEqual(ts1);

        } else if(condition.equalsIgnoreCase("Greater than equal")){
            countemployeeByHireDate=repo.countByHireDateGreaterThanEqual(ts1);


        } else if(condition.equalsIgnoreCase("Greater than")){
            countemployeeByHireDate= repo.countByHireDateGreaterThan(ts1);

        }else if (condition.equalsIgnoreCase("Equal")) {

            countemployeeByHireDate=repo.countByHireDateEquals(ts1);
        }
        if(countemployeeByHireDate > 0){
            employeeResponse.setResponseMessage("Count for the Hiredate :"+condition+" "+
                    ts1+" is "+countemployeeByHireDate);
            return new ResponseEntity(employeeResponse,HttpStatus.OK);

        } else{
            employeeResponse.setResponseMessage("Count for the Hiredate :"+condition+" "+
                    ts1+" is "+countemployeeByHireDate);
            return new ResponseEntity(employeeResponse,HttpStatus.BAD_REQUEST);
        }



    }


    @DeleteMapping("employees/deleteEmployeeById/{id}")
    public ResponseEntity<String> deleteEmployeebyid(@PathVariable("id") int id) {
        GetEmployeeResponse employeeResponse=new GetEmployeeResponse();
        List<EmployeeEntity> employees = repo.findAll();
        boolean employeeid=false;
        if(id<1000){
            employeeResponse.setResponseMessage("Invalid Id Provided");

            employeeResponse.setEmployeeId(id);
            return new ResponseEntity(employeeResponse, HttpStatus.BAD_REQUEST);
        }
        for(int i=0; i<employees.size(); i++){

            if(employees.get(i).getId()==id) {
                employeeid=true;
                break;
            }
        }
        if(employeeid){



            repo.deleteById(id);

            employeeResponse.setEmployeeId(id);

            employeeResponse.setResponseMessage("Employee deleted successfully");


            HttpHeaders headers = new HttpHeaders();

            headers.add("Status", "Success");

            return new ResponseEntity(employeeResponse, headers, HttpStatus.OK);
        } else {

            logger.info("Employee does not exists!!!");

            employeeResponse.setResponseMessage("Employee does not exists");
            employeeResponse.setEmployeeId(id);


            return new ResponseEntity(employeeResponse, HttpStatus.BAD_REQUEST);
        }

    }


    @DeleteMapping("employees/deleteEmployeeByName")
    public ResponseEntity<String> deleteEmployeebyName(@RequestParam("name") String name) {
        GetEmployeeResponse employeeResponse=new GetEmployeeResponse();
        EmployeeResponse employeeResponse1=new EmployeeResponse();
        List<EmployeeEntity> employees = repo.findAll();
        boolean employeename=false;
        int id=0;
        for(int i=0; i<employees.size(); i++){

            if(employees.get(i).getName().equals(name)) {
                employeename=true;
                id=employees.get(i).getId();
                break;
            }
        }
        if(employeename){



            repo.deleteById(id);

            employeeResponse.setEmployeeId(id);

            employeeResponse.setResponseMessage("Employee deleted successfully");


            HttpHeaders headers = new HttpHeaders();

            headers.add("Status", "Success");

            return new ResponseEntity(employeeResponse, headers, HttpStatus.OK);
        } else {

            logger.info("Employee does not exists with name : "+name);
            employeeResponse1.setResponseMessage("Employee does not exists with name : "+name);
            return new ResponseEntity(employeeResponse1, HttpStatus.BAD_REQUEST);
        }

    }



    @PutMapping("employees/updateById/{id}")
    public ResponseEntity updateEmployeebyid(@PathVariable("id") int id, @RequestBody EmployeeEntity employee) {
        GetEmployeeResponse EmployeeResponse = new GetEmployeeResponse();

        String emailLowercase=employee.getEmail().toLowerCase();

        employee.setEmail(emailLowercase);


        if(id<1000){
            EmployeeResponse.setResponseMessage("Invalid Id Provided");

            EmployeeResponse.setEmployeeId(id);

            return new ResponseEntity(EmployeeResponse, HttpStatus.BAD_REQUEST);
        }
        List<EmployeeEntity> employeeList=repo.findAll();
        boolean emailExist=false;
        int emailfoundid=0;
        for(int i=0;i<employeeList.size();i++){
            if(employee.getEmail().equals(employeeList.get(i).getEmail()) && employeeList.get(i).getId()!=id){
                emailExist=true;
                break;
            }
        }

        if(emailExist){
            EmployeeResponse.setResponseMessage("Email already exists");
            EmployeeResponse.setEmployeeId(id);
            return new ResponseEntity(EmployeeResponse,HttpStatus.BAD_REQUEST);
        }
        try{
            logger.info("Updating the Employee details for the Employee id: " + id);

            EmployeeEntity updateemployee=repo.findById(id).get();

            updateemployee.setName(employee.getName());
            updateemployee.setDepartment(employee.getDepartment());
            updateemployee.setHireDate(employee.getHireDate());
            updateemployee.setAge(employee.getAge());
            updateemployee.setSex(employee.getSex());
            updateemployee.setCity(employee.getCity());
            updateemployee.setPhone(employee.getPhone());
            updateemployee.setEmail(employee.getEmail());
            updateemployee.setId(id);

            repo.save(updateemployee);
            logger.info(String.valueOf(updateemployee.getId()));
            EmployeeResponse.setResponseMessage("Employee details updated successfully.");

            EmployeeResponse.setEmployeeId(id);
            logger.info("Employee Response Id : "+ EmployeeResponse.getEmployeeId());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Status", "Success");
            return new ResponseEntity(EmployeeResponse,headers, HttpStatus.OK);


        }catch (NoSuchElementException exception) {

            logger.info("Employee details not found for the employee id: " + id);
            EmployeeResponse.setResponseMessage("Employee details not found for the employee id: " + id);
            EmployeeResponse.setEmployeeId(id);
            return new ResponseEntity(EmployeeResponse, HttpStatus.BAD_REQUEST);

        }


    }


    @PutMapping("employees/updateByName")
    public ResponseEntity updateEmployeeByName(@RequestParam(value = "name") String name,
                                                       @RequestBody EmployeeEntity employee) {
        String emailLowercase=employee.getEmail().toLowerCase();

        employee.setEmail(emailLowercase);

        GetEmployeeResponse EmployeeResponse=new GetEmployeeResponse();
        boolean employeenamefound=false;
        EmployeeResponse employeeResponse=new EmployeeResponse();
            logger.info("Updating the employee details for the employee name: " + name);


        List<EmployeeEntity> employeeList=repo.findAll();
        boolean emailExist=false;
        int emailfoundid=0;



                int i;


                for(i=0; i<employeeList.size(); i++){

                    if(employeeList.get(i).getName().equals(name)) {

                        int k=employeeList.get(i).getId();
                        for(int j=0;j<employeeList.size();j++){
                            if(employee.getEmail().equals(employeeList.get(j).getEmail()) && employeeList.get(j).getId()!= k){
                                emailExist=true;

                                break;
                            }
                        }
                        if(emailExist){
                            employeeResponse.setResponseMessage("Email already exists");

                            return new ResponseEntity(employeeResponse,HttpStatus.BAD_REQUEST);
                        }
                        employeeList.get(i).setName(employee.getName());
                        employeeList.get(i).setDepartment(employee.getDepartment());
                        employeeList.get(i).setHireDate(employee.getHireDate());
                        employeeList.get(i).setAge(employee.getAge());
                        employeeList.get(i).setSex(employee.getSex());
                        employeeList.get(i).setCity(employee.getCity());
                        employeeList.get(i).setPhone(employee.getPhone());
                        employeeList.get(i).setEmail(employee.getEmail());
                        repo.save(employeeList.get(i));
                        employeenamefound=true;
                        break;
                    }
                }
            if(employeenamefound) {
                EmployeeResponse.setResponseMessage("Updated employee details successfully of name:" + name);
                EmployeeResponse.setEmployeeId(employeeList.get(i).getId());
                HttpHeaders headers = new HttpHeaders();
                headers.add("Status", "Success");
                return new ResponseEntity(EmployeeResponse, headers, HttpStatus.OK);
            }else{
                employeeResponse.setResponseMessage("Employee details not found for the name: " + name);
                logger.info("Employee details not found for the name: " + name);
                return new ResponseEntity(employeeResponse, HttpStatus.BAD_REQUEST);
            }





    }

}




