package com.trs.gfetch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 数据相关配置
 * 
 * @author 北京拓尔思信息技术股份有限公司
 * @since slzs @ 2019年4月2日 下午9:33:54
 */
@Component("propertiesConfig")
@Slf4j
public class PropertiesConfig {

    @Value("${firefoxurl}")
    public String firefoxurl; // firefox安装地址

    @Value("${mqsenderurl}")
    public String mqsenderurl; // 发送mq地址

    @Value("${geckodriver}")
    public String geckodriver; // 驱动位置



}
