package com.fooddelivery.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fooddelivery.common.BusinessException;
import com.fooddelivery.common.Constants;
import com.fooddelivery.dto.PageQueryDTO;
import com.fooddelivery.dto.WxLoginDTO;
import com.fooddelivery.entity.User;
import com.fooddelivery.mapper.UserMapper;
import com.fooddelivery.service.UserService;
import com.fooddelivery.util.JwtUtil;
import com.fooddelivery.vo.WxLoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Override
    public WxLoginVO wxLogin(WxLoginDTO dto) {
        String openid = getOpenid(dto.getCode());

        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getOpenid, openid));

        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setStatus(Constants.STATUS_ENABLED);
            save(user);
            log.info("新用户注册: openid={}", openid);
        }

        if (user.getStatus() == Constants.STATUS_DISABLED) {
            throw new BusinessException("账号已被禁用");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("openid", openid);
        String token = jwtUtil.generateToken(claims);

        return WxLoginVO.builder()
                .id(user.getId())
                .openid(openid)
                .token(token)
                .build();
    }

    // 开发环境测试用户的 openid（与 data.sql 初始化数据一致）
    private static final String DEV_TEST_OPENID = "dev_openid_test_user";
    
    private String getOpenid(String code) {
        // 开发环境模拟登录：如果 appid 是占位符，使用测试用户 openid
        if ("your-appid".equals(appid) || !StringUtils.hasText(appid)) {
            log.info("开发环境模拟登录: 使用测试用户 openid={}", DEV_TEST_OPENID);
            return DEV_TEST_OPENID;
        }
        
        String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                WX_LOGIN_URL, appid, secret, code);

        String response = HttpUtil.get(url);
        JSONObject json = JSONUtil.parseObj(response);

        String openid = json.getStr("openid");
        if (!StringUtils.hasText(openid)) {
            String errcode = json.getStr("errcode");
            String errmsg = json.getStr("errmsg");
            log.error("微信登录失败: errcode={}, errmsg={}, response={}", errcode, errmsg, response);
            throw new BusinessException("微信登录失败，请稍后重试");
        }

        return openid;
    }

    @Override
    public User getInfo(Long userId) {
        return getById(userId);
    }

    @Override
    public IPage<User> pageQuery(PageQueryDTO dto) {
        Page<User> page = new Page<>(dto.getPage(), dto.getPageSize());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.like(User::getNickname, dto.getKeyword())
                    .or()
                    .like(User::getPhone, dto.getKeyword());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(User::getStatus, dto.getStatus());
        }

        wrapper.orderByDesc(User::getCreateTime);
        return page(page, wrapper);
    }
}
