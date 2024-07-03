package com.cky.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName myConfig
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/22 11:03
 * @Version 1.0
 */
@Configuration
public class myConfig {
    @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor(){
      MybatisPlusInterceptor mybatisPlusInterceptor=new MybatisPlusInterceptor();
      mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
      return mybatisPlusInterceptor;
  }
}
