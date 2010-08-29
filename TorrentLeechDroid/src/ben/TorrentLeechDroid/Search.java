package ben.TorrentLeechDroid;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class Search extends Activity {

	public static final String ListUrlFormat = "http://www.torrentleech.org/browse.php";
	
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
	    setContentView(R.layout.search);
	    setTitle(R.string.search_title);
	    
	    Button SearchButton = (Button)findViewById(R.id.searchButton);
	    SearchButton.setOnClickListener(mSearchListener);

	    Spinner s = (Spinner) findViewById(R.id.spinner);
	    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getApp().CategorieNames);
	    s.setAdapter(spinnerArrayAdapter);
	    
	}
	
	private OnClickListener mSearchListener = new OnClickListener() {
    	public void onClick(View v) {
    		// do something when the button is clicked  
    		EditText st = (EditText) findViewById(R.id.searchCriteria);
    		Spinner s = (Spinner) findViewById(R.id.spinner);
    		Intent myIntent = new Intent(Search.this, TorrentList.class);
            myIntent.putExtra("PageUrl", ListUrlFormat);
            myIntent.putExtra("Search", st.getText().toString());
            myIntent.putExtra("Category", Integer.parseInt(getApp().CategorieIds[s.getSelectedItemPosition()]));
            startActivity(myIntent);
    	}
    };
}
