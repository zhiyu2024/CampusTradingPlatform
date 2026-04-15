package cn.gdsdxy.campustrading.common.model.dto.adminDto;

import lombok.Data;

@Data
public class AuditParam {//审核参数
    private Integer id;
//value = "审核状态", required = true, notes = "1-通过, 2-拒绝"
    private Integer status;//审核状态不能为空
    private String remark;//审核意见
}