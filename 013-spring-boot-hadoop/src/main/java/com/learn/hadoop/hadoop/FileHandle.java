package com.learn.hadoop.hadoop;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class FileHandle {

    private String filePath;
    private FileSystem fs;
    private static FileHandle fileHandle;

    public static FileHandle getInstance() throws InterruptedException, IOException, URISyntaxException {
        if(fileHandle==null){
            fileHandle=new FileHandle();
        }
        return fileHandle;
    }

    private FileHandle() throws InterruptedException, IOException, URISyntaxException {
        this.filePath="";
        getFs();
    }

    public void setFilePath(String filePath){
        this.filePath=filePath;
    }

    public FileSystem getFs() throws URISyntaxException, IOException, InterruptedException {
        if(fs!=null){
            return fs;
        }
        URI uri = new URI("hdfs://yqdata000:8020");
        Configuration configuration = new Configuration();
        configuration.set("dfs.client.use.datanode.hostname","true");
        configuration.set("dfs.replication","1");
        fs=FileSystem.get(uri,configuration,"hadoop");
        return fs;
    }

    public void setTearDown()throws Exception{
        if(fs!=null){
            fs.close();
        }
    }

    public int handle(String handelPath) throws IOException {
        Path path = new Path(handelPath);
        RemoteIterator<LocatedFileStatus> fileiterator=fs.listFiles(path,false);

        int r=0;
        while(fileiterator.hasNext()){
            LocatedFileStatus fileStatus=fileiterator.next();
            if(fileStatus==null){
                continue;
            }
            Path hdfsPath=fileStatus.getPath();

            boolean isDir=fileStatus.isDirectory();
            System.out.println("path:"+hdfsPath.toUri().getPath()+" isDirectory:"+isDir+"  "+hdfsPath.getParent());
            if(isDir){
                continue;
            }
            r=r+1;
            String parentPath=hdfsPath.getParent().toUri().getPath();
            String targerPathFile=parentPath+"/"+r+".txt";
            Path newFilePath = new Path(targerPathFile);

            fs.rename(hdfsPath,newFilePath);
        }
        return r;
    }

    public int execute(String date) throws Exception {
        if(StringUtils.isBlank(date)){
            return 0;
        }

        String handelPath=filePath+"/"+date;
        int r=this.handle(handelPath);

        this.setTearDown();

        return r;
    }

    public void rename(String srcPathStr,String dstPathStr) throws IOException {
        Path srcPath = new Path(srcPathStr);
        Path dstPath = new Path(dstPathStr);
        System.out.println(srcPath.toString()+"  "+dstPath.toString());
        fs.rename(srcPath,dstPath);
    }


    public FSDataInputStream getFile(String path) throws IOException {
        return fs.open(new Path(path));

    }

    public void writeFile(String path,String context) throws IOException {
        FSDataOutputStream fsout=fs.create(new Path(path),true);
        BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fsout));
        bw.write(context);
        bw.newLine();
        bw.close();
        fsout.close();
    }



}