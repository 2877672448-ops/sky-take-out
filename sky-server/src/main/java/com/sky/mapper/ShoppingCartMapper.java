package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    List<ShoppingCart> list(ShoppingCart shoppingCart);
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    @Insert("insert into shopping_cart (user_id, dish_id, setmeal_id, name, image, amount, dish_flavor, number, create_time) " +
            "values (#{userId}, #{dishId}, #{setmealId}, #{name}, #{image}, #{amount}, #{dishFlavor}, #{number}, #{createTime})")

    void insert(ShoppingCart shoppingCart);

}
