package com.wzf.mall.auth.request;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import com.alibaba.fastjson.JSONObject;
import com.wzf.mall.auth.config.AuthConfig;
import com.wzf.mall.auth.config.AuthSource;
import com.wzf.mall.auth.exception.AuthException;
import com.wzf.mall.auth.model.AuthCallback;
import com.wzf.mall.auth.model.AuthToken;
import com.wzf.mall.auth.model.AuthUser;
import com.wzf.mall.auth.model.AuthUserGender;
import com.wzf.mall.auth.utils.GlobalAuthUtil;
import com.wzf.mall.auth.utils.UrlBuilder;

import java.util.Map;

public class AuthGitHubRequest extends BaseAuthRequest {
    public AuthGitHubRequest(AuthConfig authConfig) {
        super(authConfig, AuthSource.GITHUB);
    }

    @Override
    protected AuthToken getAccessToken(AuthCallback authCallback) {
        String accessTokenUrl = UrlBuilder.getGithubAccessTokenUrl(config.getClientId(), config.getClientSecret(), authCallback.getCode(), config.getRedirectUri());
        HttpResponse response = HttpRequest.post(accessTokenUrl).execute();
        Map<String, String> res = GlobalAuthUtil.parseStringToMap(response.body());
        if (res.containsKey("error")) {
            throw new AuthException(res.get("error") + ":" + res.get("error_description"));
        }
        return AuthToken.builder().accessToken(res.get("access_token")).build();
    }

    @Override
    protected AuthUser getUserInfo(AuthToken authToken) {
        String accessToken = authToken.getAccessToken();
        HttpResponse response = HttpRequest.get(UrlBuilder.getGithubUserInfoUrl(accessToken)).execute();
        String userInfo = response.body();
        JSONObject object = JSONObject.parseObject(userInfo);
        return AuthUser.builder()
                .uuid(object.getString("id"))
                .username(object.getString("login"))
                .avatar(object.getString("avatar_url"))
                .blog(object.getString("blog"))
                .nickname(object.getString("name"))
                .company(object.getString("company"))
                .location(object.getString("location"))
                .email(object.getString("email"))
                .remark(object.getString("bio"))
                .gender(AuthUserGender.UNKNOW)
                .token(authToken)
                .source(AuthSource.GITHUB)
                .build();
    }

    /**
     * 返回认证url，可自行跳转页面
     *
     * @return 返回授权地址
     */
    @Override
    public String authorize() {
        return UrlBuilder.getGithubAuthorizeUrl(config.getClientId(), config.getRedirectUri(), config.getState());
    }
}
