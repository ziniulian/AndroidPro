package ziniulian.invengo.com.test.testfhcview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

	protected FrMain frMain = new FrMain();
	protected FrScan frScan = new FrScan();
	protected FrShow frShow = new FrShow();
	protected String path = "Log.txt";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getFragmentManager().beginTransaction().replace(R.id.frdiv, frMain).commit();
	}
}
