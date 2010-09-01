package ben.TorrentLeechDroid;

public class TLTorrentItem {

	public String ID;
	public String Name;
	public String DownloadUrl;
	public String Url;
	public String Size;
	public String DownloadTimes;
	public String Seeders;
	public String Leechers;
	public String User;
	public String Date;
	public String ImgName;
	
	public TLTorrentItem(String imgName, String id, String name, String downloadUrl, String url, String size, String downloadTimes,
			String seeders, String leechers, String user, String date)
	{
		ImgName = imgName;
		ID = id;
		Name = name;
		DownloadUrl = downloadUrl;
		Url = url;
		Size = size;
		DownloadTimes = downloadTimes;
		Seeders = seeders;
		Leechers = leechers;
		User = user;
		Date = date;
	}
}
