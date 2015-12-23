package com.activity.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class PrefaceIcon extends Activity implements AnimationListener{
	private ImageView imageView = null;
	private Animation alphaAnimation = null;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		// TODO Auto-generated method stub
		 super.onCreate(savedInstanceState);
			setContentView(R.layout.preface_icon);
			imageView =(ImageView)findViewById(R.id.welcome_image);
			alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.welcome_alpha);
			alphaAnimation.setFillEnabled(true); //����Fill����
			alphaAnimation.setFillAfter(true);  //���ö��������һ֡�Ǳ�����View����
			imageView.setAnimation(alphaAnimation);
			alphaAnimation.setAnimationListener(this);  //Ϊ�������ü���
	}

	@Override
	public void onAnimationEnd(Animation animation){
		//TODO Auto-generated method stub
		//��������ʱ������ӭ���沢ת��������
		Intent intent = new Intent(this, PrefaceCreator.class);
		startActivity(intent);
		this.finish();
	}

	@Override
	public void onAnimationRepeat(Animation animation){
		// TODO Auto-generated method stub		
	}

	@Override
	public void onAnimationStart(Animation animation){
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		// TODO Auto-generated method stub
		//�ڻ�ӭ��������back��
		if(keyCode==KeyEvent.KEYCODE_BACK){
			return false;
		}
		return false;
	}
}
