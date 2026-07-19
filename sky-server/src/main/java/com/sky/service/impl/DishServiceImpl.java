package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    private void cleanCache(String... keys) {
        Set<String> dishKeys = redisTemplate.keys("dish_*");
        if (dishKeys != null && dishKeys.size() > 0) {
            redisTemplate.delete(dishKeys);
        }
    }

    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
        log.info("菜品插入成功，生成的id={}", dish.getId());

        List<DishFlavor> flavors = dishDTO.getFlavors();
        log.info("口味数据：flavors={}, size={}", flavors, flavors == null ? "null" : flavors.size());
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());
                log.info("设置口味dishId={}, name={}, value={}", dishFlavor.getDishId(), dishFlavor.getName(), dishFlavor.getValue());
            });
            dishFlavorMapper.insertBatch(flavors);
            log.info("口味批量插入完成");
        } else {
            log.warn("口味数据为空，未执行插入！");
        }

        cleanCache();
    }

    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Transactional
    public void deleteBatch(String ids) {
        List<Long> dishIds = List.of(ids.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());

        List<Dish> dishes = dishMapper.getByIds(dishIds.stream().map(String::valueOf).collect(Collectors.joining(",")));

        List<Dish> onSaleDishes = dishes.stream()
                .filter(dish -> dish.getStatus() == 1)
                .collect(Collectors.toList());
        if (onSaleDishes != null && onSaleDishes.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }

        Integer count = setmealDishMapper.countByDishIds(String.join(",", dishIds.stream().map(String::valueOf).collect(Collectors.toList())));
        if (count > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        dishMapper.deleteByIds(dishIds);
        dishFlavorMapper.deleteByDishIds(dishIds);

        cleanCache();
    }

    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }

        cleanCache();
    }

    public List<Dish> listByCategoryId(Long categoryId) {
        return dishMapper.listByCategoryId(categoryId);
    }

    public List<DishVO> listWithFlavor(Dish dish) {
        String key = "dish_" + dish.getCategoryId() + "_" + (dish.getName() != null ? dish.getName() : "") + "_" + (dish.getStatus() != null ? dish.getStatus() : "");

        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (list != null && list.size() > 0) {
            log.info("从缓存中获取菜品数据，key={}", key);
            return list;
        }

        log.info("缓存未命中，查询数据库，key={}", key);
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);

            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());
            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        redisTemplate.opsForValue().set(key, dishVOList);
        log.info("菜品数据已缓存，key={}", key);

        return dishVOList;
    }

    @Override
    public void statusHandle(Integer status, Long id) {
        dishMapper.updateStatus(status, id);
        cleanCache();
    }

}
