package com.lb.unicron.java.fileutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * @ClassName ConfigFiles
 * @Description @TODO
 * @Author liubing
 * @Date 2021/11/2 14:09
 * @Version 1.0
 **/
public class ConfigFiles {

    static Logger log = LoggerFactory.getLogger(ConfigFiles.class);

    /**
     * 得到properties文件
     * @param filename
     * @return
     */
    public  Properties getProperties(String filename) {
        Properties props = new Properties();
        log.info("配置文件: " + filename);
        try {
            props.load(ConfigFiles.class.getClassLoader().getResourceAsStream(filename));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return props;
    }

    public static void main(String[] args) throws Exception{
       // String s = ConfigFiles.getProperties("test.properties").getProperty("project");
       // System.out.println(s);
    }
}
