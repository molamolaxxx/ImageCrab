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

	private Boolean isContinue=true;
	public DownBaiduPicture(String str,
							OnDownLoadFinishListener onDownLoadFinishListener
	){
		file = str;
		this.onDownLoadFinishListener=onDownLoadFinishListener;
		imageDownloadUtils=new ImageDownloadUtils();
	}

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
	/**
	 * @param word
	 * @param page
	 * @param flg
	 */
	/**
	 * @param srcUrl
	 * @param outputFile
	 * @throws IOException
	 */
	//下载每一张图片并保存
	public void downloadEach(String srcUrl, String outputFile) throws IOException{
		System.out.println(srcUrl+"\t"+"start");
		URL url = new URL(srcUrl);
		URLConnection uc = url.openConnection();
		if(flag == 0){
			HttpsURLConnection hus = (HttpsURLConnection)uc;
			hus.setDoOutput(true);
			hus.setRequestProperty("User-Agent", UserAgent);
			hus.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
			hus.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			hus.setRequestProperty("Connection", "keep-alive");
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			try {
				bis = new BufferedInputStream(hus.getInputStream());
				bos = new BufferedOutputStream(new FileOutputStream(outputFile));
				byte[] temp = new byte[BUFFERSIZE];
				int count = 0;
				while((count = bis.read(temp)) != -1){
					bos.write(temp, 0, count);
					bos.flush();
				}
				System.out.println(srcUrl+"\t"+"end");
			}catch (IOException e) {
				System.out.println(srcUrl+"\t"+"error");
				errorFileDel(outputFile);
			}finally {
				bos.close();
				bis.close();
			}
			return;
		}
		HttpURLConnection huc = (HttpURLConnection)uc;
		huc.setDoOutput(true);
		huc.setRequestProperty("User-Agent", UserAgent);
		huc.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
		huc.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
		huc.setRequestProperty("Connection", "keep-alive");

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(huc.getInputStream());
			bos = new BufferedOutputStream(new FileOutputStream(outputFile));
			byte[] temp = new byte[BUFFERSIZE];
			int count = 0;
			while((count = bis.read(temp)) != -1){
				bos.write(temp, 0, count);
				bos.flush();
			}
			System.out.println(srcUrl+"\t"+"end");
		}catch (IOException e) {
			e.printStackTrace();
			System.out.println(srcUrl+"\t"+"error");
			//删掉损坏的文档
			errorFileDel(outputFile);
		}finally {
			bos.close();
			bis.close();
		}
	}
	public static void addOnePic(){
		//获得一张失败图片后，所要爬载的图片加一
		imageEachPage=imageEachPage+1;
	}
	//深度爬取图片

	public void downLoad() throws IOException{
		//爬载每一页的数据
		for(int i=0; i<pn; i++){
			//获取url链接
			String urlRes = baseUrl+key+pnUrl+(i*30)+connect+widthUrl;
			urlRes += width == 0? "": width;
			urlRes += height == 0? heightUrl : heightUrl + height;
			
			System.out.println(urlRes);
			Document document = null;
			//获得htmldocument数据——jsoup
			document = Jsoup.connect(new String(urlRes.getBytes("utf-8")))
							.userAgent(UserAgent)
							.get();
			//转化为String类型的html标签,匹配正则式
			String str = document.toString();
			String reg = flag == 0? "thumbURL\":\"https://.+?\"" : "objURL\":\"http://.+?\"" ;
			Pattern pattern = Pattern.compile(reg);
			Matcher matcher = pattern.matcher(str);
			//创建存储文件夹
			//String pathname = file+"/"+key+"/"+i;
			//new File(pathname).mkdirs();
			int count = 0;
			while(matcher.find()){
				count++;
				System.out.println("count是"+count+"imageEachPage是"+imageEachPage);
				if(count==imageEachPage+1) {
					break;
				}
				if(!isContinue){
					System.out.println("已取消下载");
					break;
				}
				int start = flag == 0? 11 : 9;
				//最终每张图片的url
				String findUrl = matcher.group().substring(start, matcher.group().length()-1);
				String opn;
				int index;
				if((index = findUrl.lastIndexOf("."))!=-1&&
						(findUrl.substring(index).equals(".png")||
						 findUrl.substring(index).equals(".PNG")||
						 findUrl.substring(index).equals(".jif")||
						 findUrl.substring(index).equals(".GIF"))){
					opn = count + findUrl.substring(index);
				}
				else{
					opn = count + ".jpg";
				}
				try {
					System.out.println(findUrl);
					imageDownloadUtils.downLoadPic(findUrl,file+"/"+key,onDownLoadFinishListener);
					onDownLoadFinishListener.onFinishOnePage();
					//downloadEach(findUrl, pathname+"/"+opn);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(findUrl+"\terror");
					continue;
				}
			}
		}
	}
	/**
	 * outputFile
	 */
	public void setIsContinue(Boolean b){
		isContinue=b;
	}
	public static void errorFileDel(String outputFile){
		File errorFile = new File(outputFile);
		if(errorFile.exists()){
			errorFile.delete();
		}
	}
}
