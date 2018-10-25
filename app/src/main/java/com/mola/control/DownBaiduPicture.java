package com.mola.control;

import android.content.Context;
import android.widget.Toast;

import com.mola.interfaces.OnDownLoadFinishListener;
import com.mola.utils.ImageDownloadUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DownBaiduPicture {
	static int BUFFERSIZE = 819200;
	static String UserAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
	static String baseUrl = "https://image.baidu.com/search/index?ct=&z=&tn=baiduimage&ipn=r&word=";
	static String pnUrl = "&pn=";
	static String connect = "&face=0&istype=2&ie=utf-8&oe=utf-8&cl=&lm=-1&st=-1&fr=&fmq=&ic=0&se=&sme=";
	static String widthUrl = "&width=";
	static String heightUrl = "&height=";
	private String key = null;
	private int pn = 0;
	private int width = 0;
	private int height = 0;
	private String file = null;
	private static int imageEachPage;
	private int flag = 0;
	//下载过程中的监听器
	private OnDownLoadFinishListener onDownLoadFinishListener;
	private Context mContext;
	private ImageDownloadUtils imageDownloadUtils;
	public DownBaiduPicture(String str,
							OnDownLoadFinishListener onDownLoadFinishListener
	){
		file = str;
		this.onDownLoadFinishListener=onDownLoadFinishListener;
		imageDownloadUtils=new ImageDownloadUtils();
	}
	//设置下载图片属性
	public void setPicture(String keyword, int page, int flg,
						   int wid, int hei, int imageEachPage, Context context){
		key = keyword;
		pn = page;
		width = wid;
		height = hei;
		flag = flg;
		mContext=context;
		this.imageEachPage=imageEachPage;
	}
	public static void addOnePic(){
		//获得一张失败图片后，所要爬载的图片加一
		imageEachPage=imageEachPage+1;
	}
	//深度爬取图片
	public void downLoad(DownloadTask dk){
		try {
			//爬载每一页的数据
			for (int i = 0; i < pn; i++) {
				//获取url链接
				String urlRes = baseUrl + key + pnUrl + (i * 30) + connect + widthUrl;
				urlRes += width == 0 ? "" : width;
				urlRes += height == 0 ? heightUrl : heightUrl + height;

				System.out.println(urlRes);
				Document document = null;
				//获得htmldocument数据——jsoup
				System.out.println("1########");
				document = Jsoup.connect(new String(urlRes.getBytes("utf-8")))
						.userAgent(UserAgent)
						.timeout(3000)
						.get();
				//转化为String类型的html标签,匹配正则式
				String str = document.toString();
				String reg = flag == 0 ? "thumbURL\":\"https://.+?\"" : "objURL\":\"http://.+?\"";
				Pattern pattern = Pattern.compile(reg);
				Matcher matcher = pattern.matcher(str);
				//创建存储文件夹
				//String pathname = file+"/"+key+"/"+i;
				//new File(pathname).mkdirs();
				int count = 0;
				while (matcher.find()) {
					count++;
					System.out.println("count是" + count + "imageEachPage是" + imageEachPage);
					if (count >= imageEachPage + 1) {
						break;
					}
					if (dk.isCancelled()) {
						System.out.println("已取消下载");
						break;
					}
					System.out.println("2########");
					int start = flag == 0 ? 11 : 9;
					//最终每张图片的url
					String findUrl = matcher.group().substring(start, matcher.group().length() - 1);
					String opn;
					int index;
					if ((index = findUrl.lastIndexOf(".")) != -1 &&
							(findUrl.substring(index).equals(".png") ||
									findUrl.substring(index).equals(".PNG") ||
									findUrl.substring(index).equals(".jif") ||
									findUrl.substring(index).equals(".GIF"))) {
						opn = count + findUrl.substring(index);
					} else {
						opn = count + ".jpg";
					}
					try {
						System.out.println(findUrl);
						System.out.println("3########");
						imageDownloadUtils.downLoadPic(findUrl, file + "/" + key, onDownLoadFinishListener);
						onDownLoadFinishListener.onFinishOnePage(count,imageEachPage+1);
						//downloadEach(findUrl, pathname+"/"+opn);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println(findUrl + "\terror");
						continue;
					}
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
