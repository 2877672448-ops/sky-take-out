package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO 员工登录请求参数
     * @return 登录成功的员工信息
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    void save(EmployeeDTO employeeDTO);

    PageResult page(EmployeePageQueryDTO employeePageQueryDTO);

    void startOrStop(Integer status, Long id);

    Employee getById(Long id);



    void update(EmployeeDTO employeeDTO);
}
