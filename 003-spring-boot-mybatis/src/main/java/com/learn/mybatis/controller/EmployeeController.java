package com.learn.mybatis.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.learn.mybatis.entity.Department;
import com.learn.mybatis.entity.Employee;
import com.learn.mybatis.mapper.EmployeeMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    EmployeeMapper employeeMapper;

    /*    @PathVariable("xxx") @PathVariable是spring3.0的一个新功能：接收请求路径中占位符的值
        通过 @PathVariable 可以将URL中占位符参数{xxx}绑定到处理器类的方法形参中@PathVariable(“xxx“)
        @RequestMapping(value=”user/{id}/{name}”)
        请求路径：http://localhost:8080/employee/getEmployeeById/1
        */
    @RequestMapping(value = "/getEmployeeById/{id}", method = RequestMethod.GET)
    public Employee getEmployeeById(@PathVariable("id") Integer id) {
        System.out.println(id);
        return employeeMapper.findOneById(id);
    }

    /**
     * 查询内联标签测试
     * http://localhost:8080/employee/findEmpAndDept/1
     */
    @RequestMapping(value = "/findEmpAndDept/{id}", method = RequestMethod.GET)
    public Employee findEmpAndDept(@PathVariable("id") Integer id) {
        return employeeMapper.findEmpAndDept(id);
    }

    /**
     * 查询内联标签测试通过Association
     * http://localhost:8080/employee/findEmpAndDeptByAssociation/1
     */
    @RequestMapping(value = "/findEmpAndDeptByAssociation/{id}", method = RequestMethod.GET)
    public Employee findEmpAndDeptByAssociation(@PathVariable("id") Integer id) {
        return employeeMapper.findEmpAndDeptByAssociation(id);
    }

    /**
     * 分步查询员工信息
     * http://localhost:8080/employee/findEmpByIdStep/1
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/findEmpByIdStep/{id}", method = RequestMethod.GET)
    public Employee findEmpByIdStep(@PathVariable("id") Integer id) {
        return employeeMapper.findEmpByIdStep(id);
    }

    /**
     * 查询返回map的数据
     * http://localhost:8080/employee/findEmployeeForMap/1
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/findEmployeeForMap/{id}", method = RequestMethod.GET)
    public Map<String, Object> findEmployeeForMap(@PathVariable("id") Integer id) {
        return employeeMapper.findEmployeeForMap(id);
    }

    /**
     * 查询返回list的数据
     * http://localhost:8080/employee/findEmployeeForMap/1
     *
     * @return
     */
    @RequestMapping(value = "/findEmployeeByMapList/{lastName}", method = RequestMethod.GET)
    public List<Employee> findEmployeeByMapList(@PathVariable("lastName") String lastName) {
        return employeeMapper.findEmployeeByList("%" + lastName + "%");
    }

    /**
     * 查询返回map的数据
     * http://localhost:8080/employee/findEmployeeForMap/1
     *
     * @return
     */
    @RequestMapping(value = "/findEmployeeByListMap", method = RequestMethod.GET)
    public List<Employee> findEmployeeByListMap() {
        return employeeMapper.findEmployeeByListMap();
    }


    /**
     * 测试动态SQL if标签
     *
     * @PathVariable("id") Integer id, @PathVariable("lastName") String lastName, @PathVariable("email") String email, @PathVariable("gender") Integer gender
     * http://localhost:8080/employee/getEmployeeIf
     */
    @RequestMapping(value = "/getEmployeeIf", method = RequestMethod.GET)
    public List<Employee> getEmployeeIf() {
        Employee employee = new Employee(1, "战三", "aaa", 1);
        return employeeMapper.getEmployeeIf(employee);
    }

    /**
     * 测试动态SQL where标签
     *
     * @PathVariable("id") Integer id, @PathVariable("lastName") String lastName, @PathVariable("email") String email, @PathVariable("gender") Integer gender
     * http://localhost:8080/employee/getEmployeeWhere
     */
    @RequestMapping(value = "/getEmployeeWhere", method = RequestMethod.GET)
    public List<Employee> getEmployeeWhere() {
        Employee employee = new Employee(1, "战三", "aaa", 1);
        return employeeMapper.getEmployeeWhere(employee);
    }

    /**
     * 测试动态SQL where标签
     *
     * @PathVariable("id") Integer id, @PathVariable("lastName") String lastName, @PathVariable("email") String email, @PathVariable("gender") Integer gender
     * http://localhost:8080/employee/getEmployeeTrim
     */
    @RequestMapping(value = "/getEmployeeTrim", method = RequestMethod.GET)
    public List<Employee> getEmployeeTrim() {
        Employee employee = new Employee(1, "战三", "aaa", 1);
        return employeeMapper.getEmployeeTrim(employee);
    }

    /**
     * 测试动态SQL where标签
     *
     * @PathVariable("id") Integer id, @PathVariable("lastName") String lastName, @PathVariable("email") String email, @PathVariable("gender") Integer gender
     * http://localhost:8080/employee/getEmployeeWhere
     */
    @RequestMapping(value = "/getEmployeeChoose", method = RequestMethod.GET)
    public List<Employee> getEmployeeChoose() {
        Employee employee = new Employee(1, "战三", "aaa", 1);
        return employeeMapper.getEmployeeChoose(employee);
    }

    /**
     * 测试动态SQL set标签
     *
     * @PathVariable("id") Integer id, @PathVariable("lastName") String lastName, @PathVariable("email") String email, @PathVariable("gender") Integer gender
     * http://localhost:8080/employee/updateEmployeeSet
     */
    @RequestMapping(value = "/updateEmployeeSet", method = RequestMethod.GET)
    public void updateEmployeeSet() {
        Employee employee = new Employee(1, "战三", "ccc", 1);
        employeeMapper.updateEmployeeSet(employee);
    }

    /**
     * 测试动态SQL foreach标签 in 查询
     * http://localhost:8080/employee/getEmployeeForeach
     */
    @RequestMapping(value = "/getEmployeeForeach", method = RequestMethod.GET)
    public List<Employee> getEmployeeForeach() {
        return employeeMapper.getEmployeeForeach(Arrays.asList(1, 2, 3));
    }

    /**
     * 测试动态SQL foreach标签 批量插入
     * http://localhost:8080/employee/addEmployees
     */
    @RequestMapping(value = "/addEmployees", method = RequestMethod.GET)
    public void addEmployees() {
        Employee employee1 = new Employee(4, "hh4", "ll4", 1, new Department(1));
        Employee employee2 = new Employee(5, "hh5", "ll5", 1, new Department(2));
        Employee employee3 = new Employee(6, "hh6", "ll6", 1, new Department(3));
        List<Employee> employees = new ArrayList<>();
        employees.add(employee1);
        employees.add(employee2);
        employees.add(employee3);
        employeeMapper.addEmployees(employees);
    }

    /**
     * 测试使用内置动态标签
     * http://localhost:8080/employee/getEmployeeByIdInnerParameter
     */
    @RequestMapping(value = "/getEmployeeByIdInnerParameter", method = RequestMethod.GET)
    public List<Employee> getEmployeeByIdInnerParameter() {
        Employee employee = new Employee(1, "战三", "ccc", 1);
        return employeeMapper.getEmployeeByIdInnerParameter(employee);
    }

    /**
     * 测试使用bind标签
     * http://localhost:8080/employee/findAndCountBind/h
     */
    @RequestMapping(value = "/findAndCountBind/{lastName}", method = RequestMethod.GET)
    public List<Employee> findAndCountBind(@PathVariable("lastName") String lastName) {
        return employeeMapper.findAndCountBind(lastName);
    }

    /**
     * 测试Mybatis流程
     * http://localhost:8080/employee
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public void findAndCountBind() throws Exception {
        String config = "mybatis/config.xml";
        InputStream inputStream = Resources.getResourceAsStream(config);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        // 获取一个sql session实例，能够获取一个已经映射的sql语句
        SqlSession sqlSession = sqlSessionFactory.openSession();
        EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
        Employee employee = mapper.findOneById(1);
        employee = sqlSession.selectOne("EmployeeMapper.findOneById", 1);

        System.out.println(employee.toString());
        sqlSession.commit();
        sqlSession.close();
    }

    /**
     * http://localhost:8080/employee/findAllByPage
     *
     * @return
     */
    @RequestMapping(value = "/findAllByPage", method = RequestMethod.GET)
    public List<Employee> findAllByPage() {
        Page<Object> page = PageHelper.startPage(1, 3);
        List<Employee> result = employeeMapper.findAllByPage();
        System.out.println("当前页码：" + page.getPageNum());
        System.out.println("总记录数：" + page.getTotal());
        System.out.println("每页的记录数：" + page.getPageSize());
        System.out.println("总页码：" + page.getPages());
        PageInfo<Employee> pageInfo = new PageInfo<>(result);
        System.out.println("是否是第一页:" + pageInfo.isIsFirstPage());
        System.out.println("是否是最后一页:" + pageInfo.isIsLastPage());
        return result;
    }

}
