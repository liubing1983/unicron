package com.lb.unicron.java.jdbc;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * @ClassName DbConnection
 * @Description @TODO
 * @Author liubing
 * @Date 2021/11/2 11:30
 * @Version 1.0
 **/
public class DbConnection {

    private static String hostname;
    private static String port;
    private static String username;
    private static String passwd;
    private static String filename;
    private static Properties pro;


    private static Connection conn = null;

    public DbConnection(Properties pro) {
        this.pro = pro;
    }

    public DbConnection(String filename) {
        this.filename = filename;
    }


    public DbConnection(String hostname, String port, String username, String passwd) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.passwd = passwd;
    }

    public static Connection getMysqlConnection() {


        if(conn != null ) return conn;
        else {

        }
        return null;
    }

    public static Connection getDruidConnection(){

        try {
            DataSource ds = DruidDataSourceFactory.createDataSource(pro);
            conn = ds.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }

}
