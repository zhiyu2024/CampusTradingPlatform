package cn.gdsdxy.campustrading.common.model.dto.userDto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateUserParam {//用户名不可更改,注册的时候要提示
    private String nickname;
    private String phone;
    private MultipartFile avatar;
    private String campus;
}