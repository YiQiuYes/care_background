package com.linping.care.interceptors;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.linping.care.entity.ResultData;
import com.linping.care.entity.ReturnCode;
import com.linping.care.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JWTInterceptors implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        Map<String, Object> map = new HashMap<>();
        ResultData<String> resultData = new ResultData<>();
        // 获取请求头中令牌
        String token = request.getHeader("token");
        try {
            // 验证令牌
            JWTUtils.verifyToken(token);
            return true;  // 放行请求
        } catch (SignatureVerificationException e) {
            resultData.setCode(ReturnCode.RC401.getCode());
            resultData.setMsg(ReturnCode.RC401.getMsg());
        } catch (TokenExpiredException e) {
            resultData.setCode(ReturnCode.RC400.getCode());
            resultData.setMsg(ReturnCode.RC400.getMsg());
        } catch (AlgorithmMismatchException e) {
            resultData.setCode(ReturnCode.RC403.getCode());
            resultData.setMsg(ReturnCode.RC403.getMsg());
        } catch (Exception e) {
            resultData.setCode(ReturnCode.RC402.getCode());
            resultData.setMsg(ReturnCode.RC402.getMsg());
        }
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(resultData.toJson());
        return false;
    }
}
