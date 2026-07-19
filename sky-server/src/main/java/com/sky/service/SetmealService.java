package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;

import java.util.List;

public interface SetmealService {


    /**
     * 根据套餐id查询包含的菜品列表
     * @param id 套餐id
     * @return 菜品列表
     */
    List<DishItemVO> getDishItemById(Long id);

    /**
     * 新增套餐及关联菜品
     * @param setmealDTO 套餐及菜品数据
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO 分页查询参数
     * @return 分页结果
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量删除套餐
     * @param ids 套餐id列表
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询套餐及关联菜品
     * @param id 套餐id
     * @return 套餐数据
     */
    Setmeal getByIdWithDish(Long id);

    /**
     * 修改套餐及关联菜品
     * @param setmealDTO 套餐及菜品数据
     */
    void updateWithDish(SetmealDTO setmealDTO);

    /**
     * 套餐起售/停售
     * @param status 状态：1起售 0停售
     * @param id 套餐id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 动态条件查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);
}
