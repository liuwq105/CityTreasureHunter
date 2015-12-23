package com.function.team;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.function.UrlIP.GetHeaderIP;

public class GetPointSign {

	public String[] doInBackground(String tid)
	{
		String json="";
		CharSequence teamid="";
		String[] rest=null;
		teamid=tid.toString();
		try{
			//Toast.makeText(a, "3", Toast.LENGTH_SHORT).show();
			HttpPost httpRequest =new HttpPost(HttpUtil.GETPOINTSIGN_URL);
			//��������
			
			//��������
			//ʹ��NameValuePair���������ֵ�ԣ�ʹ��NameValuePair������Ϊ������Ҫ���Ǹ���Ĳ���Ҫ��
			List<NameValuePair> params=new ArrayList<NameValuePair>();
		
			params.add(new BasicNameValuePair("tid",teamid.toString()));
			
			//���ύ���ݽ��б��룬���ñ��뷽ʽ
			httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			//Toast.makeText(a, "2", Toast.LENGTH_SHORT).show();
			GetHeaderIP ghp = new GetHeaderIP();
			httpRequest.setHeader("X-Online-Host", ""+ghp.GetIP()+":8080");
			httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			HttpResponse httpResponse=new DefaultHttpClient().execute(httpRequest);
				
			//�����Ӧ������������
			if(httpResponse.getStatusLine().getStatusCode()==200){
				//�����ֽ��������Ͱ�װ�İ�����
					
				try {
					json = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");  
						
					//result=json.toString()+""; 
				    //�����ǻ��json����Ľ���
					JSONArray jsonArray = new JSONObject(json).getJSONArray("strings"); 
				    rest=new String[2];
					//����ֱ��Ϊһ��������ʽ�����Կ���ֱ�� ��android�ṩ�Ŀ��JSONArray��ȡJSON���ݣ�ת����Array  
				    JSONObject item = ((JSONObject)jsonArray.opt(0)); 

					// ��ȡ�����Ӧ��ֵ      
					rest[0]= item.getString("username");   
					rest[1]= item.getString("corr");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //ÿ����¼���ɼ���Object������� 
			}	
		}
		catch(ClientProtocolException e){
			e.printStackTrace();
			
		}catch(UnsupportedEncodingException e){ 
			e.printStackTrace();
			//Toast.makeText(MainActivity.this, "2", Toast.LENGTH_SHORT).show();
			//Toast.makeText(FindpswdActivity, "2", Toast.LENGTH_SHORT).show();
		}catch (IOException e) {
			e.printStackTrace();
			//Toast.makeText(FindpswdActivity, "3", Toast.LENGTH_SHORT).show();
		}
		return rest;
	}	
}
