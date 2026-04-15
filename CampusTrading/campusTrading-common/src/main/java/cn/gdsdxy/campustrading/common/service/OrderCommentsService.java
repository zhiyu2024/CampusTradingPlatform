package cn.gdsdxy.campustrading.common.service;


import cn.gdsdxy.campustrading.common.entity.OrderCommentsEntity;
import cn.gdsdxy.campustrading.common.model.dto.userDto.OrderCommentParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface OrderCommentsService extends IService<OrderCommentsEntity> {
    void addComment(OrderCommentParam param);
    List<OrderCommentsEntity> getCommentListByProductId(Integer productId, Integer sentiment);
}