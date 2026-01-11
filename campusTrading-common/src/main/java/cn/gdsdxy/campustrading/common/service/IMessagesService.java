package cn.gdsdxy.campustrading.common.service;

import cn.gdsdxy.campustrading.common.entity.MessagesEntity;
import cn.gdsdxy.campustrading.common.model.dto.userDto.MessageSendParam;
import cn.gdsdxy.campustrading.common.model.vo.userVo.MessageChatSessionVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.MessageVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户消息记录表 服务类
 * </p>
 *
 * @author CampusTrading
 * @since 2026-01-05
 */
public interface IMessagesService extends IService<MessagesEntity> {
// 原有代码基础上添加

    /**
     * 发送消息
     */
    void sendMessage(MessageSendParam param);

    /**
     * 获取聊天记录
     */
    List<MessageVo> getChatRecord(Integer productId, Integer otherUserId);

    /**
     * 获取会话列表（最新一条消息）
     */
    List<MessageChatSessionVo> getChatSessionList();

    /**
     * 标记单条消息已读
     */
    void markAsRead(Integer messageId);

    /**
     * 批量标记已读
     */
    void markAllAsRead(Integer productId, Integer otherUserId);

    /**
     * 删除聊天记录
     */
    void deleteChatRecord(Integer productId, Integer otherUserId);

    /**
     * 删除单条消息
     */
    void deleteMessage(Integer messageId);

    /**
     * 获取未读消息总数
     */
    Integer getUnreadCount();
}
