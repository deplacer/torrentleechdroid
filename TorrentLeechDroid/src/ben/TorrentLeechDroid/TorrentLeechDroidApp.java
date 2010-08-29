package ben.TorrentLeechDroid;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ParseException;
import android.os.Environment;
//import android.util.Base64InputStream;
//import android.util.Base64OutputStream;


public class TorrentLeechDroidApp extends Application {
	public static final String PREFS_NAME = "MyPrefsFile";
	public DefaultHttpClient Client = null;
	public String[] CategorieNames = null;
	public String[] CategorieIds = null;
	public static final String categoryPattern = "value=\"(\\d*)\">([^<]*)";
	public TorrentLeechDroidApp ()
	{
		super();
		Client = new DefaultHttpClient();
		
		
		
	}
	
	public int LoadCookies(DefaultHttpClient httpclient)
	{
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String cList = settings.getString("Cookie1", null);
		if(cList != null && cList.length() > 0)
		{
			final CookieStore targetCookieStore = httpclient.getCookieStore();

            List<Cookie> cookies = (List<Cookie>)stringToObject(cList);

            // Add all the extracted cookies to the cookie store.
            for (Cookie cookie : cookies) {
                targetCookieStore.addCookie(cookie);
            }
            Date d = new Date();
            targetCookieStore.clearExpired(d);
			return cookies.size();
		}
		return -1;
		
	}
	public void StoreCookies(DefaultHttpClient httpclient)
	{
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);       
		SharedPreferences.Editor editor = settings.edit();
		
		String cList = "";
		final List<Cookie> cookies = httpclient.getCookieStore().getCookies();

        

        final List<Cookie> serialisableCookies = new
        		ArrayList<Cookie>(cookies.size());

        for (Cookie cookie : cookies) {

            serialisableCookies.add(new SerializableCookie(cookie));
        }
        cList = objectToString((Serializable)serialisableCookies);
		
		editor.putString("Cookie1", cList);
		// Commit the edits!      
		editor.commit();
		
		
	}
	public static String objectToString(Serializable object) { 
	    ByteArrayOutputStream out = new ByteArrayOutputStream(); 
	    try { 
	        new ObjectOutputStream(out).writeObject(object); 
	        byte[] data = out.toByteArray(); 
	        out.close(); 
	 
//	        out = new ByteArrayOutputStream(); 
//	        Base64OutputStream b64 = new Base64OutputStream(out, 0); 
//	        b64.write(data); 
//	        b64.close(); 
//	        out.close(); 
			return Base64.encodeBytes(data);
	    } catch (IOException e) { 
	        e.printStackTrace(); 
	    } 
	    return null; 
	} 
	 
	public static Object stringToObject(String encodedObject) { 
	    try { 
	        return new ObjectInputStream(new ByteArrayInputStream(Base64.decode(encodedObject))).readObject(); 
	    } catch (Exception e) { 
	        e.printStackTrace(); 
	    } 
	    return null; 
	}
	public String GetUrlHtml(String url)
	{
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = null;
		try {
			response = Client.execute(httpget);
		
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return getResponseBody(response);
	}
	
	public String PostUrlHtml(String url, List<NameValuePair> postValues)
	{
		HttpPost httppost = new HttpPost(url);   
		String resp = "";
	    try {   
	        // Add your data   
	        httppost.setEntity(new UrlEncodedFormEntity(postValues));   
	        
	        // Execute HTTP Post Request   
	        HttpResponse response = Client.execute(httppost);
	        resp = getResponseBody(response);
	        
	        StoreCookies(Client);
	        
	           
	    } catch (ClientProtocolException e) {   
	        // TODO Auto-generated catch block   
	    } catch (IOException e) {   
	        // TODO Auto-generated catch block   
	    }
	    return resp;
	}
	
	public boolean DownloadFile(String url, String filename)
	{
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = null;
		try {
			response = Client.execute(httpget);
		
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			File mf = new File(Environment.getExternalStorageDirectory()+"/Download/", filename);
			FileOutputStream fos = new FileOutputStream(mf);
			copy(response.getEntity().getContent(), fos);
			fos.flush();
			fos.close();
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
		
	}
	
	private static final int IO_BUFFER_SIZE = 4 * 1024;
	
	private static void copy(InputStream in, OutputStream out) throws IOException {  
		byte[] b = new byte[IO_BUFFER_SIZE];  
		int read;  
		while ((read = in.read(b)) != -1) {  
		out.write(b, 0, read);  
		}  
		}
	
	public static String getResponseBody(HttpResponse response) 
	{

    	String response_text = null;

    	HttpEntity entity = null;

    	try {

    		entity = response.getEntity();

    		response_text = _getResponseBody(entity);

    	} catch (ParseException e) {

    		e.printStackTrace();

    	} catch (IOException e) {

    	if (entity != null) {

    	try {

    		entity.consumeContent();

    	} catch (IOException e1) {

    	}

    	}

    	}

    	return response_text;

    }

    	public static String _getResponseBody(final HttpEntity entity) throws IOException, ParseException 
    	{

    		if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }

    		InputStream instream = entity.getContent();

    		if (instream == null) { return ""; }

    		if (entity.getContentLength() > Integer.MAX_VALUE) 
    		{ 
    			throw new IllegalArgumentException("HTTP entity too large to be buffered in memory"); 
    		}
    		

    		String charset = getContentCharSet(entity);

    		if (charset == null) {

    			charset = HTTP.DEFAULT_CONTENT_CHARSET;

    		}

    		Reader reader = new InputStreamReader(instream, charset);

    		StringBuilder buffer = new StringBuilder();

    		try {

    			char[] tmp = new char[1024];

    			int l;

    			while ((l = reader.read(tmp)) != -1) {

    				buffer.append(tmp, 0, l);

    			}

    		} finally {

    			reader.close();

    		}

    		return buffer.toString();

    	}

    	public static String getContentCharSet(final HttpEntity entity) throws ParseException {

    		if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }

    		String charset = null;

    		if (entity.getContentType() != null) {

    			HeaderElement values[] = entity.getContentType().getElements();

    			if (values.length > 0) {

    				NameValuePair param = values[0].getParameterByName("charset");

    				if (param != null) {

    					charset = param.getValue();

    				}

    			}

    		}

    		return charset;

    	}
    	
    	public void LoadCategories(String pageHtml)
    	{
    		Pattern categoryStart = Pattern.compile("<select name=\"cat\">.*</select>", Pattern.DOTALL);

    		 Matcher catMatch = categoryStart.matcher(pageHtml);
    		 boolean found = catMatch.find();
    		 String cats = "";
    		 if(found)
    			 cats = catMatch.group();
    		
    		Pattern categoryPat = Pattern.compile(categoryPattern);
    		Matcher categoryM = categoryPat.matcher(cats);
    		List<String> cNames = new ArrayList<String>();
    		List<String> cIds = new ArrayList<String>();
    		int index = 0;
    		while(categoryM.find(index)) {
    			cNames.add(categoryM.group(2));
    			cIds.add(categoryM.group(1));
    			index = categoryM.end();
    		}
    		CategorieNames = cNames.toArray(new String[cNames.size()]);
    		CategorieIds = cIds.toArray(new String[cIds.size()]);
    	}
}
