package com.learn.hadoop.controller;

import com.learn.hadoop.entity.LogResponse;
import com.learn.hadoop.hadoop.HDFSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hadoop")
public class HadoopController {

    @Autowired
    HDFSService hdfsService;

    @GetMapping("/mkdir")
    public LogResponse<String> mkdir(@RequestParam("dir") String path) throws Exception {
        boolean result = hdfsService.mkdir(path);
        System.out.println(result);
        return LogResponse.success("创建成功");
    }

    @GetMapping("/createFile")
    public LogResponse<String> createFile(@RequestParam("fileName") String fileName) throws Exception {
        hdfsService.createFile(fileName);
        return LogResponse.success("创建成功");
    }

    @GetMapping("/readFile")
    public LogResponse<String> readFile(@RequestParam("path") String path) throws Exception {
        String data = hdfsService.readFile(path);
        System.out.println("read:::" + data);
        return LogResponse.success("读取成功");
    }

    @DeleteMapping("/delete")
    public LogResponse<String> delete(@RequestParam("filePath") String filePath, boolean recursive) throws Exception {
        boolean result = hdfsService.delete(filePath, recursive);
        if (result) {
            return LogResponse.success("删除成功");
        } else {
            return LogResponse.success("删除失败");
        }
    }
}
