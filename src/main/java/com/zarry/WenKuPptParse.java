package com.zarry;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class WenKuPptParse {
    private Logger logger = Logger.getLogger(WenKuPptParse.class);
    private static String SUFFIX = ".jpg";
    private static AtomicInteger NUM = new AtomicInteger();
    private static String PATH = "/";
    private boolean fastCreat = true;
    private String codeJs;
    private String phantomjs;
    private Runtime rt;

    public WenKuPptParse() {
        NUM.incrementAndGet();
        init();
    }

    public void init() {
        codeJs = (Constant.getParentPath(WenKuPptParse.class) + "/soft/code.js");
        phantomjs = (Constant.getParentPath(WenKuPptParse.class) + "/soft/phantomjs.exe");
        rt = Runtime.getRuntime();
    }

//    public static void main(String[] args) throws Exception {
//       new WenKuPptParse().getConn(url);
//    }

    public void getConn(String url, String readDir,String writeFile) throws Exception {
        String exec = phantomjs + " " + codeJs + " " + url;
        Process exeResult = rt.exec(exec);
        InputStream is = exeResult.getInputStream();
        StringBuilder builder = new StringBuilder(2048);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String tem;
        while ((tem = reader.readLine()) != null) {
            builder.append(tem);
        }

        Document document = Jsoup.parse(builder.toString());
        Elements imgs = document.getElementsByTag("img");
        ArrayList<String> imgUrls = new ArrayList<>();
        int fileName = 0;
        logger.error("==================="+ imgs.size());
        for (Element img : imgs) {
            String attr = img.attr("data-src");
            if (StringUtil.isBlank(attr)) {
                attr = img.attr("src");
            }
            fileName++;
            downImg(attr, fileName);
            imgUrls.add(attr);
        }
        if (imgs != null && imgs.size()>0){
            PictureToPDF.toPdf(readDir,writeFile);
        }
    }

    public void downImg(String url, int fileName) throws Exception {
        if (StringUtil.isBlank(url) || !url.startsWith("http")) {
            return;
        }
        String str = Constant.getParentPath(WenKuPptParse.class) + PATH + NUM + PATH + fileName + SUFFIX;
        File file = new File(str);
        if (fastCreat && !file.getParentFile().exists()) {
            fastCreat = false;
            file.getParentFile().mkdirs();
        }
        BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        //缓冲字节数组
        byte[] data = new byte[1024];
        int length;
        while ((length = in.read(data)) != -1) {
            out.write(data, 0, length);
            out.flush();
        }
        System.out.println("正在执行下载任务：当前正在下载图片" + str);
        in.close();
        out.close();
    }
}
