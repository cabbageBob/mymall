package com.wzf.mall.auth.request;


import cn.hutool.core.bean.BeanUtil;
import com.wzf.mall.auth.config.AuthConfig;
import com.wzf.mall.auth.config.AuthSource;
import com.wzf.mall.auth.exception.AuthException;
import com.wzf.mall.auth.model.AuthCallback;
import com.wzf.mall.auth.model.AuthResponse;
import com.wzf.mall.auth.model.AuthToken;
import com.wzf.mall.auth.model.AuthUser;
import com.wzf.mall.auth.utils.AuthChecker;
import com.wzf.mall.dto.UmsAdminParam;
import com.wzf.mall.mbg.model.UmsAdmin;
import com.wzf.mall.service.UmsAdminService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public abstract class BaseAuthRequest implements AuthRequest {
    @Autowired
    private UmsAdminService umsAdminService;

    protected AuthConfig config;
    protected AuthSource source;

    public BaseAuthRequest(AuthConfig authConfig, AuthSource authSource) {
        this.config = authConfig;
        this.source = authSource;
        if (!AuthChecker.isSupportedAuth(authConfig,authSource)){
            throw new AuthException(ResponseStatus.PARAMETER_INCOMPLETE);
        }
    }

    protected abstract AuthToken getAccessToken(AuthCallback authCallback);

    protected abstract AuthUser getUserInfo(AuthToken authToken);

    @Override
    public String login(AuthCallback authCallback) {
        AuthChecker.checkCode(source==AuthSource.ALIPAY?authCallback.getAuth_code():authCallback.getCode());
        AuthChecker.checkState(authCallback.getState(), config.getState());
        AuthToken authToken = this.getAccessToken(authCallback);
        AuthUser user = this.getUserInfo(authToken);
        //获取本应用中对应的用户
        /*
        这里的参数应该设置为user.getUuid()的类型，或者通过某种方式根据user.getUuid()来生成一个唯一的id，
        例如user.getUuid()+user.getSource()。
        由于是之前写的一个例子，没考虑到第三方登录的情况，不应该将id设为Long类型
         */
        UmsAdmin umsAdmin = umsAdminService.getItem(234353L);
        if (umsAdmin==null){
            UmsAdminParam umsAdminParam = new UmsAdminParam();
            BeanUtil.copyProperties(user,umsAdminParam);
            umsAdminParam.setIcon(user.getAvatar());
            umsAdminService.register(umsAdminParam);
            UmsAdmin registed = umsAdminService.getAdminByUsername(user.getUsername());
            return umsAdminService.login(registed.getUsername(),registed.getPassword());
        }
        return umsAdminService.login(umsAdmin.getUsername(),umsAdmin.getPassword());
    }

    private AuthResponse responseError(Exception e) {
        int errorCode = ResponseStatus.FAILURE.getCode();
        if (e instanceof AuthException) {
            errorCode = ((AuthException) e).getErrorCode();
        }
        return AuthResponse.builder().code(errorCode).msg(e.getMessage()).build();
    }

    /**
     * 返回认证url，可自行跳转页面
     *
     * @return 返回授权地址
     */
    @Override
    public abstract String authorize();
}
