package ben.TorrentLeechDroid;



import java.util.regex.*;

public class reCaptcha {
	
	public static final String PREFS_NAME = "MyPrefsFile";
	
	
	public static final String apiBaseUrl = "http://api.recaptcha.net/";
	
	
	public static Captcha GetCaptcha(String page) throws Exception
    {
        // http://api.recaptcha.net/noscript?k=
       
        
        try
        {
	        Pattern imgReg = Pattern.compile("(<img .*src\\=\")([^\"]*)");
	        Pattern chalReg = Pattern.compile("(id=\"recaptcha_challenge_field\" value=\")([^\"]*)");
	
	        
	        
	        // test for regex match
	        Matcher challengeMatch = chalReg.matcher(page);
	        Matcher imageMatch = imgReg.matcher(page);
	        boolean bChal = challengeMatch.find();
	        boolean bImg = imageMatch.find();
	
	        // make sure we got regex matches
	        if (bChal && bImg)
	        {
	            // get challenge
	            String challenge = challengeMatch.group(2);
	
	            // get image url
	            String imageUrl = apiBaseUrl+imageMatch.group(2);
	
	            // return a Captcha struct
	            return new Captcha(challenge, imageUrl, page);
	        }
	        else
	        {
	            // something didn't work.
	        	Captcha myCap = new Captcha(new Boolean(bChal).toString(),new Boolean( bImg).toString(), page);
	            return myCap;
	        }
        }
        catch(Exception e)
        {
        	Captcha myCap = new Captcha("",page, page);
            return myCap;
        
        }
    }

	
	

}
