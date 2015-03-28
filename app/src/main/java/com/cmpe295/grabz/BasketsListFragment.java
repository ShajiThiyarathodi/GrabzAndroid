package com.cmpe295.grabz;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sina on 3/27/2015.
 */
public class BasketsListFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    /** Items entered by the user is stored in this ArrayList variable */
    ArrayList<String> list = new ArrayList<String>();
    /** Declaring an ArrayAdapter to set items to ListView */
    // This is the Adapter being used to display the list's data
    ArrayAdapter<String> mAdapter;
    Context parentCtx;
    public static final String LOG_PREFIX = "BasketList";

    public static BasketsListFragment newInstance(int sectionNumber) {
        BasketsListFragment fragment = new BasketsListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.baskets_list, container,
                false);
        ListView lView = (ListView) rootView.findViewById(R.id.basketList);
        parentCtx = getActivity().getApplicationContext();
        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new ArrayAdapter<String>(parentCtx,
                R.layout.basket_names, list);
        lView.setAdapter(mAdapter);
        lView.setOnItemClickListener(new ListItemClickListener());
        Button btn = (Button) rootView.findViewById(R.id.btnAdd);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                EditText edit = (EditText) rootView.findViewById(R.id.txtItem);
                list.add(edit.getText().toString());
                edit.setText("");
                mAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }
    private class ListItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        Toast.makeText(
                parentCtx,
                " Position [" + position
                        + "]", Toast.LENGTH_SHORT).show();
    }
    private class AsyncListViewLoader extends
            AsyncTask<String, Void, List<String>> {
        private final ProgressDialog dialog = new ProgressDialog(
                parentCtx);

        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);

            Log.d(LOG_PREFIX, "In Post Execute" + String.valueOf(result.size()));
            dialog.dismiss();

            mAdapter.addAll(result);
           mAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Fetching items on the aisle...");
            dialog.show();
        }

        @Override
        protected List<String> doInBackground(String... params) {
            List<String> result = new ArrayList<String>();

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
                JSONArray arr = obj.getJSONArray("baskets");
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

// http://amitdikkar.x10host.com/grabz/GetUserBaskets.php?phoneId=1