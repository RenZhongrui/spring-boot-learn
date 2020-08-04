package com.learn.security.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import com.learn.security.dao.enums.SexEnum;
import com.learn.security.dao.enums.SysRoleEnum;
import com.learn.security.dao.enums.ThirdLoginTypeEnum;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
@Data
public class SysUser implements UserDetails {

    @TableId(type = IdType.UUID)
    private String id;

    private String username;

    private String password;
    /**
     * 人脸唯一标识
     */
    private String faceToken;
    /**
     * 用户角色
     */
    @TableField("role_id")
    @JsonValue
    private SysRoleEnum role;
    /**
     * 性别
     */
    @JsonValue
    private SexEnum sex;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 身份证号
     */
    private String idCard;
    /**
     * 学生证号
     */
    private String studentIdCard;

    private String tel;
    /**
     * 学校
     */
    private Integer schoolId;
    /**
     * 三方登陆类型
     */
    @TableField("third_login_type")
    @JsonValue
    private ThirdLoginTypeEnum thirdLogin;
    /**
     * 三方登陆ID
     */
    private String thirdLoginId;
    /**
     * 是否启用
     * 1：启用；0：禁用
     */
    private Integer hasEnable;

    @Version
    private Integer version;

    /**
     * 解冻时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lockDate;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;

    // 设置角色
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<GrantedAuthority>(){{
            add(new SimpleGrantedAuthority(getRole().getName()));
        }};
    }

    // 是否冻结
    @Override
    public boolean isAccountNonLocked() {
        if(this.lockDate == null) { // 没有冻结日期表示冻结该账户
            return true;
        }
        return LocalDateTime.now().isAfter(this.lockDate); // 判断当前日期是否在冻结日期之后
    }

    /**
     * 是否启用
     */
    @Override
    public boolean isEnabled() {
        return this.hasEnable == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
}
