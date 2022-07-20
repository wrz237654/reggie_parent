package com.reggie.controller.admin;


import com.reggie.annotation.IgnoreToken;
import com.reggie.constant.JwtClaimsConstant;
import com.reggie.dto.EmployeeDTO;
import com.reggie.dto.EmployeeLoginDTO;
import com.reggie.entity.Employee;
import com.reggie.properties.JwtProperties;
import com.reggie.result.R;
import com.reggie.service.impl.EmployeeServiceImpl;
import com.reggie.utils.JwtUtil;
import com.reggie.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * 员工操作控制器
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {
    @Autowired
    private EmployeeServiceImpl employeeService;


    @Autowired
    private JwtProperties jwtProperties;   //jwt令牌相关配置类

    /**
     * 测试方法，用于测试jwt校验
     *
     * @return
     */
    @ApiOperation("Jwt测试接口")
    @IgnoreToken //自定义放行拦截注解
    @GetMapping("/testJwt")
    public R<String> testJwt() {
        return R.success("jwt test");
    }


    /**
     * 员工登录
     *
     * @param employeeLoginDTO 用户账号密码
     * @return 登录员工信息
     */
    @PostMapping("/login")
    @ApiOperation("员工登录接口")
    public R<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：用户名{}，密码{}", employeeLoginDTO.getUsername(), employeeLoginDTO.getPassword());
        //调用业务登录返回对象
        Employee employeeLogin = employeeService.login(employeeLoginDTO);

        //设置jwt中有效载荷部分的数据，通常是用户的身份标识
        HashMap<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employeeLogin.getId());


        //创建jwt令牌
        String token = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), claims);

        //封装响应对象
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employeeLogin.getId())
                .name(employeeLogin.getName())
                .userName(employeeLogin.getUsername())
                .token(token)
                .build();

        return R.success(employeeLoginVO);
    }

    /**
     * 员工退出
     *
     * @return 返回success
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出接口")
    public R<String> logout() {
        return R.success("退出登录");
    }


    /**
     * 员工注册
     *
     * @param employeeDTO  获取添加的员工表单
     * @return  返回success
     */
    @PostMapping
    @ApiOperation("员工注册")
    public R<String> add(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工:{}", employeeDTO);
        employeeService.save(employeeDTO);
        return R.success();
    }
}
