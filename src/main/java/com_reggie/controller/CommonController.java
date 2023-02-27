package com_reggie.controller;

import com_reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.ietf.jgss.Oid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@RequestMapping("/common")
@RestController
@Slf4j
public class CommonController {

    //在配置文件中关联着
    @Value("${reegie.path}")
    private String basePath;

    /**
     * 文件上传
     *  注意事项：
     *      1、形参中的方法名不可以随便写，需要跟前端传过来的一致才可以
     * @return
     */
    @PostMapping("/upload")
    private R<String> upload(MultipartFile file) {
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件就会删除

        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        //获取文件名后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //随机生成文件，名使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID()+suffix;
        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if (!dir.exists()) {
            //目录不存在，需要创建
            dir.mkdirs();
        }

        try {
            //将临时文件转存到指定位置，目录加上文件名
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.  printStackTrace();
        }

        /**
         * 问题：为什么返回给文件名名称？
         * 答案：上传完成之后需要给页面返回，需要新增的时候需要把文件名称存到数据库中
         *
         */
        return R.success(fileName);
    }

    /**
     * 文件下载：就是在文件上传的地方显示上传的图片，也可以用到其他地方
     *   不需要返回值，因为不向浏览器发送什么提示信息
     * @param name
     * @param response
     */
    @GetMapping("/download")
    private void download(String name,HttpServletResponse response) {

        try {
            //1、输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            //2、输出流，通过输出流将文件写回浏览器，在浏览器展示图片了
            ServletOutputStream outputStream = response.getOutputStream();

            //设置返回的什么类型的图片，image/jpeg代表是图片文件
            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            //将读到的内容放入byte数组中，不等于-1等于没有读完，直到-1才是读完了
            while ((len = fileInputStream.read(bytes)) !=-1) {
                //通过输出流，向浏览器写
                outputStream.write(bytes,0,len);
                //刷新
                outputStream.flush();
            }
            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
