package com.function.joinact;

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
import android.widget.EditText;

public class GetTeamid {
	
	public String doInBackground(Object... params_obj)
	{
		String result="";
		CharSequence cid="";
		CharSequence tdescri="";
		cid=params_obj[0].toString();
		tdescri=params_obj[1].toString();
		try{
			HttpPost httpRequest =new HttpPost(HttpUtil.GETTEAMID_URL);
		if(!tdescri.equals(""))
		{                                                 //&&!sdescribe.equals("")
			
			//��������
			
			//��������
			//ʹ��NameValuePair���������ֵ�ԣ�ʹ��NameValuePair������Ϊ������Ҫ���Ǹ���Ĳ���Ҫ��
			List<NameValuePair> params=new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("cid",cid.toString()));
			params.add(new BasicNameValuePair("tdescribe",tdescri.toString()));
			//params.add(new BasicNameValuePair("flag","0"));
			
			
			    
			  //���ύ���ݽ��б��룬���ñ��뷽ʽ
				httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
				GetHeaderIP ghp = new GetHeaderIP();
				httpRequest.setHeader("X-Online-Host", ""+ghp.GetIP()+":8080");
				httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
				HttpResponse httpResponse=new DefaultHttpClient().execute(httpRequest);
				//Toast.makeText(FindpswdActivity, "!!!", Toast.LENGTH_SHORT).show();
				//�����Ӧ������������
				if(httpResponse.getStatusLine().getStatusCode()==200)
				{
					
					//�����ֽ��������Ͱ�װ�İ�����
					byte[] data =new byte[2048];
					//�ȰѴӷ���˴���������ת��Ϊ�ֽ�����
					
					data =EntityUtils.toByteArray((HttpEntity)httpResponse.getEntity());
					
					//�ٴ����ֽ���������������
					ByteArrayInputStream bais = new ByteArrayInputStream(data);
					
					//���ֽ��������ݰ���
					DataInputStream dis = new DataInputStream(bais);
					result =new String(dis.readUTF());
				}
				
				
			}
		
	}
		catch(ClientProtocolException e){
			e.printStackTrace();
			
		}catch(UnsupportedEncodingException e){ 
			e.printStackTrace();
			
		}catch (IOException e) {
			e.printStackTrace();
		}
		return result;

}

}