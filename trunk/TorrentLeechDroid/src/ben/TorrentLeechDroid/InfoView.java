package ben.TorrentLeechDroid;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class InfoView extends Activity {

	
	public TorrentLeechDroidApp app = null;
	public TorrentLeechDroidApp getApp()
	{
		if(app == null)
			app = (TorrentLeechDroidApp)getApplication();
		return app;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);

    	setContentView(R.layout.info);
    	((TextView)findViewById(R.id.titleRightTop)).setText("Info");
        ((TextView)findViewById(R.id.titleLeftTop)).setText(getApp().Username+" - "+getApp().Ratio);
   		((TextView)findViewById(R.id.titleDown)).setText(getApp().Downloaded);
   		((TextView)findViewById(R.id.titleUp)).setText(getApp().Uploaded);
   		
    	Bundle bundle = getIntent().getExtras();
    	
    	WebView webview = (WebView) findViewById(R.id.webview);
    	webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
    	webview.setInitialScale(99);

    	webViewLoadData(webview, "<html><body style=\"background-color: black;\"><font style=\"font-size: .3px;color: white;\">"+
    			bundle.getString("HtmlData")+"</font></body></html>");
    	//webViewLoadData(webview, bundle.getString("HtmlData"));
	    
	}
	
	public final static void webViewLoadData(WebView web, String html) {
        StringBuilder buf = new StringBuilder(html.length());
        for (char c : html.toCharArray()) {
            switch (c) {
              case '#':  buf.append("%23"); break;
              case '%':  buf.append("%25"); break;
              case '\'': buf.append("%27"); break;
              case '?':  buf.append("%3f"); break;                
              default:
                buf.append(c);
                break;
            }
        }
        web.loadData(buf.toString(), "text/html", "utf-8");
    }

	
}
