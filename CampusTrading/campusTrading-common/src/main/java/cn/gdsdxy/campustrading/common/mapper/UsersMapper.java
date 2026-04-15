package cn.gdsdxy.campustrading.common.mapper;

import cn.gdsdxy.campustrading.common.entity.UsersEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UsersMapper extends BaseMapper<UsersEntity> {

    @Insert({
            "<script>",
            "INSERT INTO users (student_no, username, password, nickname, phone, campus, role, status) VALUES ",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.studentNo}, #{item.username}, #{item.password}, #{item.nickname}, #{item.phone}, #{item.campus}, #{item.role}, #{item.status})",
            "</foreach>",
            "</script>"
    })
    void batchInsert(@Param("list") List<UsersEntity> list);
}