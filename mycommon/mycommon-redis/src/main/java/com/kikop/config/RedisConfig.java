package com.kikop.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.kikop.serialize.FastJson2JsonRedisSerializer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis配置
 *
 * @author ruoyi
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * 注入 RedisTemplate
     * 默认 RedisTemplate,key:String,value:Object
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<Object, Object> template = new RedisTemplate<>();

        // 1.setConnectionFactory
        template.setConnectionFactory(redisConnectionFactory);

        // 2.构建 fastJson2JsonRedisSerializer
        FastJson2JsonRedisSerializer fastJson2JsonRedisSerializer = new FastJson2JsonRedisSerializer(Object.class);

        // 2.1.自定义 ObjectMapper

        // 引入:com.fasterxml.jackson.databind-v2.13.1,保存储到 redis里的数据将是有类型的纯json
        // 功能:解决 Redis对象 Object 转为 Jsonobject类型错误
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // ObjectMapper.DefaultTyping.NON_FINAL:避免存储到 Redis里的数据将是没有类型的纯json
        // 这个方法已经被弃用
        // objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);

        // 2.2.设置setObjectMapper
        fastJson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 设置Hash存储的序列化
        // Spring Data Redis（一下简称SDR）,SDR序列化方式有多种
        // StringRedisSerializer、JdkSerializationRedisSerializer、Jackson2JsonRedisSerializer、OxmSerializer

        // 2.2.for string key 序列化和反序列化
        // redis的key(StringRedisSerializer)、value(FastJson2JsonRedisSerializer)
        template.setKeySerializer(new StringRedisSerializer());
        // fro string value序列化
        template.setValueSerializer(fastJson2JsonRedisSerializer);

        // 2.3.for hash key 序列化和反序列化
        // hash的key(StringRedisSerializer)、value(FastJson2JsonRedisSerializer)
        template.setHashKeySerializer(new StringRedisSerializer());
        // for hash value 序列化
        template.setHashValueSerializer(fastJson2JsonRedisSerializer);

        // 2.4.初始化
        template.afterPropertiesSet();

        return template;
    }
}
