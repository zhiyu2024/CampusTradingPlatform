package cn.gdsdxy.campustrading.common.service.impl;

import cn.gdsdxy.campustrading.common.entity.MessagesEntity;
import cn.gdsdxy.campustrading.common.mapper.MessagesMapper;
import cn.gdsdxy.campustrading.common.service.IMessagesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
