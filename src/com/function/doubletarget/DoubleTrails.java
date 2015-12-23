package com.function.doubletarget;
//������Ĳ���
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.function.UrlIP.GetHeaderIP;

import android.os.AsyncTask;

@SuppressWarnings("rawtypes")
public class DoubleTrails extends AsyncTask{
	protected void onPreExecute() {
		//FindpswdActivity.progressDialogfind.show();
	}

	protected Object doInBackground(Object... params_obj)
	{
		String result="";
		CharSequence sid="";
		CharSequence uid="";
		CharSequence slat="";
		CharSequence slon="";
		sid=params_obj[0].toString();
		uid=params_obj[1].toString();
		slat=params_obj[2].toString();
		slon=params_obj[3].toString();
		
		try{
			HttpPost httpRequest =new HttpPost(HttpUtil.DOUBLETRAIL_URL);
			if(!sid.equals("")&&!uid.equals("")&&!slat.equals("")&&!slon.equals("")){
			
				//��������	
				//��������
				//ʹ��NameValuePair���������ֵ�ԣ�ʹ��NameValuePair������Ϊ������Ҫ���Ǹ���Ĳ���Ҫ��
				List<NameValuePair> params=new ArrayList<NameValuePair>();
			
				//s_id,s_name,u_id,s_lat,s_lon,s_starttime,s_lasttime,
				//s_limatetime,s_total,s_describe,s_limatenumber
				params.add(new BasicNameValuePair("sid",sid.toString()));
				params.add(new BasicNameValuePair("uid",uid.toString()));
				params.add(new BasicNameValuePair("ulat",slat.toString()));
				params.add(new BasicNameValuePair("ulon",slon.toString()));
				//params.add(new BasicNameValuePair("flag","0"));
 
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
					byte[] data =new byte[2048];
					//�ȰѴӷ���˴���������ת��Ϊ�ֽ�����
					
					data =EntityUtils.toByteArray((HttpEntity)httpResponse.getEntity());
					
					//�ٴ����ֽ���������������
					ByteArrayInputStream bais = new ByteArrayInputStream(data);
					
					//���ֽ��������ݰ���
					DataInputStream dis = new DataInputStream(bais);
					//Toast.makeText(FindpswdActivity,"!!!", Toast.LENGTH_SHORT).show();
					//���ֽ������е����ݻ�ԭ��ԭ���ĸ����������ͣ��������£�
					//result="!!!!";
					result =new String(dis.readUTF());
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
		return result;
	}
	protected void onPostExecute(Object result) {
	}	
}