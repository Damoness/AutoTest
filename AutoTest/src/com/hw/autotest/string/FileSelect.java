package com.hw.autotest.string;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;

import com.hw.autotest.R;
import com.hw.autotest.R.id;
import com.hw.autotest.R.layout;



import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FileSelect  extends Activity {
	public static final int FILE_RESULT_CODE = 1;
	private TextView textView;
	private List<String> strb=new ArrayList<String>();
	ListView myListView ;
	List<Persons> list_no = new ArrayList<Persons>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button button = (Button)findViewById(R.id.button);
		textView = (TextView)findViewById(R.id.fileText);
	    myListView = (ListView) findViewById(R.id.listView1);
		button.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(FileSelect.this,MyFileManager.class);
				startActivityForResult(intent, FILE_RESULT_CODE);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, textViewResourceId)
		if(FILE_RESULT_CODE == requestCode){
			Bundle bundle = null;
			strb.clear();
			if(data!=null&&(bundle=data.getExtras())!=null){
		
				PackageManager pm = getPackageManager();
				try {
					
					
					List<Persons> list=readFileSdcardFile(bundle.getString("file"));
					for (int i = 0; i < list.size(); i++) {
						Resources resources = pm.getResourcesForApplication(list.get(i).getName());
						int id = resources.getIdentifier(list.get(i).getId(), "string",
								list.get(i).getName());
						Log.i("xiawei", "xiawei=" + resources.getString(id));
						Configuration config = resources.getConfiguration();// 获得设置对象
						DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
						config.locale = new Locale(list.get(i).getLanguage());
						resources.updateConfiguration(config, dm);
						if(!list.get(i).getValue().equals(resources.getString(id))){
							list_no.add(list.get(i));
						}
						Log.i("xiawei", "xiawei1=" + resources.getString(id));
						
				}
					
				
				if(list_no!= null){
					strb.add("包名"+"  "+"字符串ID"+"   "+"语言");
					for (int i = 0; i < list_no.size(); i++){
						strb.add(list_no.get(i).getName()+"  "+list_no.get(i).getId()+"   "+list_no.get(i).getLanguage());
					}
				}else{
					strb.add("完全正确");
				}
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				myListView.setAdapter(new ArrayAdapter<String>(this,
		                android.R.layout.simple_list_item_1, strb));
            //  textView.setText(strb.toString());
			}
		}
	}
	
	
	public  List<Persons> readFileSdcardFile(String fileName) throws IOException {
		
		 List<Persons> list =new ArrayList<Persons>();
		try {

		    XmlPullParser pullParser = Xml.newPullParser();
			FileInputStream fin = new FileInputStream(fileName);
		    pullParser.setInput(fin, "UTF-8"); 
		    int event = pullParser.getEventType();
		    while (event != XmlPullParser.END_DOCUMENT){
		    
		    	if(event == XmlPullParser.START_TAG){
		    		Log.i("xiawei", "xiawei21=" + pullParser.getName());
	                if ("Test".equals(pullParser.getName())){
	                	  Persons pers=new Persons();
	                	  pers.setName(pullParser.getAttributeValue(0));
	                	  pers.setId(pullParser.getAttributeValue(1));
	                	  pers.setValue(pullParser.getAttributeValue(2));
	                	  pers.setLanguage(pullParser.getAttributeValue(3));
	                      list.add(pers);
	                }
		    	}
		    
		  
		            event = pullParser.next();


		    }

			fin.close();
		    
		    
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}



	
}