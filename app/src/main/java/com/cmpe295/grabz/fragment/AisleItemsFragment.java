package com.cmpe295.grabz.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpe295.grabz.Dto.AisleItemDto;
import com.cmpe295.grabz.Dto.LinkDto;
import com.cmpe295.grabz.activity.ItemDetailActivity;
import com.cmpe295.grabz.R;
import com.cmpe295.grabz.activity.MainActivity;
import com.cmpe295.grabz.adapter.AisleItemAdapter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AisleItemsFragment extends Fragment {

    public static final String AisleItemsTabTitle = "Grabz";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String LOG_PREFIX = "AisleItemsFragment";
    public static final String TAG_ID = "Tag_Id";
    public static final String ITEM_HREF = "Item_href";
    public static final String SOURCE = "source";
    public static final String GRABZPREFERENCES = "TAG_ID";
//    private String deviceId;

    private static Context parentCtx;
    private TextView mTextView;
    private NfcAdapter mNfcAdapter;
    private RelativeLayout relativeLayout;
    ListView lv;
    private SharedPreferences sharedPreferences;

    List<AisleItemDto> aisleItemList = new ArrayList<AisleItemDto>();
    ArrayAdapter aisleItemAdapter;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public AisleItemsFragment() {
    }

    public static AisleItemsFragment newInstance(int sectionNumber) {
        AisleItemsFragment fragment = new AisleItemsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_aisle_items, container,
                false);

        getActivity().setTitle(AisleItemsTabTitle);
        mTextView = (TextView) rootView.findViewById(R.id.textView_message);
        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.tap_view_group);
        parentCtx = getActivity().getApplicationContext();
        TelephonyManager telephonyManager = (TelephonyManager) this.
                getActivity().getSystemService(Context.TELEPHONY_SERVICE);
//        deviceId = telephonyManager.getDeviceId();
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
            mTextView.setVisibility(View.INVISIBLE);
        }


        //Aisle item list view, empty list now
        lv = (ListView) rootView.findViewById(R.id.aisleItemListView);


        aisleItemAdapter = new AisleItemAdapter(parentCtx, aisleItemList);

        lv.setAdapter(aisleItemAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity().getApplicationContext(),
                        ItemDetailActivity.class);
                String itemHref = null;
                LinkDto link = null;
                Iterator<LinkDto> iterator = ((AisleItemDto) lv.getItemAtPosition(position)).getLinks().iterator();
                while (iterator.hasNext()) {
                    link = iterator.next();
                    if (link.getRel().equals("view-AisleItem")) {
                        itemHref = link.getHref();
                        break;
                    }
                }
                // href is required to load item details
                intent.putExtra(ITEM_HREF, itemHref);
                //intent.putExtra(ITEM_ID, ((AisleItemDto) lv.getItemAtPosition(position)).getAisleItem().getItemId());

                // source is used to detect if the user comes from aisle items screen or shopping list screen
                intent.putExtra(SOURCE, "aisleItem");
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


            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8"
                    : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;


            // Read the Text
            return new String(payload, languageCodeLength + 1, payload.length
                    - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                sharedPreferences = getActivity().getSharedPreferences(GRABZPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(TAG_ID, result);
                editor.commit();

                String url = getString(R.string.awsLink) + "/tags/" + result + "/items/";
                SharedPreferences settings = parentCtx.getSharedPreferences(MainActivity.PREFS_NAME, 0);
                final String deviceId = settings.getString("deviceId", null);
                (new AsyncListViewLoader())
                        .execute(url);
                if (deviceId!=null)
                    (new PopulateAilseNumbers())
                            .execute(getString(R.string.awsLink) + "/tags/" +
                                    result + "/users/" + deviceId + "/baskets/updateAisleNumbers");
            } else {
                Log.e(LOG_PREFIX, "Tag reading failed");
                mTextView.setText("Could not read the tag.");

            }
        }
    }

    private class AsyncListViewLoader extends
            AsyncTask<String, Void, AisleItemDto[]> {
        private final ProgressDialog dialog = new ProgressDialog(
                getActivity());
        HttpStatus responseCode;

        @Override
        protected void onPostExecute(AisleItemDto[] result) {
            if (responseCode == HttpStatus.OK && result != null) {
                dialog.dismiss();
                ((LinearLayout) relativeLayout.getParent()).removeView(relativeLayout);
                aisleItemAdapter.addAll(result);
                aisleItemAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Fetching items on the aisle...");
            dialog.show();
        }

        @Override
        protected AisleItemDto[] doInBackground(String... params) {

            try {
                String url = params[0];
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
                HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                restTemplate.getMessageConverters().add(mapper);
                ResponseEntity<AisleItemDto[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, AisleItemDto[].class);
                AisleItemDto[] items = responseEntity.getBody();
                responseCode = responseEntity.getStatusCode();

                return items;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

    }

    public static class PopulateAilseNumbers extends
            AsyncTask<String, Void, Void> {

        HttpStatus responseCode;

        @Override
        protected void onPostExecute(Void result) {
            if (responseCode != HttpStatus.OK) {
                Toast.makeText(parentCtx, "Aisle numbers didn't get updated", Toast.LENGTH_SHORT).show();
            } else if (responseCode == HttpStatus.UNPROCESSABLE_ENTITY) {
                Toast.makeText(parentCtx, "Wrong tag id", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected Void doInBackground(String... params) {

            try {
                String url = params[0];
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
                HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                restTemplate.getMessageConverters().add(mapper);
                ResponseEntity<Void> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Void.class);
                responseCode = responseEntity.getStatusCode();
                Log.d("PopulateAilseNumbers", "on "+url+" "+responseCode);
                return null;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

    }

}
