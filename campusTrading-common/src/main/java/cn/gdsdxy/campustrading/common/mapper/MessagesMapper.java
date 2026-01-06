package cn.gdsdxy.campustrading.common.mapper;

import cn.gdsdxy.campustrading.common.entity.MessagesEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessagesMapper extends BaseMapper<MessagesEntity> {

    @Insert({
            "<script>",
            "INSERT INTO messages (product_id, sender_id, receiver_id, content, message_type, is_read, created_at) VALUES ",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.productId}, #{item.senderId}, #{item.receiverId}, #{item.content}, #{item.messageType}, #{item.isRead}, #{item.createdAt})",
            "</foreach>",
            "</script>"
    })
    void batchInsert(@Param("list") List<MessagesEntity> list);
}