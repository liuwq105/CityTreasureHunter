package com.function.buildact;
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

public class CreatCheckpoint{
	public String doInBackground(Object... params_obj){
		String result="";
		CharSequence pro="";//��Ϊ�ж��ĸ�sport��
		CharSequence sports="";//�ж���Ϻ�ֵ
		
		CharSequence cquestion="";
		CharSequence canswer="";
		CharSequence clat="";
		CharSequence clon="";
		//��������Ϊsport���ȫ����Ϣ
		pro=params_obj[0].toString();
		if(pro.equals("������"))
			sports="sport3";
		if(pro.equals("������"))
			sports="sport2";
		if(pro.equals("˫�˻���"))
			sports="sport1";
		cquestion=params_obj[1].toString();
		canswer=params_obj[2].toString();
		clat=params_obj[3].toString();
		clon=params_obj[4].toString();
		try	{
			HttpPost httpRequest =new HttpPost(HttpUtil.CREATCHECKPOINT_URL);
			if(!pro.equals("")&&!cquestion.equals("")&&!canswer.equals("")
				&&!clat.equals("")&&!clon.equals("")){
		
				List<NameValuePair> params=new ArrayList<NameValuePair>();
					
				params.add(new BasicNameValuePair("sports",sports.toString()));
				params.add(new BasicNameValuePair("cquestion",cquestion.toString()));
				params.add(new BasicNameValuePair("canswer",canswer.toString()));
				params.add(new BasicNameValuePair("clat",clat.toString()));
				params.add(new BasicNameValuePair("clon",clon.toString()));
		
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

