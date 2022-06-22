package com.kikop.annotation;

import com.kikop.config.ApplicationConfig;
import com.kikop.feign.FeignAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.annotation.*;

//import org.mybatis.spring.annotation.MapperScan;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
// 表示通过aop框架暴露该代理对象,AopContext能够访问
@EnableAspectJAutoProxy(exposeProxy = true)
// 指定要扫描的 Mapper类的包的路径 todo
//@MapperScan("com.kikop.**.mapper")
// 开启线程异步执行
@EnableAsync

// 自动加载类
// 系统时区配置
// 传递用户信息请求头，防止丢失,为什么还要 Import todo
@Import({ApplicationConfig.class, FeignAutoConfiguration.class})
public @interface EnableCustomConfig {

}
