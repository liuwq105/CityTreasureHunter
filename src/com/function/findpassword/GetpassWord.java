package com.function.findpassword;
//�һ�������첽��ʾ���������ǣ������ڵڶ���activity����ʾ����������Ϊʲô��
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

import com.activity.main.LoginFindPassword;
import com.function.UrlIP.GetHeaderIP;

import android.os.AsyncTask;
import android.widget.EditText;

public class GetpassWord extends AsyncTask<Object, Object, Object>{
	
	protected void onPreExecute() {
	}

	protected Object doInBackground(Object... params_obj)//(EditText et_ques,EditText tv_ans,Context FindpswdActivity)
	{
		String result="";
		CharSequence username="";//������1����
		CharSequence question="";
		CharSequence answer="";
		username=((EditText) params_obj[0]).getText().toString();
		question=((EditText) params_obj[1]).getText().toString();
		answer=((EditText) params_obj[2]).getText().toString();
		
		try{
			HttpPost httpRequest =new HttpPost(HttpUtil.FINDPASSWORD_URL);
			if(!username.equals("")&&!question.equals("")&&!answer.equals("")){
			
				//��������
				//��������
				//ʹ��NameValuePair���������ֵ�ԣ�ʹ��NameValuePair������Ϊ������Ҫ���Ǹ���Ĳ���Ҫ��
				List<NameValuePair> params=new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("username",username.toString()));
				params.add(new BasicNameValuePair("question",question.toString()));
				params.add(new BasicNameValuePair("answer",answer.toString()));
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
					
					System.out.println(result.toString());
					//Toast.makeText(FindpswdActivity,"!!!", Toast.LENGTH_SHORT).show();
					//Toast.makeText(MainActivity.this, "!!!", Toast.LENGTH_SHORT).show();					
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

		if(result.toString().equals(" "))
			LoginFindPassword.adialog.show();
		else
			LoginFindPassword.show.setText("�������룺"+result.toString());
		result=" ";
	}	
}

