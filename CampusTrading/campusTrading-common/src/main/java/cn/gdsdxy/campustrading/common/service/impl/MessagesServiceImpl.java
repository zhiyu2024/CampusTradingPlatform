package cn.gdsdxy.campustrading.common.service.impl;

import cn.gdsdxy.campustrading.common.entity.MessagesEntity;
import cn.gdsdxy.campustrading.common.entity.ProductImagesEntity;
import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.entity.UsersEntity;
import cn.gdsdxy.campustrading.common.exception.BusinessException;
import cn.gdsdxy.campustrading.common.mapper.MessagesMapper;
import cn.gdsdxy.campustrading.common.mapper.ProductImagesMapper;
import cn.gdsdxy.campustrading.common.mapper.ProductsMapper;
import cn.gdsdxy.campustrading.common.mapper.UsersMapper;
import cn.gdsdxy.campustrading.common.model.dto.userDto.MessageSendParam;
import cn.gdsdxy.campustrading.common.model.vo.userVo.MessageChatSessionVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.MessageVo;
import cn.gdsdxy.campustrading.common.service.IMessagesService;
import cn.gdsdxy.campustrading.common.util.SecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessagesServiceImpl extends ServiceImpl<MessagesMapper, MessagesEntity> implements IMessagesService {
    @Autowired
    MessagesMapper messagesMapper;

    @Autowired
    ProductsMapper productsMapper;
    @Autowired
    ProductImagesMapper productImagesMapper;

    @Autowired
    UsersMapper usersMapper;

    private Long getCurrentUserId() {
        return SecurityUtil.getUserId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(MessageSendParam param) {
        Long senderId = getCurrentUserId();

        validateProductAndReceiver(param.getProductId(), param.getReceiverId());

        if (senderId.equals(param.getReceiverId().longValue())) {
            throw new BusinessException(4003, "不能给自己发送消息");
        }
        LocalDateTime now = LocalDateTime.now();
        Date currentDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

        MessagesEntity message = new MessagesEntity()
                .setProductId(param.getProductId())
                .setSenderId(senderId.intValue())
                .setReceiverId(param.getReceiverId())
                .setContent(param.getContent())
                .setMessageType(param.getMessageType().byteValue())
                .setIsRead((byte) 0)
                .setCreatedAt(currentDate);

        messagesMapper.insert(message);
    }

    @Override
    public List<MessageVo> getChatRecord(Integer productId, Integer otherUserId) {
        return messagesMapper.selectChatRecord(
                getCurrentUserId().intValue(), productId, otherUserId);
    }

    // ========================= 🔥 这里已加上排序 =========================
    @Override
    public List<MessageChatSessionVo> getChatSessionList() {
        List<MessageChatSessionVo> sessionList =
                messagesMapper.selectChatSessionList(getCurrentUserId().intValue());

        // 排序规则：未读 > 已读；同状态按最新时间倒序
        sessionList = sessionList.stream().sorted((a, b) -> {
            // 1. 未读排前面
            if (a.getUnreadCount() > 0 && b.getUnreadCount() == 0) {
                return -1;
            }
            if (a.getUnreadCount() == 0 && b.getUnreadCount() > 0) {
                return 1;
            }
            // 2. 都已读 → 按最后消息时间最新排前面
            Date timeA = a.getLatestMessageTime();
            Date timeB = b.getLatestMessageTime();
            if (timeA == null) return 1;
            if (timeB == null) return -1;
            return timeB.compareTo(timeA);
        }).collect(Collectors.toList());

        return sessionList;
    }
    // ==================================================================

    @Override
    public MessageChatSessionVo getOrCreateSession(Integer productId, Integer otherUserId) {
        Long userId = SecurityUtil.getUserId();
        Integer currentUserId = userId.intValue();

        LambdaQueryWrapper<MessagesEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MessagesEntity::getProductId, productId)
                .and(w -> w
                        .eq(MessagesEntity::getSenderId, currentUserId).eq(MessagesEntity::getReceiverId, otherUserId)
                        .or()
                        .eq(MessagesEntity::getSenderId, otherUserId).eq(MessagesEntity::getReceiverId, currentUserId)
                );
        MessagesEntity one = this.getOne(wrapper);

        if (one != null) {
            return buildSession(one);
        }

        MessagesEntity newMsg = new MessagesEntity();
        newMsg.setProductId(productId);
        newMsg.setSenderId(currentUserId);
        newMsg.setReceiverId(otherUserId);
        newMsg.setContent(null);
        newMsg.setIsRead((byte)0);
        newMsg.setCreatedAt(new Date());
        this.save(newMsg);

        return buildSession(newMsg);
    }

    private MessageChatSessionVo buildSession(MessagesEntity entity) {
        MessageChatSessionVo vo = new MessageChatSessionVo();

        vo.setProductId(entity.getProductId());
        vo.setLatestMessage(entity.getContent());
        vo.setLatestMessageTime(entity.getCreatedAt());
        vo.setUnreadCount(0);

        Integer currentUserId = SecurityUtil.getUserId().intValue();
        Integer oppositeUserId = entity.getSenderId().equals(currentUserId)
                ? entity.getReceiverId()
                : entity.getSenderId();
        vo.setOppositeUserId(oppositeUserId);

        ProductsEntity product = productsMapper.selectById(entity.getProductId());
        if (product != null) {
            vo.setProductName(product.getProductName());

            ProductImagesEntity firstImg = productImagesMapper.selectOne(
                    Wrappers.lambdaQuery(ProductImagesEntity.class)
                            .eq(ProductImagesEntity::getProductId, entity.getProductId())
                            .orderByAsc(ProductImagesEntity::getSortOrder)
                            .last("limit 1")
            );
            if (firstImg != null) {
                vo.setProductImage(firstImg.getImageUrl());
            }
        }

        UsersEntity oppositeUser = usersMapper.selectById(oppositeUserId);
        if (oppositeUser != null) {
            vo.setOppositeNickname(oppositeUser.getNickname());
            vo.setOppositeAvatar(oppositeUser.getAvatar());
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Integer messageId) {
        Long userId = getCurrentUserId();

        MessagesEntity message = messagesMapper.selectById(messageId);
        validateMessageOwnership(message, userId, true);

        if (message.getIsRead() == 0) {
            message.setIsRead((byte) 1);
            messagesMapper.updateById(message);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Integer productId, Integer otherUserId) {
        messagesMapper.markAllAsRead(getCurrentUserId().intValue(), productId, otherUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteChatRecord(Integer productId, Integer otherUserId) {
        messagesMapper.deleteChatRecord(getCurrentUserId().intValue(), productId, otherUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessage(Integer messageId) {
        Long userId = getCurrentUserId();

        MessagesEntity message = messagesMapper.selectById(messageId);
        validateMessageOwnership(message, userId, false);

        messagesMapper.deleteById(messageId);
    }

    @Override
    public Integer getUnreadCount() {
        return messagesMapper.countUnreadMessages(getCurrentUserId().intValue());
    }

    private void validateProductAndReceiver(Integer productId, Integer receiverId) {
        if (productsMapper.selectById(productId) == null) {
            throw new BusinessException(4001, "商品不存在");
        }
        if (usersMapper.selectById(receiverId) == null) {
            throw new BusinessException(4002, "接收者不存在");
        }
    }

    private void validateMessageOwnership(MessagesEntity message, Long userId, boolean isReceiverOnly) {
        if (message == null) {
            throw new BusinessException(4004, "消息不存在");
        }

        boolean hasPermission = isReceiverOnly
                ? message.getReceiverId().equals(userId.intValue())
                : message.getReceiverId().equals(userId.intValue()) ||
                message.getSenderId().equals(userId.intValue());

        if (!hasPermission) {
            throw new BusinessException(4005, "无权操作此消息");
        }
    }
}