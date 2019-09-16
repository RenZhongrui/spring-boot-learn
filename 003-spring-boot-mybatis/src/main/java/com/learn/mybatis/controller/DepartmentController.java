package com.learn.mybatis.controller;

import com.learn.mybatis.entity.Department;
import com.learn.mybatis.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dept")
public class DepartmentController {

    @Autowired
    DepartmentMapper departmentMapper;

    // http://localhost:8080/dept/1
    @GetMapping("/dept/{id}")
    public Department findDepartment(@PathVariable("id") Integer id) {
        return departmentMapper.findOne(id);
    }

    // http://localhost:8080/dept?departmentName=CC
    @GetMapping("/dept")
    public Department addDepartment(Department department) {
        departmentMapper.addOne(department);
        return department;
    }

    /**
     * http://localhost:8080/dept/findOneById/1
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/findOneById/{id}", method = RequestMethod.GET)
    public Department findOneById(@PathVariable("id") Integer id) {
        return departmentMapper.findOneById(id);
    }

    /**
     * 通过集合查询, 因为一个部门对应很多员工，所以需要使用集合
     * http://localhost:8080/dept/findOneByIdWithEmp/1
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/findOneByIdWithEmp/{id}", method = RequestMethod.GET)
    public Department findOneByIdWithEmp(@PathVariable("id") Integer id) {
        return departmentMapper.findOneByIdWithEmp(id);
    }

}
