package com.doc.format.util.user;

import com.doc.format.entity.UserEntity;
import lombok.Data;
import org.docx4j.wml.U;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Optional;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2025/3/24 20:49
 */
// 自定义 UserDetails 实现类
// 简化的YourUserDetails.java（仅需ID和名称）

@Data // 使用Lombok注解简化getter/setter
public class YourUserDetails implements UserDetails {
    private final User user; // 用户实体类（需包含id和name字段）

    public YourUserDetails(User user) {
        this.user = user;
    }

    // 必须实现的UserDetails方法（简化实现）
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 无需权限时返回空列表
    }

    @Override
    public String getPassword() {
        return ""; // 可返回空字符串（实际可能需要根据需求调整）
    }

    @Override
    public String getUsername() {
        return user.getName(); // 返回用户名称
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // 获取用户实体
    public User getUser() {
        return user;
    }
}
