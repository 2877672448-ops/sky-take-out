package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.categoryId")
    public Result<String> addSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐：{}", setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        cleanRedis("setmealCache::*");
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询：{}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result<String> deleteSetmeal(@RequestParam String ids) {
        log.info("批量删除套餐：{}", ids);
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        setmealService.deleteBatch(idList);
        cleanRedis("setmealCache::*");
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<Setmeal> getById(@PathVariable Long id) {
        log.info("根据id查询套餐：{}", id);
        Setmeal setmeal = setmealService.getByIdWithDish(id);
        return Result.success(setmeal);
    }

    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result<String> updateSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐：{}", setmealDTO);
        setmealService.updateWithDish(setmealDTO);
        cleanRedis("setmealCache::*");
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售停售")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result<String> startOrStop(@PathVariable Integer status, @RequestParam Long id) {
        log.info("套餐状态修改：status={}, id={}", status, id);
        setmealService.startOrStop(status, id);
        cleanRedis("setmealCache::*");
        return Result.success();
    }

    private void cleanRedis(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
