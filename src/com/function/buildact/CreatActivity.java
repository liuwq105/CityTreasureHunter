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

public class CreatActivity{
	public String doInBackground(Object... params_obj){
		String result="";
		CharSequence pro="";//��Ϊ�ж��ĸ�sport��
		CharSequence sports="";//�ж���Ϻ�ֵ
		
		CharSequence sname="";
		CharSequence uid="";
		CharSequence slat="";
		CharSequence slon="";
		CharSequence slasttime="";
		CharSequence slitimeh="";
		CharSequence slitimemin="";
		CharSequence stotal="";
		CharSequence sdescribe="";
		CharSequence slinum="";
		//��������Ϊsport���ȫ����Ϣ
		pro=params_obj[0].toString();
		
		//sid=params_obj[1].toString();
		sname=params_obj[1].toString();
		uid=params_obj[2].toString();
		slat=params_obj[3].toString();
		slon=params_obj[4].toString();
		slasttime=params_obj[5].toString();
		slitimeh=params_obj[6].toString();
		slitimemin=params_obj[7].toString();
		
		String staslitime=slitimeh+":"+slitimemin+":00";
		stotal=params_obj[8].toString();
		sdescribe=params_obj[9].toString();
		slinum=params_obj[10].toString();
		
		if(pro.equals("������"))
			sports="sport3";
		if(pro.equals("������"))
			sports="sport2";
		if(pro.equals("˫�˻���")){
			sports="sport1";
			stotal="1";
		}
		
		try{
			HttpPost httpRequest =new HttpPost(HttpUtil.CREATGAME_URL);
			if(!sname.equals("")&&!uid.equals("")&&!slat.equals("")
				&&!slon.equals("")&&!slasttime.equals("")&&!slinum.equals("")){
				List<NameValuePair> params=new ArrayList<NameValuePair>();
			
				//s_id,s_name,u_id,s_lat,s_lon,s_starttime,s_lasttime,
				//s_limatetime,s_total,s_describe,s_limatenumber
				params.add(new BasicNameValuePair("sports",sports.toString()));
				//params.add(new BasicNameValuePair("sid",sid.toString()));
				params.add(new BasicNameValuePair("sname",sname.toString()));
				params.add(new BasicNameValuePair("uid",uid.toString()));
				params.add(new BasicNameValuePair("slat",slat.toString()));
				params.add(new BasicNameValuePair("slon",slon.toString()));
				params.add(new BasicNameValuePair("slasttime",slasttime.toString()));
				params.add(new BasicNameValuePair("slitime",staslitime.toString()));
				params.add(new BasicNameValuePair("stotal",stotal.toString()));
				params.add(new BasicNameValuePair("sdescribe",sdescribe.toString()));
				params.add(new BasicNameValuePair("slinum",slinum.toString()));
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
					result =new String(dis.readUTF());
					
					System.out.println(result.toString());	
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
}
