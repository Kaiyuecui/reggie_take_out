package com.cky.filter;

import com.alibaba.fastjson.JSON;
import com.cky.common.MythreadLocal;
import com.cky.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName MyloginFilter
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/20 20:08
 * @Version 1.0
 */
@Slf4j
@WebFilter(filterName = "loginChcekFilter",urlPatterns = "/*")
public class MyloginFilter implements Filter {
    //路径匹配器，支持通配符
    public   static  final  AntPathMatcher PATH_MATCHER=new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest=(HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse=(HttpServletResponse) servletResponse;
        String requestUri=httpServletRequest.getRequestURI();
        //不需要过滤的页面和内容
        String[] urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        //检查当前是否不需要拦截
        boolean check = check(urls, requestUri);
        if(check){
            log.info("{}不需要拦截",requestUri);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        //判断登录功能
        System.out.println("---------");
        System.out.println(httpServletRequest.getSession().getAttribute("employee"));
        if (httpServletRequest.getSession().getAttribute("employee")!=null) {
            log.info("{} 已经登陆",requestUri);
            Long id= (Long) httpServletRequest.getSession().getAttribute("employee");
            MythreadLocal.setCurrendId(id);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        if (httpServletRequest.getSession().getAttribute("user")!=null) {
            log.info("{} 已经登陆",requestUri);
            Long id= (Long) httpServletRequest.getSession().getAttribute("user");
            MythreadLocal.setCurrendId(id);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }

        httpServletResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

        }
        public boolean check(String[] urls,String requestURI){
            for (String url : urls) {
                boolean match = PATH_MATCHER.match(url, requestURI);
                if(match){
                    log.info("Matching {} with {}: {}", requestURI, url, match);

                    return true;
            }}
            return false;
        }
}
