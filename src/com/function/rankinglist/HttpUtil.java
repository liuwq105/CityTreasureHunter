package com.function.rankinglist;

import com.function.UrlIP.UrlIP;

public class HttpUtil {
	static UrlIP ip = new UrlIP();

	public static final String GETACTIVITY_URL=ip.GetUtilIP()+"CityTreasureHunterServlet/servlet/GetActivityServlet";
	//�����������Ϣ
	public static final String GETPERSONAL_URL=ip.GetUtilIP()+"CityTreasureHunterServlet/servlet/GetpersonalServlet";
    //��õ�������Ϣ
	public static final String GETRECORD_URL=ip.GetUtilIP()+"CityTreasureHunterServlet/servlet/GetrecordServlet";
	public static final String IFONLINE_URL=ip.GetUtilIP()+"CityTreasureHunterServlet/servlet/IfOnlineServlet";
}
