package com.learn.hadoop.hadoop;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

@Component
public class HDFSService {

    @Value("${hadoop.user}")
    private String user;

    @Value("${hadoop.uri}")
    private String uri;

    @Value("${hadoop.apiDir}")
    private String apiDir;

    /**
     * 获取HDFS配置信息
     *
     * @return
     */
    private Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.set("dfs.client.use.datanode.hostname", "true");
        configuration.set("dfs.replication", "1");
        //configuration.set("dfs.datanode.address","www.rui.hadoop.com:50010");
        return configuration;
    }

    /**
     * 获取HDFS文件系统对象
     *
     * @return
     * @throws Exception
     */
    public FileSystem getFileSystem() throws Exception {
        return FileSystem.get(new URI(uri), getConfiguration(), user);
    }

    /**
     * 在HDFS创建文件夹
     *
     * @param path
     * @return
     * @throws Exception
     */
    public boolean mkdir(String path) throws Exception {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        FileSystem fs = getFileSystem();
        // 目标路径
        Path srcPath = new Path(path);
        boolean isOk = fs.mkdirs(srcPath);
        fs.close();
        return isOk;
    }

    // 创建文件
    public void createFile(String fileName) throws Exception {
        FileSystem fs = getFileSystem();
        Path path = new Path(fileName);
        FSDataOutputStream out = fs.create(path);
        out.writeUTF("hello");
        out.flush();
        out.close();
    }

    /**
     * 读取HDFS文件内容
     *
     * @param path
     * @return
     * @throws Exception
     */
    public String readFile(String path) throws Exception {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        FileSystem fs = getFileSystem();
        // 目标路径
        Path srcPath = new Path(path);
        FSDataInputStream inputStream = null;
        try {
            inputStream = fs.open(srcPath);
            // 防止中文乱码
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String lineTxt = "";
            StringBuffer sb = new StringBuffer();
            while ((lineTxt = reader.readLine()) != null) {
                sb.append(lineTxt);
            }
            return sb.toString();
        } finally {
            inputStream.close();
            fs.close();
        }
    }

}

