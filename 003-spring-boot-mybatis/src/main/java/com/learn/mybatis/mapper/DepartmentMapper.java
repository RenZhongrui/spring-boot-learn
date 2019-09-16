package com.learn.mybatis.mapper;


import com.learn.mybatis.entity.Department;
import org.apache.ibatis.annotations.*;

// @Mapper
public interface DepartmentMapper {

    @Select("select * from department where id = #{id}")
    Department findOne(Integer id);

    @Delete("delete from department where id = #{id}")
    int deleteOneById(Integer id);

    // 表示使用自增主键，主键属性是id
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into department(departmentName) values(#{departmentName})")
    int addOne(Department departmentName);

    Department findOneById(Integer id);

    Department findOneByIdWithEmp(Integer id);
}
