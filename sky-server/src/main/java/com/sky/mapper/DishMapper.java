package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {

    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void deleteByIds(List<Long> ids);

    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    @Select("select * from dish where category_id = #{categoryId} and status = 1")
    List<Dish> listByCategoryId(Long categoryId);

    @Update("update dish set status = #{status} where id = #{id}")
    void updateStatus(Integer status, Long id);

    @Select("select * from dish where id in (${ids})")
    List<Dish> getByIds(String ids);

    List<Dish> list(Dish dish);
}
