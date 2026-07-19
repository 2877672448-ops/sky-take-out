package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品及口味
     * @param dishDTO 菜品及口味数据
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 分页查询参数
     * @return 分页结果
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     * @param ids 菜品id，多个用逗号分隔
     */
    void deleteBatch(String ids);

    /**
     * 根据id查询菜品及口味
     * @param id 菜品id
     * @return 菜品及口味数据
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 修改菜品及口味
     * @param dishDTO 菜品及口味数据
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 根据分类id查询菜品
     * @param categoryId 分类id
     * @return 菜品列表
     */
    List<Dish> listByCategoryId(Long categoryId);
    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);

    /**
     * 菜品起售/停售
     * @param status 状态：1起售 0停售
     * @param id 菜品id
     */
    void statusHandle(Integer status, Long id);

}
