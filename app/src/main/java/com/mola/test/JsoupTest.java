package com.mola.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
//jsoup测试模块
public class JsoupTest {
    private String URL="https://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=-1&st=-1&fm=result&fr=&sf=1&fmq=1540432889282_R&pv=&ic=0&nc=1&z=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&word=%E5%88%9D%E9%9F%B3%E6%9C%AA%E6%9D%A5";
    @Test
    public void test() throws IOException{
        Document mDocument=Jsoup.connect(URL).get();
        //System.out.println(mDocument);
        Elements imgElements=mDocument.select("img.main_img");
        System.out.println(imgElements.first());

    }
}

