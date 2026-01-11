package cn.gdsdxy.campustrading.common.mapper;

import cn.gdsdxy.campustrading.common.entity.MessagesEntity;
import cn.gdsdxy.campustrading.common.model.vo.userVo.MessageChatSessionVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.MessageVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessagesMapper extends BaseMapper<MessagesEntity> {

    /**
     * 查询聊天记录完整列表（包含用户信息）
     */
    List<MessageVo> selectChatRecord(@Param("userId") Integer userId,
                                     @Param("productId") Integer productId,
                                     @Param("otherUserId") Integer otherUserId);

    /**
     * 查询会话完整列表
     */
    List<MessageChatSessionVo> selectChatSessionList(@Param("userId") Integer userId);

    /**
     * 批量标记已读
     */
    void markAllAsRead(@Param("userId") Integer userId,
                       @Param("productId") Integer productId,
                       @Param("otherUserId") Integer otherUserId);

    /**
     * 删除聊天记录
     */
    void deleteChatRecord(@Param("userId") Integer userId,
                          @Param("productId") Integer productId,
                          @Param("otherUserId") Integer otherUserId);

    /**
     * 统计未读消息数量
     */
    Integer countUnreadMessages(@Param("userId") Integer userId);
}