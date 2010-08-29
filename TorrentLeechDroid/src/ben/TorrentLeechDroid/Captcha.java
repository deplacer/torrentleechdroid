package ben.TorrentLeechDroid;




public class Captcha {

	public String Challenge;
	public String ImageUrl;
	public String AllHtml;
	//public DefaultHttpClient Client;
	
	public Captcha(String challenge, String imageUrl, String allHtml)
	{
		Challenge = challenge;
		ImageUrl = imageUrl;
		AllHtml = allHtml;
		//Client = client;
	}
}
