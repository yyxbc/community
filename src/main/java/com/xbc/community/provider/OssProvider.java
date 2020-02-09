package com.xbc.community.provider;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import com.xbc.community.exception.CustomizeErrorCode;
import com.xbc.community.exception.CustomizeException;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.util.Date;

@Component
public class OssProvider {
    String endpoint = "http://oss-cn-beijing.aliyuncs.com";
    // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
    String accessKeyId = "LTAI4Fw93y64DFdE3CWkhcrg";
    String accessKeySecret = "ROoGgDdjJBdgGdq88g26inSBlIho82";
    String bucketName = "xbc1";
    String objectName = "xbc1";
    // 创建OSSClient实例。
    OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

    public String upload(InputStream inputStream, String originalFilename) {
        String url="";
        try {
            byte[] buff = new byte[8000];
            int bytesRead = 0;
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buff)) != -1) {
                bao.write(buff, 0, bytesRead);
            }
            inputStream.close();
            ossClient.putObject(bucketName, originalFilename, new ByteArrayInputStream(bao.toByteArray()));
            Date expiration = new Date(new Date().getTime() + 3600 * 1000*24*365);
            // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
            URL imgurl = ossClient.generatePresignedUrl(bucketName, originalFilename, expiration);
            url=imgurl.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        }
// 关闭OSSClient。
        ossClient.shutdown();
        return url;
    }

    // 调用ossObject.getObjectContent获取文件输入流，可读取此输入流获取其内容。
    public String download(String Name) {
        // 调用ossClient.getObject返回一个OSSObject实例，该实例包含文件内容及文件元信息。
        objectName = Name;
        ossClient.getObject(new GetObjectRequest(bucketName, objectName), new File(Name));
//        InputStream content = ossObject.getObjectContent();
//        String line = null;
//        try {
//            if (content != null) {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
//                while (true) {
//                    line = reader.readLine();
//                    if (line == null) break;
//                    System.out.println("\n" + line);
//                    System.out.println(1);
//                }
//                // 数据读取完成后，获取的流必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
//                content.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

// 关闭OSSClient。
        ossClient.shutdown();
        return "";
    }
}
