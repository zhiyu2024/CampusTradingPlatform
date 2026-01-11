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
    public FwResult<Void> sendMessage( @RequestBody MessageSendParam param) {
        messagesService.sendMessage(param);
        return FwResult.ok();
    }

    @GetMapping("/chat")
    public FwResult<List<MessageVo>> getChatRecord(
            @RequestParam Integer productId,
            @RequestParam Integer otherUserId) {
        return FwResult.ok(messagesService.getChatRecord(productId, otherUserId));
    }

    @GetMapping("/sessions")
    public FwResult<List<MessageChatSessionVo>> getChatSessionList() {
        return FwResult.ok(messagesService.getChatSessionList());
    }

    @PostMapping("/read/{messageId}")//读消息
    public FwResult<Void> markAsRead(@PathVariable Integer messageId) {
        messagesService.markAsRead(messageId);
        return FwResult.ok();
    }

    @PostMapping("/read/batch")//全部已读
    public FwResult<Void> markAllAsRead(
            @RequestParam Integer productId,
            @RequestParam Integer otherUserId) {
        messagesService.markAllAsRead(productId, otherUserId);
        return FwResult.ok();
    }

    @DeleteMapping("/chat")
    public FwResult<Void> deleteChatRecord(
            @RequestParam Integer productId,
            @RequestParam Integer otherUserId) {
        messagesService.deleteChatRecord(productId, otherUserId);
        return FwResult.ok();
    }

    @DeleteMapping("/{messageId}")
    public FwResult<Void> deleteMessage(@PathVariable Integer messageId) {
        messagesService.deleteMessage(messageId);
        return FwResult.ok();
    }


    @GetMapping("/unread/count")//未读
    public FwResult<Integer> getUnreadCount() {
        return FwResult.ok(messagesService.getUnreadCount());
    }
}
