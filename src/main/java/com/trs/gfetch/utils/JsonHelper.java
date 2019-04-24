package com.trs.gfetch.utils;

import com.alibaba.fastjson.JSON;

public class JsonHelper {
    /**
     * @param jsonStr {"name":"ll","age":"20","sex":"1"}
     * @param clazz Task.class
     * @return
     */
    public static Object parseJson2Object(String jsonStr,Class clazz){
        return JSON.toJavaObject(JSON.parseObject(jsonStr), clazz);
    }
}
