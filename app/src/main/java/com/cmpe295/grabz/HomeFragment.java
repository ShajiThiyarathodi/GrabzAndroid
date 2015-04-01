package com.cmpe295.grabz;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    public static final String HomeTabTitle = "Grabz";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String LOG_PREFIX = "HomeFragment";
    public static final String TAG_ID = "TAG_ID";

    private TextView mTextView;
    private NfcAdapter mNfcAdapter;
    private RelativeLayout relativeLayout;
    ListView lv;
    private ViewGroup header;

    List<AisleItem> aisleItemList = new ArrayList<AisleItem>();
    AisleItemAdapter aisleItemAdapter;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public HomeFragment() {
    }

    public static HomeFragment newInstance(int sectionNumber) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container,
                false);

        getActivity().setTitle(HomeTabTitle);
        mTextView = (TextView) rootView.findViewById(R.id.textView_message);
        relativeLayout = (RelativeLayout)rootView.findViewById(R.id.tap_view_group);
        Context parentCtx = getActivity().getApplicationContext();

        //NFC Tap screen
        mNfcAdapter = NfcAdapter.getDefaultAdapter(parentCtx);

        if (mNfcAdapter == null) {

            Toast.makeText(parentCtx, "Device doesn't support NFC.",
                    Toast.LENGTH_LONG).show();
            getActivity().finish();
            return rootView;
        }

        if (!mNfcAdapter.isEnabled()) {
            mTextView.setText("NFC is disabled.");
        } else {
            mTextView.setText(R.string.message);
        }


        //Aisle item list view, empty list now

        lv = (ListView) rootView.findViewById(R.id.aisleItemListView);
        aisleItemAdapter = new AisleItemAdapter(parentCtx, aisleItemList);
        header = (ViewGroup) inflater.inflate(R.layout.header, lv,
                false);


        lv.setAdapter(aisleItemAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view,
                                    int position, long id) {
               	Intent intent = new Intent(getActivity().getApplicationContext(),
						ItemDetailActivity.class);
				startActivity(intent);
            }
        });



        handleIntent(getActivity().getIntent(), parentCtx);

        return rootView;

    }

    private void handleIntent(Intent intent, Context context) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask(context).execute(tag);

            } else {
                Log.d(LOG_PREFIX, "Wrong mime type: " + type);
            }
        }
    }

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        private Context parentCtx;

        public NdefReaderTask(Context context) {
            parentCtx = context;
        }

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN
                        && Arrays.equals(ndefRecord.getType(),
                        NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(LOG_PREFIX, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record)
                throws UnsupportedEncodingException {
			/*
			 * See NFC forum specification for "Text Record Type Definition" at
			 * 3.2.1
			 * 
			 * http://www.nfc-forum.org/specs/
			 * 
			 * bit_7 defines encoding bit_6 reserved for future use, must be 0
			 * bit_5..0 length of IANA language code
			 */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8"
                    : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength,
            // "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length
                    - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d(LOG_PREFIX, "Tag content is: " + result);
			/*	Intent intent = new Intent(parentCtx,
						ItemsInAisleActivity.class);
				intent.putExtra(TAG_ID, result);
				startActivity(intent); */

                (new AsyncListViewLoader())
                        .execute("http://amitdikkar.x10host.com/grabz/GetAisleItems.php?tagId=3");
            } else {
                Log.e(LOG_PREFIX, "Tag reading failed");
                mTextView.setText("Could not read the tag.");

            }
        }
    }

    private class AsyncListViewLoader extends
            AsyncTask<String, Void, List<AisleItem>> {
        private final ProgressDialog dialog = new ProgressDialog(
                getActivity());

        @Override
        protected void onPostExecute(List<AisleItem> result) {
            super.onPostExecute(result);
            Log.d(LOG_PREFIX, "In Post Execute"+String.valueOf(result.size()));
            dialog.dismiss();
            // relativeLayout.setVisibility(View.GONE);
            ((LinearLayout)relativeLayout.getParent()).removeView(relativeLayout);

            lv.addHeaderView(header, null, false);
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
