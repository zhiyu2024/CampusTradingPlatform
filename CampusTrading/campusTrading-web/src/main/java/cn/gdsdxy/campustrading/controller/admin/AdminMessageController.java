package cn.gdsdxy.campustrading.controller.admin;

import cn.gdsdxy.campustrading.common.entity.MessagesEntity;
import cn.gdsdxy.campustrading.common.mapper.MessagesMapper;
import cn.gdsdxy.campustrading.common.model.vo.adminVo.MessageListVo;
import cn.gdsdxy.campustrading.common.result.FwResult;
import cn.gdsdxy.campustrading.common.service.IMessagesService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "管理员消息管理", description = "管理员消息管理相关接口")
@RestController
@RequestMapping("/api/admin/messages")
public class AdminMessageController {

    @Autowired
    private IMessagesService messagesService;

    @Autowired
    private MessagesMapper messagesMapper;

    @GetMapping
    public FwResult<IPage<MessageListVo>> getList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Byte message_type) {
        log.info("管理员查询消息列表, page={}, pageSize={}, content={}, message_type={}", page, pageSize, content, message_type);
        Page<MessageListVo> pageParam = new Page<>(page, pageSize);
        IPage<MessageListVo> result = messagesMapper.selectMessageListWithDetails(pageParam, content, message_type);
        return FwResult.ok(result);
    }

    @GetMapping("/{id}")
    public FwResult<MessagesEntity> getDetail(@PathVariable Integer id) {
        log.info("管理员查询消息详情, id={}", id);
        MessagesEntity message = messagesService.getById(id);
        if (message == null) {
            return FwResult.fail("消息不存在");
        }
        return FwResult.ok(message);
    }

    @PostMapping
    public FwResult<String> send(@RequestBody MessagesEntity message) {
        log.info("管理员发送消息, content={}", message.getContent());
        messagesService.save(message);
        return FwResult.ok("发送成功");
    }

    @DeleteMapping("/{id}")
    public FwResult<String> delete(@PathVariable Integer id) {
        log.info("管理员删除消息, id={}", id);
        messagesService.removeById(id);
        return FwResult.ok("删除成功");
    }

    @PutMapping("/{id}/read")
    public FwResult<String> markAsRead(@PathVariable Integer id) {
        log.info("管理员标记消息已读, id={}", id);
        MessagesEntity message = new MessagesEntity();
        message.setMessageId(id);
        message.setIsRead((byte) 1);
        messagesService.updateById(message);
        return FwResult.ok("标记已读成功");
    }
}
