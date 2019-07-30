package com.zarry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Main.class);

        try {
            FileInputStream is = new FileInputStream(Constant.getParentPath(Main.class) + "/soft/config.properties");
            Properties properties = new Properties();
            properties.load(is);
            if (StringUtils.isNotBlank(properties.getProperty("host"))) {
                Config.proxy(properties.getProperty("host"), properties.getProperty("port"));
                logger.error("进入到proxy");
            }
            if (StringUtils.isNotBlank(properties.getProperty("catalina.home"))) {
                System.getProperties().setProperty("catalina.home", properties.getProperty("catalina.home"));
                logger.error("进入到catalina.home");
            }
            if (properties.getProperty("downType").equals("ppt")) {
                new WenKuPptParse().getConn(properties.getProperty("pptUrl"), properties.getProperty("readDir"), properties.getProperty("writeFile"));
            } else if (properties.getProperty("downType").equals("docx")) {
                WenKuDocParse parse = new WenKuDocParse();
                parse.run(parse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
