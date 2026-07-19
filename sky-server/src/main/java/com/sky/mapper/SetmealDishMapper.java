package com.sky.mapper;

import com.sky.entity.SetmealDish;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

    Integer countByDishIds(String dishIds);

    void insertBatch(List<SetmealDish> setmealDishes);

    void deleteBySetmealId(Long setmealId);

    void deleteBySetmealIds(List<Long> setmealIds);
}
