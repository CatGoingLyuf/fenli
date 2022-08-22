package com.lyuf.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * @Author lyuf
 * @Date 2021/8/4 14:18
 * @Version 1.0
 */
@Slf4j
@Aspect // FOR AOP
//@Order(-99) // 控制多个Aspect的执行顺序，越小越先执行
@Component
public class TestAspect {


    @Before("execution(* com.lyuf.controller.FileController.*(..))") //该FileController类下的所有方法
    public void beforeTest(JoinPoint point) throws Throwable {

        HttpServletResponse response = null;
        for (Object param : point.getArgs()) {
            if (param instanceof HttpServletResponse) {
                response = (HttpServletResponse) param;
            }
        }

        System.out.println(point.getTarget().toString());
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
       try {
           if (session.getAttribute("user") == null) {
               // response.sendRedirect("/login.html");
           }
       }catch (Exception e){
           System.out.println("用户未登录异常");
           log.error("用户未登录异常");
       }
        System.out.println(session.getAttribute("user"));

    }
}
