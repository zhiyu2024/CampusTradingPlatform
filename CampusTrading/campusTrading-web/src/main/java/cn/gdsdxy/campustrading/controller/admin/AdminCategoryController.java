package cn.gdsdxy.campustrading.controller.admin;

import cn.gdsdxy.campustrading.common.entity.CategoriesEntity;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.ICategoriesService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "管理员分类管理", description = "管理员分类管理的相关接口")
@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    @Autowired
    private ICategoriesService categoriesService;

    @GetMapping
    public FwResult<IPage<CategoriesEntity>> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String categoryName) {
        log.info("管理员查询分类列表, page={}, pageSize={}, categoryName={}", page, pageSize, categoryName);
        Page<CategoriesEntity> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<CategoriesEntity> wrapper = new LambdaQueryWrapper<>();
        if (categoryName != null && !categoryName.isEmpty()) {
            wrapper.like(CategoriesEntity::getCategoryName, categoryName);
        }
        wrapper.orderByAsc(CategoriesEntity::getSortOrder);
        IPage<CategoriesEntity> result = categoriesService.page(pageParam, wrapper);
        return FwResult.ok(result);
    }

    @GetMapping("/all")
    public FwResult<List<CategoriesEntity>> getAllCategories() {
        log.info("管理员查询所有分类");
        List<CategoriesEntity> list = categoriesService.list(new LambdaQueryWrapper<CategoriesEntity>()
                .orderByAsc(CategoriesEntity::getSortOrder));
        return FwResult.ok(list);
    }

    @GetMapping("/{id}")
    public FwResult<CategoriesEntity> getDetail(@PathVariable Integer id) {
        log.info("管理员查询分类详情, id={}", id);
        CategoriesEntity category = categoriesService.getById(id);
        if (category == null) {
            return FwResult.fail("分类不存在");
        }
        return FwResult.ok(category);
    }

    @PostMapping
    public FwResult<String> create(@RequestBody CategoriesEntity category) {
        log.info("管理员创建分类, categoryName={}", category.getCategoryName());
        categoriesService.save(category);
        return FwResult.ok("创建成功");
    }

    @PutMapping("/{id}")
    public FwResult<String> update(@PathVariable Integer id, @RequestBody CategoriesEntity category) {
        log.info("管理员更新分类, id={}", id);
        category.setCategoryId(id);
        categoriesService.updateById(category);
        return FwResult.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    public FwResult<String> delete(@PathVariable Integer id) {
        log.info("管理员删除分类, id={}", id);
        categoriesService.removeById(id);
        return FwResult.ok("删除成功");
    }

    @GetMapping("/stats")
    public FwResult<Long> getCategoryStats() {
        log.info("管理员查询分类统计");
        Long count = categoriesService.count();
        return FwResult.ok(count);
    }
}
