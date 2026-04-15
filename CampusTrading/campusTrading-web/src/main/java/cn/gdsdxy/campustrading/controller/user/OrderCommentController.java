package cn.gdsdxy.campustrading.controller.user;


import cn.gdsdxy.campustrading.common.model.dto.userDto.OrderCommentParam;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.util.SecurityUtil;
import cn.gdsdxy.campustrading.common.entity.OrderCommentsEntity;
import cn.gdsdxy.campustrading.common.service.OrderCommentsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
@Slf4j // ✅ 添加日志
@Tag(name = "用户商品评论管理", description = "用户商品评论的相关接口") // ✅ 使用 @Tag

@RestController
@RequestMapping("/api/user/order/comment")
public class OrderCommentController {

    @Autowired
    private OrderCommentsService orderCommentsService;


    /**
     * 提交订单评价
     */
    @PostMapping("/add")
    public FwResult addComment(@RequestBody OrderCommentParam param) {
        orderCommentsService.addComment(param);
        return FwResult.ok("评价成功");
    }

    /**
     * 根据商品ID获取评价列表
     */
    @GetMapping("/list/{productId}")
    public FwResult getCommentList(@PathVariable Integer productId,
                                    @RequestParam(required = false) Integer sentiment) {
        return FwResult.ok(orderCommentsService.getCommentListByProductId(productId, sentiment));
    }

}