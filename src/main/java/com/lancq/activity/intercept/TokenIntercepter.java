package com.lancq.activity.intercept;

import com.lancq.activity.controller.BaseController;
import com.lancq.commom.annotation.Anoymous;
import com.lancq.commom.constants.MallWebConstant;
import com.lancq.commom.utils.CookieUtil;
import com.lancq.user.IUserCoreService;
import com.lancq.user.dto.CheckAuthRequest;
import com.lancq.user.dto.CheckAuthResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @Author lancq
 * @Description
 * @Date 2018/7/5
 **/
public class TokenIntercepter extends HandlerInterceptorAdapter {
    Logger Log = LoggerFactory.getLogger(this.getClass());
    private final String ACCESS_TOKEN="access_token";

    @Autowired
    IUserCoreService userCoreService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        Log.debug("request:" + request);
        Log.debug("response:" + response);
        Log.debug("handler:" + handler);
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Object bean = handlerMethod.getBean();

        if(isAnoymous(handlerMethod)){
            return true;
        }
        if(!(bean instanceof BaseController)){
            throw new RuntimeException("must extend basecontroller");
        }

        String token = CookieUtil.getCookieValue(request, ACCESS_TOKEN);

        boolean isAjax = CookieUtil.isAjax(request);
        if(StringUtils.isBlank(token)){
            if(isAjax){
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("{\"code\":\"-1\",\"msg\":\"error\"}");
                return false;
            }
            response.sendRedirect(MallWebConstant.MALL_SSO_ACCESS_URL);
            return false;
        }
        CheckAuthRequest checkAuthRequest=new CheckAuthRequest();
        checkAuthRequest.setToken(token);
        CheckAuthResponse checkAuthResponse = userCoreService.validToken(checkAuthRequest);
        if("000000".equals(checkAuthResponse.getCode())){
            BaseController baseController = (BaseController) bean;
            baseController.setUid(checkAuthResponse.getUid());
            return super.preHandle(request,response,handler);
        }

        if(isAjax){
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("{\"code\":\""+checkAuthResponse.getCode()+"\"" +
                    ",\"msg\":\""+checkAuthResponse.getMsg()+"\"}");
            return false;
        }
        response.sendRedirect(MallWebConstant.MALL_SSO_ACCESS_URL);
        //ObjectMapper objectMapper = new ObjectMapper();
        //response.setContentType("text/html;charset=UTF-8");
        //response.getWriter().write(objectMapper.writeValueAsString(checkAuthResponse));
        return false;
    }

    private boolean isAnoymous(HandlerMethod handlerMethod){
        Object bean = handlerMethod.getBean();
        Class clazz = bean.getClass();
        if(clazz.getAnnotation(Anoymous.class) != null){
            return true;
        }
        Method method = handlerMethod.getMethod();

        return method.getAnnotation(Anoymous.class) != null;
    }
}
