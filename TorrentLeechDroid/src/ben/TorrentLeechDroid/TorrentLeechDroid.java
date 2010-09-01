package ben.TorrentLeechDroid;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.*;

public class TorrentLeechDroid extends Activity {
	
	public Captcha cCaptcha;
	public TorrentLeechDroidApp app = null;
	private ProgressDialog dialog;
	public TorrentLeechDroidApp getApp()
	{
		if(app == null)
			app = (TorrentLeechDroidApp)getApplication();
		return app;
	}
	public static final String LoginUrl = "http://www.torrentleech.org/takelogin.php";
	public static final String ListUrlFormat = "http://www.torrentleech.org/browse.php";
	public static final String reCAPTCHA_PUBLIC_KEY = "6LfjPgEAAAAAAJExraZeYXdYbMhPcG__Hyv-URXF";
	//public static final String reCAPTCHA_PRIVATE_KEY = "YOUR_reCAPTCHA_PRIVATE_KEY_HERE";

	public static final String reCAPTCHA_NOSCRIPT_URL = "http://api.recaptcha.net/noscript?k=";
	//public static final String reCAPTCHA_VERIFY_URL = "http://api-verify.recaptcha.net/verify";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout2);
     
        
        int validCookies = getApp().LoadCookies(getApp().Client); 
        //MessageBox(""+validCookies);
        if(validCookies <= 0)
        {
        	
        	
        	
        	EditText username = (EditText)findViewById(R.id.username);
        	username.setOnClickListener(mUserNameListener);
        	
        	EditText password = (EditText)findViewById(R.id.password);
        	password.setOnFocusChangeListener(mPasswordListener);
        	
	        Button PostButton = (Button)findViewById(R.id.postButton);
	        PostButton.setOnClickListener(mPostListener);
	        Button refreshButton = (Button)findViewById(R.id.refreshButton);
	        refreshButton.setOnClickListener(mRefreshListener);
	        ReloadLogin();
	        
        }
        else
        {
        	LoadList();
        }
    }
    public void MessageBox(String message){
	    Toast.makeText(this,message,Toast.LENGTH_LONG).show();
	}
    public void LoadList()
    {
    	Intent myIntent = new Intent(TorrentLeechDroid.this, TorrentList.class);
        myIntent.putExtra("PageUrl", ListUrlFormat);
        myIntent.putExtra("Search", "");
        myIntent.putExtra("Category", 0);
        startActivity(myIntent);
    }
    public void ReloadLogin()
    {
    	((TextView)findViewById(R.id.titleRightTop)).setText("Login");
    	
    	dialog  = ProgressDialog.show(TorrentLeechDroid.this, "", 
				"Loading . . Please wait.", true);
		//MessageBox(Url);
		new DownloadCaptcha().execute(reCAPTCHA_NOSCRIPT_URL+reCAPTCHA_PUBLIC_KEY);
    	
    	
    }
    
    private void loadCaptcha(Boolean result)
    {
    	dialog.hide();
    	ImageView iv = (ImageView)findViewById(R.id.myImg);
    	iv.setImageDrawable(ImageOperations(this, cCaptcha.ImageUrl, "tempCaptcha.jpg"));
    }
    private OnClickListener mUserNameListener = new OnClickListener() {
    	@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
    		EditText username = (EditText)findViewById(R.id.username);
		    username.setText("");
		    username.setTextColor(Color.BLACK);
    		
		}
    };
    private OnFocusChangeListener mPasswordListener = new OnFocusChangeListener() {
    	@Override
		public void onFocusChange(View arg0, boolean arg1) {
			// TODO Auto-generated method stub
    		if(arg1)
    		{
    			EditText password = (EditText)findViewById(R.id.password);
		    		password.setText("");
		    		password.setTextColor(Color.BLACK);
    		}
		}
    };
    
    private OnClickListener mPostListener = new OnClickListener() {
    	public void onClick(View v) {
    		// do something when the button is clicked   
    		EditText username = (EditText)findViewById(R.id.username);
    		getApp().Username = username.getText().toString();
    		dialog  = ProgressDialog.show(TorrentLeechDroid.this, "", 
    				"Loading . . Please wait.", true);
    		//MessageBox(Url);
    		new DownloadHtml().execute(LoginUrl);
    	    
    	}
    };
    private OnClickListener mRefreshListener = new OnClickListener() {
    	public void onClick(View v) {
    		// do something when the button is clicked   
    		ReloadLogin();
    	           
    	    
    	}
    };
    	
    	
    	private Drawable ImageOperations(Context ctx, String url, String saveFilename) {
    		try {
    			InputStream is = (InputStream) this.fetch(url);
    			Drawable d = Drawable.createFromStream(is, "src");
    			return d;
    		} catch (MalformedURLException e) {
    			e.printStackTrace();
    			return null;
    		} catch (IOException e) {
    			e.printStackTrace();
    			return null;
    		}
    	}

    	public Object fetch(String address) throws MalformedURLException,IOException {
    		URL url = new URL(address);
    		Object content = url.getContent();
    		return content;
    	}
    	
    	private class DownloadHtml extends AsyncTask<String, Integer, String> {
   	     protected String doInBackground(String... url) {
   	    	 
   	      //TorrentLeechDroidApp app = (TorrentLeechDroidApp)getApplication();
   	      EditText uName = (EditText)findViewById(R.id.username);
   	      EditText pass = (EditText)findViewById(R.id.password);
   	      EditText capResp = (EditText)findViewById(R.id.captchaResponse);
  		  
  	     
  	        // Add your data   
  	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);   
  	        nameValuePairs.add(new BasicNameValuePair("username", uName.getText().toString() ));   
  	        nameValuePairs.add(new BasicNameValuePair("password", pass.getText().toString() )); 
  	        nameValuePairs.add(new BasicNameValuePair("recaptcha_challenge_field", cCaptcha.Challenge )); 
  	        nameValuePairs.add(new BasicNameValuePair("recaptcha_response_field", capResp.getText().toString() )); 
  	        
  	        
   	         return getApp().PostUrlHtml(LoginUrl, nameValuePairs);
   	     }

   	     protected void onProgressUpdate(Integer... progress) {
   	         //setProgressPercent(progress[0]);
   	     }

   	     protected void onPostExecute(String result) {
   	    	dialog.hide();
   	    	 if(result != null && result.length() > 0)
  	        {
   	    		LoadList();
  	        }
   	    	else
   	    	{
   	    		MessageBox("Login error retry!");
   	    		ReloadLogin();
   	    	
   	    	}
   	     }
   	 }
    	private class DownloadCaptcha extends AsyncTask<String, Integer, Boolean> {
      	     protected Boolean doInBackground(String... url) {
      	    	 
      	    	
      	    	//tv.setText(captchaHtml);
      	    	try {
      	    		String captchaHtml = getApp().GetUrlHtml(url[0]);
      				cCaptcha = reCaptcha.GetCaptcha(captchaHtml);
      			} catch (Exception e) {
      				// TODO Auto-generated catch block
      				return false;
      			}
      			return true;
      	     }

      	     protected void onProgressUpdate(Integer... progress) {
      	         //setProgressPercent(progress[0]);
      	     }

      	     protected void onPostExecute(Boolean result) {
      	    	loadCaptcha(result);
      	    	
      	     }
      	 }
        	
}
