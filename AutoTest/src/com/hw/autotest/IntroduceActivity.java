package com.hw.autotest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class IntroduceActivity extends Activity{
	private TextView introductTv = null;
	private String content = null;
	private String testTitle =null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_introduce);
		Intent intent = this.getIntent();
		if(null != intent) {
			content = intent.getStringExtra(Constant.KEY_INTRODUCE_CONTENT);
			testTitle = intent.getStringExtra(Constant.KEY_TITLE);
		}
		initView();
	}
	
	private void initView() {
		TextView backBtn = (TextView) findViewById(R.id.back_button);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				IntroduceActivity.this.finish();
			}
		});
		TextView title = (TextView) findViewById(R.id.title_textview);
		title.setText(testTitle+""+getString(R.string.introduce));
		introductTv = (TextView) findViewById(R.id.introduce_textview);
		introductTv.setText(content);
	}
}
