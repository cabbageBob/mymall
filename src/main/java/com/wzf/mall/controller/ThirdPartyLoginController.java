package com.wzf.mall.controller;

import com.wzf.mall.auth.config.AuthConfig;
import com.wzf.mall.auth.model.AuthCallback;
import com.wzf.mall.auth.request.AuthGitHubRequest;
import com.wzf.mall.auth.request.AuthRequest;
import com.wzf.mall.service.UmsAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("login/thridParty")
public class ThirdPartyLoginController {
    @Autowired
    private UmsAdminService adminService;

    @RequestMapping("/git")
    public void gitLogin(HttpServletResponse response) throws IOException {
        AuthRequest authRequest = getAuthRequest();
        response.sendRedirect(authRequest.authorize());
    }
    @RequestMapping("/callback/github")
    public Object login(AuthCallback authCallback){
        AuthRequest authRequest = getAuthRequest();
        return authRequest.login(authCallback);
    }

    private AuthRequest getAuthRequest(){
        return new AuthGitHubRequest(AuthConfig.builder()
                .clientId("867a3a2e0f16e6310b93")
                .clientSecret("09f6b79e44f8343946c13814f04bd542a4d1149e")
                .redirectUri("http://localhost:16333/outhTest/callback/github")
                .build());
    }
}
