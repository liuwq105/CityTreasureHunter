package com.function.singlecontext;

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

public class GetSingleRecord{

	public String[] doInBackground(Object... params_obj)
	{
		String json="";
		CharSequence uid="";//������1����
		CharSequence sid="";//������1����
		CharSequence sign="";
		String[] rest=null;
		uid=params_obj[0].toString();
		sid=params_obj[1].toString();
		sign=params_obj[2].toString();
		
		try{
			HttpPost httpRequest =new HttpPost(HttpUtil.SINGLEORTEAMRECORD_URL);
			if(!sid.equals("")&&!sign.equals("")){			
				//��������
			
				//��������
				//ʹ��NameValuePair���������ֵ�ԣ�ʹ��NameValuePair������Ϊ������Ҫ���Ǹ���Ĳ���Ҫ��
				List<NameValuePair> params=new ArrayList<NameValuePair>();
		
				params.add(new BasicNameValuePair("uid",uid.toString()));
				params.add(new BasicNameValuePair("tid","-1"));
				params.add(new BasicNameValuePair("sid",sid.toString()));
				params.add(new BasicNameValuePair("sign",sign.toString()));

				//���ύ���ݽ��б��룬���ñ��뷽ʽ
				httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
				GetHeaderIP ghp = new GetHeaderIP();
				httpRequest.setHeader("X-Online-Host", ""+ghp.GetIP()+":8080");
				httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
				HttpResponse httpResponse=new DefaultHttpClient().execute(httpRequest);
				//Toast.makeText(FindpswdActivity, "!!!", Toast.LENGTH_SHORT).show();
				//�����Ӧ������������
				if(httpResponse.getStatusLine().getStatusCode()==200){
					//�����ֽ��������Ͱ�װ�İ�����
					try {	
						json = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");  
						JSONArray jsonArray = new JSONObject(json).getJSONArray("strings"); 
						rest=new String[3];
						//����ֱ��Ϊһ��������ʽ�����Կ���ֱ�� ��android�ṩ�Ŀ��JSONArray��ȡJSON���ݣ�ת����Array    
					    	JSONObject item = ((JSONObject)jsonArray.opt(0)); 
					        // ��ȡ�����Ӧ��ֵ      
							rest[0]= item.getString("sname");  
			                rest[1]= item.getString("sspendtime");
							rest[2]= item.getString("checknumber");  
		            
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} //ÿ����¼���ɼ���Object������� 
				}
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
