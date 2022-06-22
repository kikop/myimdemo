package com.kikop.myrestful.service;

import com.alibaba.fastjson.JSONObject;
import feign.Headers;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author kikop
 * @version 1.0
 * @project myauthserver
 * @file IRemoveUserService
 * @desc 需要代理的接口, 对接口中的所有方法整体代理
 * @date 2021/3/7
 * @time 10:30
 * @by IDE: IntelliJ IDEA
 */
public interface IRemoveUserService {

//    // say
//    // 通过@RequestLine指定访问方式及URL地址
//    @RequestLine("GET /mybusinessdemo/myrestful/say")
//    String say();
//
//    // getStrValue
//    // 字符串传参示例
//    @RequestLine("GET /mybusinessdemo/myrestful/getStrValue?userName={userName}")
//    String getStrValue(@Param(value = "userName") String userName);
//
//    // getObjectValue
//    // JSON对象传参示例
//    @Headers({"Content-Type: application/json", "Accept: application/json"})
//    @RequestLine("POST /mybusinessdemo/myrestful/getObjectValue")
//    JSONObject getObjectValue(JSONObject req);
//
//    // getRibbonValue
//    // 负载均衡访问示例
//    @Headers({"Content-Type: application/json", "Accept: application/json"})
//    @RequestLine("POST /mybusinessdemo/myrestful/getRibbonValue")
//    JSONObject getRibbonValue(JSONObject req);

    // login
    // contextPath:这里用了硬编码,正常的微服务可以不要
    // POST contextPath/requestMapping/method
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @RequestLine("POST /myuserserver/uc/userLogin")
    JSONObject userLogin(@RequestBody JSONObject reqParam);
}