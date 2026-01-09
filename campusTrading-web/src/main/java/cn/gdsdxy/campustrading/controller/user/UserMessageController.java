package cn.gdsdxy.campustrading.controller.user;

import cn.gdsdxy.campustrading.common.model.dto.userDto.MessageSendParam;
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
    IMessagesService iMessagesService;
    // 在UserMessageController类中添加

    /**
     * 发送消息
     */
    @PostMapping("/send")
    public FwResult<String> sendMessage(@RequestBody MessageSendParam param) {
        iMessagesService.sendMessage(param);
        return FwResult.ok("发送成功");
    }

    /**
     * 获取聊天记录
     */
    @GetMapping("/chat")
    public FwResult<List<MessageVo>> getChatRecord(@RequestParam Integer productId,
                                                   @RequestParam Integer otherUserId) {
        List<MessageVo> list = iMessagesService.getChatRecord( productId, otherUserId);
        return FwResult.ok(list);
    }

    /**
     * 标记消息已读
     */
    @PostMapping("/read")
    public FwResult<String> markAsRead(@RequestParam Integer messageId) {
        iMessagesService.markAsRead( messageId);
        return FwResult.ok("标记成功");
    }

    /**
     * 删除聊天记录
     */
    @PostMapping("/chat")
    public FwResult<String> deleteChatRecord(@RequestParam Integer productId,
                                             @RequestParam Integer otherUserId) {
        iMessagesService.deleteChatRecord( productId, otherUserId);
        return FwResult.ok("删除成功");
    }

    /**
     * 删除单条消息
     */
    @PostMapping("/{messageId}")
    public FwResult<String> deleteMessage(@RequestHeader("Authorization") String token,
                                          @PathVariable Integer messageId) {
        iMessagesService.deleteMessage( messageId);
        return FwResult.ok("删除成功");
    }
}
