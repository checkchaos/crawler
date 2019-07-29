package com.zarry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.scene.media.VideoTrack;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
//wkbjcloudbos.bdimg.com
public class WenKuDocParse {

    private static String url = "https://wkbjcloudbos.bdimg.com/v1/docconvert4166/wk/424083af3640849a1aa97ab57129afb8/0.json?responseContentType=application%2Fjavascript&responseCacheControl=max-age%3D3888000&responseExpires=Thu%2C%2012%20Sep%202019%2017%3A09%3A54%20%2B0800&authorization=bce-auth-v1%2Ffa1126e91489401fa7cc85045ce7179e%2F2019-07-29T09%3A09%3A54Z%2F3600%2Fhost%2F451c7f721961cc892277482dca3a16a04596f327857ef70ebccb9f8af31699fa&x-bce-range=0-14114&token=eyJ0eXAiOiJKSVQiLCJ2ZXIiOiIxLjAiLCJhbGciOiJIUzI1NiIsImV4cCI6MTU2NDM5NDk5NCwidXJpIjp0cnVlLCJwYXJhbXMiOlsicmVzcG9uc2VDb250ZW50VHlwZSIsInJlc3BvbnNlQ2FjaGVDb250cm9sIiwicmVzcG9uc2VFeHBpcmVzIiwieC1iY2UtcmFuZ2UiXX0%3D.8PqUZKc89NsmV1TiK%2BwIuanAGR4utg7yqEvBldLURI0%3D.1564394994";
    private boolean fastTitle = true;
    private double y;
    private XWPFDocument document;
    private String fileName;
    private JSONArray urls;
    private FileOutputStream out;
    private Runtime rt;
    private static String codeJs = Constant.parentPath + "js/docParse.js";
    private static String phantomjs = Constant.parentPath + "soft/phantomjs.exe";


    public void init() throws IOException {
        readJson();
        document = new XWPFDocument();
        out = new FileOutputStream(new File(fileName));
        rt = Runtime.getRuntime();
    }

    public static void main(String[] args) throws Exception {
        WenKuDocParse parse = new WenKuDocParse();
        parse.init();
        parse.run(parse);
    }

    public void run(WenKuDocParse parse) throws Exception {
        for (Object url : urls) {
            parse.getConn(MapUtils.getString((JSONObject)url, "url"));
        }
        document.write(out);
        document.close();
        parse.close(null, out);
    }

    public void readJson() throws IOException {
        String parentPath = Constant.parentPath;
        ClassPathResource resource = new ClassPathResource("json/urls.json");
        File file = resource.getFile();
        String jsonString = FileUtils.readFileToString(file);
        JSONObject json = JSONObject.parseObject(jsonString);
        urls = (JSONArray) json.get("urls");
        fileName = (String) json.get("fileName");
    }

    public void getConn(String url) throws Exception {
        InputStream is = null;
        BufferedReader reader = null;
        try {
            String exec = phantomjs + " " + codeJs + " " + url;
            Process exeResult = rt.exec(exec);
            is = exeResult.getInputStream();
            StringBuilder builder = new StringBuilder(2048);
            reader = new BufferedReader(new InputStreamReader(is));
            String tem;
            while ((tem = reader.readLine()) != null) {
                builder.append(tem);
            }

            Document document = Jsoup.parse(builder.toString());
            Element pre = document.getElementsByTag("pre").get(0);
            String val = pre.childNode(0).toString();
            val = val.substring(val.indexOf("{"), val.lastIndexOf("}") + 1);
            JSONArray bodys = JSONObject.parseObject(val).getJSONArray("body");
            writeBodys(bodys);
        } finally {
            close(is, null);
            if (reader != null) {
                reader.close();
            }
        }

    }

    public void close(InputStream in, OutputStream ou) {
        try {
            if (in != null) {
                in.close();
            }
            if (ou != null) {
                ou.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeBodys(JSONArray bodys) throws Exception {
        //Blank Document
        StringBuilder builder = new StringBuilder(512);
        for (Object body : bodys) {
            String font_size = MapUtils.getString(MapUtils.getMap((JSONObject) body, "s"), "font-size");
            if (MapUtils.getString((JSONObject) body, "c").equals(" ")) {
                continue;
            }
            if (fastTitle && StringUtils.isNotBlank(font_size)) {
                writeTitle(document, MapUtils.getString((JSONObject) body, "c"));
                fastTitle = false;
                nextLine(document);
                continue;
            }
            //如果y不相同则换行
            if (y != MapUtils.getDoubleValue(MapUtils.getMap((JSONObject) body, "p"), "y") && StringUtils.isNotBlank(builder.toString())) {
                writeBody(document, builder.toString().replaceAll(" ", ""));
                builder.setLength(0);
                builder.append(MapUtils.getString((JSONObject) body, "c"));
            } else {
                builder.append(MapUtils.getString((JSONObject) body, "c"));
            }
            y = MapUtils.getDoubleValue(MapUtils.getMap((JSONObject) body, "p"), "y");
        }
        if (builder.toString().length() > 0) {
            writeBody(document, builder.toString().replaceAll(" ", ""));
        }
    }

    public void writeTitle(XWPFDocument document, String text) {
        //添加标题
        XWPFParagraph titleParagraph = document.createParagraph();
        //设置段落居中
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleParagraphRun = titleParagraph.createRun();
        titleParagraphRun.setText(text);
        titleParagraphRun.setColor("000000");
        titleParagraphRun.setFontSize(16);
    }

    public void writeBody(XWPFDocument document, String text) {
        //段落
        XWPFParagraph firstParagraph = document.createParagraph();
        XWPFRun run = firstParagraph.createRun();
        run.setText(text);
        run.setColor("0000");
        run.setFontSize(10);
    }

    public void nextLine(XWPFDocument document) {
        //换行
        XWPFParagraph paragraph1 = document.createParagraph();
        XWPFRun paragraphRun1 = paragraph1.createRun();
        paragraphRun1.setText("\r");
    }
}
