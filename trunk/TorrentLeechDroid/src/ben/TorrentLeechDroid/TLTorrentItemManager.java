package ben.TorrentLeechDroid;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class TLTorrentItemManager {

	public static final String tlItem = "\"details\\.php\\?id=([^\"|&]*).*<b>([^<]*).*<font[^>]*>([^<]*).*\\n((?!href=\").)*href=\"([^\"]*).*\\n.*\\n.*\\n[^>]*>([^<]*)<br>([^<]*).*\\n[^>]*>([^<]*)<br>([^<]*).*\\n((?!>[0-9]+<).)*>([^<]*).*\\n((?!>[0-9]+<).)*>([^<]*).*\\n((?!>[a-zA-z0-9]+<).)*>([^<]*).*\\n";
	public static final String catPattern = "name=c(\\d*) type=\"checkbox\" checked ";
	public static final String lastPagePattern = "page=(\\d*)\"><b>\\d*&nbsp;-&nbsp;\\d*</b></a></p>";
	
	
	
	public static TLTorrentItem[] getTLTorrentItems(String pageHtml)
	{
		Pattern pTLItem = Pattern.compile(tlItem);
		Matcher mTLItem = pTLItem.matcher(pageHtml);
		List<TLTorrentItem> myItems = new ArrayList<TLTorrentItem>();
		int index = 0;
		while(mTLItem.find(index)) {
			TLTorrentItem tlTI = //new TLTorrentItem("2324", "Ben", "", "", "", "", "", "", "", "date");
				new TLTorrentItem(
					mTLItem.group(1),
					mTLItem.group(2),
					mTLItem.group(5),
					"details.php?id="+mTLItem.group(1)+"&amp;hit=1",
					mTLItem.group(6)+" "+mTLItem.group(7),
					mTLItem.group(8),
					mTLItem.group(11),
					mTLItem.group(13),
					mTLItem.group(15),
					mTLItem.group(3));
			myItems.add(tlTI);
			index = mTLItem.end();
		}
		//myItems.add(new TLTorrentItem("2324", "Ben", "", "", "", "", "", "", "", "date"));
		return myItems.toArray(new TLTorrentItem[myItems.size()]);
		
	}
	
	public static int getCategory(String pageHtml)
	{
		int cat = 0;
		Pattern categoryPat = Pattern.compile(catPattern);
		Matcher mCategory = categoryPat.matcher(pageHtml);
		if(mCategory.find())
		{
			cat = Integer.parseInt(mCategory.group(1));
		}
		return cat;
	}
	public static int geLastPage(String pageHtml)
	{
		int lastPage = 0;
		Pattern lastPagePat = Pattern.compile(lastPagePattern);
		Matcher mLastPage = lastPagePat.matcher(pageHtml);
		if(mLastPage.find())
		{
			lastPage = Integer.parseInt(mLastPage.group(1));
		}
		else
		{
			Pattern findLast = Pattern.compile("page=(\\d*)\"");
			mLastPage = findLast.matcher(pageHtml);
			int index = 0;
			while(mLastPage.find(index)) {
				int nPg = Integer.parseInt(mLastPage.group(1));
				lastPage = (nPg > lastPage) ? nPg : lastPage;
				index = mLastPage.end();
			}
			if(index > 0)
				lastPage++;
		}
		return lastPage;
	}
	
	
}
