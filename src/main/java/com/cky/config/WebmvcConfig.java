package com.cky.config;

import com.cky.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @ClassName WebmvcConfig
 * @Description TODO
 * @Author lukcy
 * @Date 2024/5/29 9:40
 * @Version 1.0
 */
//默认能访问类路径下的static和template文件夹 这里我们没有放在这些文件夹下 所以访问不到，需要自己配置静态资源处理器
@Configuration
@Slf4j
public class WebmvcConfig extends WebMvcConfigurationSupport {
    /**
     * 静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 增加消息转换器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("配置的消息转换器....");
        MappingJackson2HttpMessageConverter mappingJackson2CborHttpMessageConverter=new MappingJackson2HttpMessageConverter();
        mappingJackson2CborHttpMessageConverter.setObjectMapper(new JacksonObjectMapper());
        converters.add(0,mappingJackson2CborHttpMessageConverter);
    }
}
