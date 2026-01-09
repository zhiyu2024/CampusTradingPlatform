package cn.gdsdxy.campustrading.common.service;

import cn.gdsdxy.campustrading.common.entity.MessagesEntity;
import cn.gdsdxy.campustrading.common.model.dto.userDto.MessageSendParam;
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
    FwResult sendMessage( MessageSendParam param);//Integer senderId,

    /**
     * 获取聊天记录
     */
    List<MessageVo> getChatRecord(Integer productId, String ortherUserId);//Integer userId,ortherUserId是卖家的id 前端应该是通过昵称获取?

    /**
     * 标记消息已读
     */
    FwResult markAsRead( Integer messageId);//Integer userId,

    /**
     * 删除聊天记录
     */
    FwResult deleteChatRecord( Integer productId, Integer otherUserId);//Integer userId,

    /**
     * 删除单条消息
     */
    FwResult deleteMessage( Integer messageId);//Integer userId,
}
