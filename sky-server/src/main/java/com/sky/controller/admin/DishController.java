package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping
    @ApiOperation("新增菜品")
    public Result<String> addDish(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        redisTemplate.delete("dish_" + dishDTO.getCategoryId());
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result<String> deleteDish(@RequestParam String ids) {
        log.info("批量删除菜品：{}", ids);
        dishService.deleteBatch(ids);
        cleanRedis("dish_*");
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("修改菜品")
    public Result<String> updateDish(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        cleanRedis("dish_*");
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> listByCategoryId(@RequestParam Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);
        List<Dish> dishList = dishService.listByCategoryId(categoryId);
        return Result.success(dishList);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result<String> statusHandle(@PathVariable Integer status, @RequestParam Long id) {
        log.info("菜品状态修改：status={}, id={}", status, id);
        dishService.statusHandle(status, id);
        cleanRedis("dish_*");
        return Result.success();
    }


    public void cleanRedis(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
