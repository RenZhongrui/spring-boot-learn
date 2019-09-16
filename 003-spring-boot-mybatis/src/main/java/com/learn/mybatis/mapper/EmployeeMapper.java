package com.learn.mybatis.mapper;

import com.learn.mybatis.entity.Employee;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

// @Mapper
public interface EmployeeMapper {

    // 查找一条数据
    Employee findOneById(Integer id);

    // 添加一条数据
    void addOne(Employee employee);

    Employee findEmpAndDept(Integer id);

    Employee findEmpAndDeptByAssociation(Integer id);

    Employee findEmpByIdStep(Integer id);

    Map<String, Object> findEmployeeForMap(Integer id);

    List<Employee> findEmployeeByList(String lastName);

    List<Employee> findEmployeeByListMap();

    // 使用if标签
    List<Employee> getEmployeeIf(Employee employee);

    // 使用where标签
    List<Employee> getEmployeeWhere(Employee employee);

    // 使用trim标签
    List<Employee> getEmployeeTrim(Employee employee);

    // 使用choose标签
    List<Employee> getEmployeeChoose(Employee employee);

    // 使用set标签
    void updateEmployeeSet(Employee employee);

    // 使用foreach标签
    List<Employee> getEmployeeForeach(@Param("ids") List<Integer> ids);

    // 批量插入 @Param 给传过去的参数进行别名
    void addEmployees(@Param("employees") List<Employee> employees);

    // 通过内置参数来执行查询
    List<Employee> getEmployeeByIdInnerParameter(Employee employee);

    // bind标签的使用
    List<Employee> findAndCountBind(String lastName);

    List<Employee> findAllByPage();

}
