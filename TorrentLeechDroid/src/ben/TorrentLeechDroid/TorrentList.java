package ben.TorrentLeechDroid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.methods.HttpGet;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import android.widget.*;

public class TorrentList extends ListActivity {

	public static final String imgPath = "http://www.torrentleech.org/pic/";
	public static final String infoPath = "http://www.torrentleech.org/details.php?id=";
	private int CurrentPage = 0;
	private int LastPage = 0;
	private int CurrentCategory = 0;
	private String BaseUrl = "";
	private String Search = "";
	private TLTorrentItemAdapater adapter;
	private ProgressDialog MyDialog;
	
	public TorrentLeechDroidApp app = null;
	public TorrentLeechDroidApp getApp()
	{
		if(app == null)
			app = (TorrentLeechDroidApp)getApplication();
		return app;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listlayout);
		
        try
		{
			Bundle bundle = getIntent().getExtras();
			BaseUrl = bundle.getString("PageUrl");
			Search = bundle.getString("Search");
			CurrentCategory = bundle.getInt("Category");
			String Url = getCurrentUrl();
			//MessageBox(Url);
			LoadList(getCurrentUrl());
		}
		finally {}
	}
	public void LoadList(final String Url)
	{
		//String pageHtml = "";
		MyDialog  = ProgressDialog.show(TorrentList.this, "", 
				"Loading . . Please wait.", true);
		//MessageBox(Url);
		new DownloadHtml().execute(Url);
		
	}
	
	private void loadIt(String pageHtml)
	{
		if(pageHtml == null || pageHtml.length() == 0)
		{
			LogoutClear();
			return;
		}
		if(getApp().CategorieIds == null)
			getApp().LoadCategories(pageHtml);
		
		
		
		this.CurrentCategory = TLTorrentItemManager.getCategory(pageHtml);
		this.LastPage = TLTorrentItemManager.geLastPage(pageHtml);
		//setTitle(R.string.list_title);
		//setTitle(getTitle()+" "+getCategoryText()+" "+(CurrentPage+1)+" of "+(LastPage+1));
		getApp().CheckAndLoadHeader(pageHtml);
   		((TextView)findViewById(R.id.titleLeftTop)).setText(getApp().Username+" - "+getApp().Ratio);
   		((TextView)findViewById(R.id.titleDown)).setText(getApp().Downloaded);
   		((TextView)findViewById(R.id.titleUp)).setText(getApp().Uploaded);
		((TextView)findViewById(R.id.titleRightTop)).setText(getCategoryText());
		((TextView)findViewById(R.id.titleRightBottom)).setText((CurrentPage+1)+" of "+(LastPage+1));
		
		this.adapter = new TLTorrentItemAdapater(this, R.layout.listitemlayout, TLTorrentItemManager.getTLTorrentItems(pageHtml)); 
		setListAdapter(this.adapter);
        MyDialog.hide();
        
	}
	
	private void LogoutClear()
	{
		getApp().Client.getCookieStore().clear();
		getApp().StoreCookies(getApp().Client);
		Intent myIntent = new Intent(TorrentList.this, TorrentLeechDroid.class);
        startActivity(myIntent);
	}
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.menu, menu);
	    return true;
	}
	
	private TLTorrentItem CurrentItem = null;
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		CurrentItem = this.adapter.getItem(position);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	//builder.setTitle("Categories");
    	builder.setItems(new String[] { "Download Now", "View Info" } , new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	       // Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
    	    	if(item == 0)
    	    	{
    	    		
    	    		String uriString = "http://www.torrentleech.org/"+CurrentItem.DownloadUrl;
    	    		
    	    		MyDialog  = ProgressDialog.show(TorrentList.this, "", 
    	    				"Downloading Torrent. . Please wait.", true);
    	    		//MessageBox(Url);
    	    		new DownloadFile().execute(uriString);
    	    	}
    	    	else if(item == 1)
    	    	{
    	    		MyDialog  = ProgressDialog.show(TorrentList.this, "", 
    	    				"Downloading Info. . Please wait.", true);
    	    		//MessageBox(Url);
    	    		new DownloadInfo().execute(infoPath+CurrentItem.ID);
    	    	}
    	    }
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
		
		

		
		
		super.onListItemClick(l, v, position, id);
	}
	
	public void LaunchInfo(String pageHtml)
	{
		MyDialog.hide();
		//MessageBox(pageHtml);
		try
		{
			Intent myIntent = new Intent(TorrentList.this, InfoView.class);
			String justInfo = TLTorrentItemManager.getInfo(pageHtml);
			myIntent.putExtra("HtmlData", justInfo);
	        startActivity(myIntent);
		}
		catch(Exception e)
		{
		
		}
	}
	
	private void OpenTempTorrent(String downloadResult)
	{
		MyDialog.hide();
		try
		{
			if(downloadResult == "Download Complete")
			{
				MessageBox(downloadResult+" Launching Torrent");
				Uri mUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()+"/Download/", "temp.torrent"));
				//MessageBox(mUri.toString());
			    Intent i2 = new Intent(android.content.Intent.ACTION_VIEW, mUri);
			    startActivity(i2);
			}
			else
				MessageBox("Failed Downloading Torrent. "+downloadResult);
		    
		}catch(Exception e)
		{
			MessageBox("failed "+e.getMessage());
		}
	}
	
	public void MessageBox(String message){
	    Toast.makeText(this,message,Toast.LENGTH_LONG).show();
	}
 
	private class TLTorrentItemAdapater extends ArrayAdapter<TLTorrentItem> { 
	 
	        private TLTorrentItem[] items; 
	 
	        public TLTorrentItemAdapater(Context context, int textViewResourceId, TLTorrentItem[] items) { 
	                super(context, textViewResourceId, items); 
	                this.items = items; 
	        } 
	 
	        @Override 
	        public View getView(int position, View convertView, ViewGroup parent) { 
	                View v = convertView; 
	                if (v == null) { 
	                        LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	                v = vi.inflate(R.layout.listitemlayout, null); 
	                } 
	 
	                TLTorrentItem it = items[position]; 
	                if (it != null) { 
	                	TextView tv1 = (TextView) v.findViewById(R.id.text1);
	                	ImageView catImageView = (ImageView) v.findViewById(R.id.imgCategory);
	                	//TextView tImgAlt = (TextView) v.findViewById(R.id.imgAlt);
	                    //tImgAlt.setText(it.ImgName);
	                	catImageView.setImageDrawable( getApp().ImageGet(this.getContext(), imgPath+it.ImgName, it.ImgName) );
	                	
	                	if (tv1 != null) { 
	                        	tv1.setText(it.Name);
	                        }
                        TextView tv2 = (TextView) v.findViewById(R.id.text2); 
                        if (tv2 != null) { 
                        	//tv2.setText(it.DownloadUrl);
                        	tv2.setText(it.Date+"    "+it.Size+" "+it.DownloadTimes+"-"+it.Seeders+"-"+it.Leechers+" "+it.User);
                        } 
	                } 
	 
	                return v; 
	        } 
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.search:
	    	Intent myIntent = new Intent(TorrentList.this, Search.class);
	        startActivity(myIntent);
	        return true;
	    case R.id.refresh:
	    	LoadList(getCurrentUrl());
	        return true;
	    case R.id.next:
	    	CurrentPage = (CurrentPage+1 <= LastPage) ? CurrentPage+1 : CurrentPage;
	    	String cUrl = getCurrentUrl();
	    	LoadList(cUrl);
	    	
	    	
	        return true;
	    case R.id.previous:
	    	CurrentPage = (CurrentPage-1 <= 0) ? 0 : CurrentPage-1;
	    	//MessageBox(getCurrentUrl());
	    	LoadList(getCurrentUrl());
	        return true;
	    case R.id.categories:
	    	final String[] items = getApp().CategorieNames;

	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setTitle("Categories");
	    	builder.setItems(items, new DialogInterface.OnClickListener() {
	    	    public void onClick(DialogInterface dialog, int item) {
	    	       // Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
	    	    	CurrentCategory = Integer.parseInt( getApp().CategorieIds[item] );
	    	    	Search = "";
	    	    	CurrentPage = 0;
	    	    	LoadList(getCurrentUrl());
	    	    }
	    	});
	    	AlertDialog alert = builder.create();
	    	alert.show();
	    	return true;
	    case R.id.logout:
	    	LogoutClear();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private String getCurrentUrl()
	{
		String qUrl = (Search.length() > 0) ? "search="+Uri.encode(Search)+"&" : "";
		qUrl += "cat="+CurrentCategory+"&";
		return BaseUrl+"?"+qUrl+"page="+CurrentPage;
	}
	
	private String getCategoryText()
	{
		if(CurrentCategory > 0)
		{
			try
			{
			int indx = 0;
			for(String catId : getApp().CategorieIds)
			{
				if(Integer.parseInt(catId) == CurrentCategory)
				{
					return getApp().CategorieNames[indx];
				}
				indx++;
			}
			
			}
			catch(Exception e)
			{
				return "error";
			}
		}
		return "";
	}
	
	private class DownloadHtml extends AsyncTask<String, Integer, String> {
	     protected String doInBackground(String... url) {
	    	 String pageHtml = "";
	    	 try
	    	 {
	    		 TorrentLeechDroidApp app = (TorrentLeechDroidApp)getApplication();
	    		 pageHtml = app.GetUrlHtml(url[0]);
	    	 }
	    	 finally
	    	 {
	    		 
	    	 }
	         return pageHtml;
	     }

	     protected void onProgressUpdate(Integer... progress) {
	         //setProgressPercent(progress[0]);
	     }

	     protected void onPostExecute(String result) {
	         loadIt(result);
	     }
	 }
	private class DownloadFile extends AsyncTask<String, Integer, String> {
	     @SuppressWarnings("finally")
		protected String doInBackground(String... url) {
	    	 
	    	 try
	    	 {
	    		 ((TorrentLeechDroidApp)getApplication()).DownloadFile(url[0], "temp.torrent");
	    		 return "Download Complete";
	    	 }
	    	 catch(Exception e)
	    	 {
	    		 return e.getMessage();
	    	 }
	     }

	     protected void onProgressUpdate(Integer... progress) {
	         //setProgressPercent(progress[0]);
	     }

	     protected void onPostExecute(String result) {
	    	 OpenTempTorrent(result);
	     }
	 }
	private class DownloadInfo extends AsyncTask<String, Integer, String> {
	     protected String doInBackground(String... url) {
	    	 String pageHtml = "";
	    	 try
	    	 {
	    		 TorrentLeechDroidApp app = (TorrentLeechDroidApp)getApplication();
	    		 pageHtml = app.GetUrlHtml(url[0]);
	    	 }
	    	 catch(Exception e)
	    	 {
	    		 return e.getMessage();
	    	 }
	         return pageHtml;
	     }

	     protected void onProgressUpdate(Integer... progress) {
	         //setProgressPercent(progress[0]);
	     }

	     protected void onPostExecute(String result) {
	    	 MyDialog.hide();
	         LaunchInfo(result);
	     }
	 }
}
