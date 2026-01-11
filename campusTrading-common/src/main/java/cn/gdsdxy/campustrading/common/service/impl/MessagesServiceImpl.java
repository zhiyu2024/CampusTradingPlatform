package cn.gdsdxy.campustrading.common.service.impl;

import cn.gdsdxy.campustrading.common.entity.MessagesEntity;
import cn.gdsdxy.campustrading.common.entity.ProductsEntity;
import cn.gdsdxy.campustrading.common.entity.UsersEntity;
import cn.gdsdxy.campustrading.common.exception.BusinessException;
import cn.gdsdxy.campustrading.common.mapper.MessagesMapper;
import cn.gdsdxy.campustrading.common.mapper.ProductsMapper;
import cn.gdsdxy.campustrading.common.mapper.UsersMapper;
import cn.gdsdxy.campustrading.common.model.dto.userDto.MessageQueryParam;
import cn.gdsdxy.campustrading.common.model.dto.userDto.MessageSendParam;
import cn.gdsdxy.campustrading.common.model.vo.userVo.MessageChatSessionVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.MessageVo;
import cn.gdsdxy.campustrading.common.service.IMessagesService;
import cn.gdsdxy.campustrading.common.util.SecurityUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户消息记录表 服务实现类
 * </p>
 *
 * @author CampusTrading
 * @since 2026-01-05
 */
@Service
public class MessagesServiceImpl extends ServiceImpl<MessagesMapper, MessagesEntity> implements IMessagesService {
    @Autowired
    MessagesMapper messagesMapper;

    @Autowired
    ProductsMapper productsMapper;

    @Autowired
    UsersMapper usersMapper;

    private Long getCurrentUserId() {
        return SecurityUtil.getUserId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(MessageSendParam param) {
        Long senderId = getCurrentUserId();

        // 验证商品和接收者
        validateProductAndReceiver(param.getProductId(), param.getReceiverId());

        // 验证不能给自己发消息
        if (senderId.equals(param.getReceiverId().longValue())) {
            throw new BusinessException(4003, "不能给自己发送消息");
        }
        LocalDateTime now = LocalDateTime.now();
        Date currentDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

        // 构建消息实体
        MessagesEntity message = new MessagesEntity()
                .setProductId(param.getProductId())
                .setSenderId(senderId.intValue())  // 根据实际字段类型调整
                .setReceiverId(param.getReceiverId())
                .setContent(param.getContent())
                .setMessageType(param.getMessageType().byteValue())
                .setIsRead((byte) 0)
                .setCreatedAt(currentDate);  // 使用LocalDateTime

        messagesMapper.insert(message);
    }

    @Override
    public List<MessageVo> getChatRecord(Integer productId, Integer otherUserId) {
        return messagesMapper.selectChatRecord(
                getCurrentUserId().intValue(), productId, otherUserId);
    }

    @Override
    public List<MessageChatSessionVo> getChatSessionList() {
        return messagesMapper.selectChatSessionList(getCurrentUserId().intValue());
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

    /**
     * 验证商品和接收者是否存在
     */
    private void validateProductAndReceiver(Integer productId, Integer receiverId) {
        if (productsMapper.selectById(productId) == null) {
            throw new BusinessException(4001, "商品不存在");
        }
        if (usersMapper.selectById(receiverId) == null) {
            throw new BusinessException(4002, "接收者不存在");
        }
    }

    /**
     * 验证消息权限
     * @param isReceiverOnly 是否仅接收者可操作
     */
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
