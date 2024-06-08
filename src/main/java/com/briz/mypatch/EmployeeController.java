package com.briz.mypatch;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class EmployeeController 
{
@Autowired
EmployeeRepository erepo;

// note use requestmapping into the coding and change the method only in postman it works fine 
@RequestMapping("/test")
public String test()
{
	return "this is patch testing";
}
@RequestMapping("/save")// without any id request data is sent here 
public String save(@RequestBody Employee employee)
{
	erepo.save(employee);
	return "data saved into database";
}
@RequestMapping("/all")
public List<Employee> alldata()
{
	return erepo.findAll();
}
@RequestMapping("/{id}")
public Optional<Employee> byid(@PathVariable int id)
{
	return erepo.findById(id);
}
@PutMapping("/update")// update with matching data otherwise insert(upsert example)
// id is also sent with the request data if it matches then it will update(all fields must be present otherwise null) 
//if id not present then it will insert 
public Employee dataupdate(@RequestBody Employee employee)
{
	return erepo.save(employee);	
}
/*
@PutMapping("/by/{id}")// update with matching id otherwise gives exception 
// all fields with valid is sent to the request data 
public Employee update(@PathVariable int id,@RequestBody Employee employee)// all fields with valid id 
{
Employee emp=erepo.findById(id).get();
emp.setAge(employee.getAge());
emp.setCity(employee.getCity());
emp.setName(employee.getName());
erepo.save(emp);// note save emp here employee is coming as request data 
return emp;	// all fields are must otherwise null value will be present 
}
*/
/*
@PatchMapping("/patch/{id}")// this is patch mapping working fine in postman 
public Employee partialupd(@PathVariable int id,@RequestBody Employee employee)
{//  THIS IS WELL WORKING EXAMPLE INTO THE POSTMAN VERY IMPORTANT
	Employee emp=erepo.findById(id).get();
	emp.setName(employee.getName());// changing the name only name must be passed
	emp.setAge(employee.getAge());// changing the age only pass age only
	//for changing both name and age both methods must be used 
	// use only those methods which are updatable 
	// if city is not used into postman it will not change the field note this 
	// we can update as many as but not all fields note this is patch mapping 
return 	erepo.save(emp);// type of emp is Optional 

}
*/
/*
@PatchMapping("/my/patch/{id}")// this is also same as above example do not confuse 
public Employee mypatch(@PathVariable int id,@RequestBody Employee employee)
{
	//  NOTE NAME AND AGE MUST BE PASSED HERE 
	Optional<Employee> emp=erepo.findById(id);
	emp.get().setAge(employee.getAge());
	emp.get().setName(employee.getName());
	return erepo.save(emp.get());
	}
*/
// below is the example of partial updating the data using reflection utils class 
@PatchMapping("/partial/{id}")// for partial updating the data if all fields are updated then will cause error 
public void partialupd(@PathVariable int id,@RequestBody Map<String,Object> fields)
{// @RequestBody is must with map OK working example 
Optional<Employee>  emp=erepo.findById(id);

fields.forEach((key,value)->
{
	Field field=ReflectionUtils.findField(Employee.class,key);
	field.setAccessible(true);
	ReflectionUtils.setField(field, emp.get(), value);
	erepo.save(emp.get());// must save otherwise data will not be saved into database 
System.out.println("data successfully updated");
});
}
}


