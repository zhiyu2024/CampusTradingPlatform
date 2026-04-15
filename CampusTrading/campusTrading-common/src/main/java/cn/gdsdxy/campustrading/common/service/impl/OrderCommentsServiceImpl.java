package cn.gdsdxy.campustrading.common.service.impl;

import cn.gdsdxy.campustrading.common.entity.OrdersEntity;
import cn.gdsdxy.campustrading.common.entity.OrderCommentsEntity;
import cn.gdsdxy.campustrading.common.entity.UsersEntity;
import cn.gdsdxy.campustrading.common.exception.BusinessException;
import cn.gdsdxy.campustrading.common.mapper.OrdersMapper;
import cn.gdsdxy.campustrading.common.mapper.OrderCommentsMapper;
import cn.gdsdxy.campustrading.common.mapper.UsersMapper;
import cn.gdsdxy.campustrading.common.model.dto.userDto.OrderCommentParam;
import cn.gdsdxy.campustrading.common.service.OrderCommentsService;
import cn.gdsdxy.campustrading.common.service.BaiduQianfanService;
import cn.gdsdxy.campustrading.common.util.SecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class OrderCommentsServiceImpl extends ServiceImpl<OrderCommentsMapper, OrderCommentsEntity>
        implements OrderCommentsService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private BaiduQianfanService baiduQianfanService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addComment(OrderCommentParam param) {
        // 从token获取当前登录用户
        Long userId = SecurityUtil.getUserId();
        Integer buyerId = userId.intValue();

        // 1. 校验订单是否存在 & 是否属于当前用户
        OrdersEntity order = ordersMapper.selectById(param.getOrderId());
        if (order == null) {
            throw new BusinessException(3004, "订单不存在");
        }
        if (!order.getBuyerId().equals(buyerId)) {
            throw new BusinessException(3006, "无权评价此订单");
        }

        // ===================== 新增：防止重复评价 =====================
        if (order.getHasComment() != null && order.getHasComment() == 1) {
            throw new BusinessException(3009, "该订单已评价，不可重复评价");
        }
        // ==============================================================

        // 2. 组装评价信息
        OrderCommentsEntity comment = new OrderCommentsEntity();
        comment.setOrderId(param.getOrderId());
        comment.setOrderNo(order.getOrderNo());
        comment.setProductId(param.getProductId());
        comment.setBuyerId(buyerId);
        comment.setSellerId(order.getSellerId());
        comment.setContent(param.getContent());
        comment.setScore(param.getScore());
        comment.setCreatedAt(LocalDateTime.now());

        // 3. 保存评价
        baseMapper.insert(comment);

        // 4. 调用百度千帆 API 分析情感倾向
        try {
            BaiduQianfanService.SentimentResult result = baiduQianfanService.analyzeSentiment(param.getContent());
            if (result.getSentiment() != 0) {
                comment.setSentiment(result.getSentiment());
                comment.setConfidence(result.getConfidence());
                baseMapper.updateById(comment);
            }
        } catch (Exception e) {
            log.error("情感分析失败，不影响评论发表", e);
        }

        //评价成功后更新订单为已评价
        order.setHasComment((byte) 1);
        ordersMapper.updateById(order);

    }

    @Override
    public List<OrderCommentsEntity> getCommentListByProductId(Integer productId, Integer sentiment) {
        LambdaQueryWrapper<OrderCommentsEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderCommentsEntity::getProductId, productId);
        if (sentiment != null && sentiment != 0) {
            wrapper.eq(OrderCommentsEntity::getSentiment, sentiment);
        }
        wrapper.orderByDesc(OrderCommentsEntity::getCreatedAt);
        List<OrderCommentsEntity> list = baseMapper.selectList(wrapper);

        // 遍历查询每个评论的买家信息（昵称+头像）
        for (OrderCommentsEntity comment : list) {
            UsersEntity user = usersMapper.selectById(comment.getBuyerId());
            if (user != null) {
                comment.setBuyerNickname(user.getNickname());
                comment.setBuyerAvatar(user.getAvatar());
            }
        }
        return list;
    }
}