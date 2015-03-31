package com.cmpe295.grabz;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ItemsInAisleActivity extends Activity {
	public static final String LOG_PREFIX = "ItemsInAisleActivity";

	List<AisleItem> aisleItemList = new ArrayList<AisleItem>();
	AisleItemAdapter aisleItemAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_items_in_aisle);

		(new AsyncListViewLoader())
				.execute("http://amitdikkar.x10host.com/grabz/GetAisleItems.php?tagId=3");

		ListView lv = (ListView) findViewById(R.id.listView);
		aisleItemAdapter = new AisleItemAdapter(this, aisleItemList);
		LayoutInflater inflater = getLayoutInflater();
	    ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header, lv,
	                false);
	    lv.addHeaderView(header, null, false);

		lv.setAdapter(aisleItemAdapter);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parentAdapter, View view,
					int position, long id) {

				Toast.makeText(
						ItemsInAisleActivity.this,
						"Item with id [" + id + "] - Position [" + position
								+ "]", Toast.LENGTH_SHORT).show();

			}
		});

	}

	private void initItemList() {

		aisleItemList.add(new AisleItem("Bread_item_Id", 10.5f));


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.items_in_aisle, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class AsyncListViewLoader extends
			AsyncTask<String, Void, List<AisleItem>> {
		private final ProgressDialog dialog = new ProgressDialog(
				ItemsInAisleActivity.this);

		@Override
		protected void onPostExecute(List<AisleItem> result) {
			super.onPostExecute(result);
			
			Log.d(LOG_PREFIX, "In Post Execute"+String.valueOf(result.size()));
			dialog.dismiss();

			aisleItemAdapter.addAll(result);
			aisleItemAdapter.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Fetching items on the aisle...");
			dialog.show();
		}

		@Override
		protected List<AisleItem> doInBackground(String... params) {
			List<AisleItem> result = new ArrayList<AisleItem>();

			try {
				URL u = new URL(params[0]);

				HttpURLConnection conn = (HttpURLConnection) u.openConnection();
				conn.setRequestMethod("GET");

				conn.connect();
				InputStream is = conn.getInputStream();

				// Read the stream
				byte[] b = new byte[1024];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				while (is.read(b) != -1)
					baos.write(b);

				String JSONResp = new String(baos.toByteArray());
				JSONObject obj = new JSONObject(JSONResp);
				JSONArray arr = obj.getJSONArray("aisleItems");
				Log.d(LOG_PREFIX, arr.toString());

				for (int i = 0; i < arr.length(); i++) {
					result.add(parseItems(arr.getJSONObject(i)));
				}

				Log.d(LOG_PREFIX, String.valueOf(result.size()));

				return result;
			} catch (Throwable t) {
				t.printStackTrace();
			}
			return null;
		}

		private AisleItem parseItems(JSONObject obj) throws JSONException {

			String name = obj.getString("itemId");
			Double price = obj.getDouble("price");

			return new AisleItem(name, price.floatValue());
		}

	}
}
