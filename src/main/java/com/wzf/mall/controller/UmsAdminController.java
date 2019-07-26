package com.wzf.mall.controller;

import com.wzf.mall.common.CommonResult;
import com.wzf.mall.dto.UmsAdminLoginParam;
import com.wzf.mall.dto.UmsAdminParam;
import com.wzf.mall.mbg.model.UmsAdmin;
import com.wzf.mall.mbg.model.UmsPermission;
import com.wzf.mall.service.UmsAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "UmsAdminController",description = "后台用户管理")
@RequestMapping("/admin")
public class UmsAdminController {
    @Autowired
    private UmsAdminService adminService;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @ApiOperation(value = "用户注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public CommonResult<UmsAdmin> register(UmsAdminParam umsAdminParam, BindResult result){
        UmsAdmin admin = adminService.register(umsAdminParam);
        if (null != admin){
            return CommonResult.failed("操作失败，服务器异常");
        }
        return CommonResult.success(admin);
    }
    @ApiOperation(value = "登录以后返回token")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public CommonResult login(UmsAdminLoginParam loginParam){
        String token = adminService.login(loginParam.getUsername(),loginParam.getPassword());
        if (null == token){
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String,String> tokenMap = new HashMap<>();
        tokenMap.put("token",token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation("获取用户所有权限（包括+-权限）")
    @RequestMapping(value = "/permission/{adminId}", method = RequestMethod.GET)
    public CommonResult<List<UmsPermission>>getPermissionList(@PathVariable Long adminId){
        return CommonResult.success(adminService.getPermissionList(adminId));
    }
}
