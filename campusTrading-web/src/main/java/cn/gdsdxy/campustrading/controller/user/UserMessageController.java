package cn.gdsdxy.campustrading.controller.user;

import cn.gdsdxy.campustrading.common.model.dto.userDto.MessageQueryParam;
import cn.gdsdxy.campustrading.common.model.dto.userDto.MessageSendParam;
import cn.gdsdxy.campustrading.common.model.vo.userVo.MessageChatSessionVo;
import cn.gdsdxy.campustrading.common.model.vo.userVo.MessageVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.IMessagesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j // ✅ 添加日志
@Tag(name = "用户消息管理", description = "用户消息相关接口") // ✅ 使用 @Tag
@RestController
@RequestMapping("/api/user/message")
public class UserMessageController {
       @Autowired
      IMessagesService messagesService;

    @PostMapping("/send")
    public FwResult<String> sendMessage( @RequestBody MessageSendParam param) {//发送信息给指定用户
        messagesService.sendMessage(param);
        return FwResult.ok("信息发送成功");
    }

    @GetMapping("/chat")
    public FwResult<List<MessageVo>> getChatRecord(//获取与指定用户的聊天记录
            @RequestParam Integer productId,
            @RequestParam Integer otherUserId) {

        return FwResult.ok(messagesService.getChatRecord(productId, otherUserId));
    }

    @GetMapping("/sessions")
    public FwResult<List<MessageChatSessionVo>> getChatSessionList() {//获取当前用户的聊天记录,聊天列表
        return FwResult.ok(messagesService.getChatSessionList());
    }

    @PostMapping("/read")//读消息
    public FwResult<String> markAsRead(@RequestParam Integer messageId) {
        messagesService.markAsRead(messageId);
        return FwResult.ok("您已读此条信息");
    }

    @PostMapping("/read/batch")//全部已读
    public FwResult<String> markAllAsRead(
            @RequestParam Integer productId,
            @RequestParam Integer otherUserId) {
        messagesService.markAllAsRead(productId, otherUserId);
        return FwResult.ok("您已读了全部信息");
    }

    @DeleteMapping("/delete/chat")//删除聊天记录
    public FwResult<String> deleteChatRecord(
            @RequestParam Integer productId,
            @RequestParam Integer otherUserId) {
        messagesService.deleteChatRecord(productId, otherUserId);
        return FwResult.ok("您成功删除了与对方的聊天信息");
    }

    @DeleteMapping("/delete")//删除一条信息
    public FwResult<String> deleteMessage(@RequestParam Integer messageId) {
        messagesService.deleteMessage(messageId);
        return FwResult.ok("您成功删除了一条信息");
    }


    @GetMapping("/unread/count")//未读
    public FwResult<Integer> getUnreadCount() {
        return FwResult.ok(messagesService.getUnreadCount());
    }
}
