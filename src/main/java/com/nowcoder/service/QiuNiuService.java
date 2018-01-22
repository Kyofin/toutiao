package com.nowcoder.service;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class QiuNiuService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QiuNiuService.class);
    private static final String ACCESS_KEY ="LELbaM8GR1voWe7ZAIIlQl11_ELdCHTzSvph9CDo";
    private static final String SECRET_KEY ="LaWydtvkqLxoOLs4Ti_ubvHUXH1km4Xu9isJY-T9";
    private static final String BUCKET="forest";
    private static final String IMAGE_HOST = "http://olcc65vo9.bkt.clouddn.com/";

    public static   String getImageHost()
    {
        return  IMAGE_HOST;
    }


    public String saveImageToQiuNiu(MultipartFile multipartFile) {
        //上传文件到七牛服务器
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone2());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = ACCESS_KEY;
        String secretKey = SECRET_KEY;
        String bucket = BUCKET;

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String fileName = UUID.randomUUID().toString().replace("-","");
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(multipartFile.getBytes(),fileName,upToken);
            System.out.println(JSONObject.toJSONString(response));
            //解析上传成功的结果
           /* DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);*/
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        //返回获取图片的url
        String fileUrl = QiuNiuService.IMAGE_HOST+fileName;

        return fileUrl;
    }

}
