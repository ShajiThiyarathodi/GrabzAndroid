package com.cmpe295.grabz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Sina on 3/27/2015.
 */
public class BasketsListFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    /** Items entered by the user is stored in this ArrayList variable */
    static ArrayList<BasketDto> list = new ArrayList<BasketDto>();
    /** Declaring an ArrayAdapter to set items to ListView */
    // This is the Adapter being used to display the list's data
    static ArrayAdapter<BasketDto> mAdapter;
    Context parentCtx;
    View rootView;
    EditText edit;
    public static final String LOG_PREFIX = "BasketList";

    public BasketsListFragment() {
    }

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

        rootView = inflater.inflate(R.layout.baskets_list, container,
                false);
        ListView lView = (ListView) rootView.findViewById(R.id.basketList);
        parentCtx = getActivity().getApplicationContext();
//        (new AsyncListViewLoader())
//                .execute("http://amitdikkar.x10host.com/grabz/GetUserBaskets.php?phoneId=1");
        (new BasketListGetRequestTask())
                .execute("http://grabztestenv.elasticbeanstalk.com/baskets?phoneId=myPhone-4");

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new BasketListAdapter(parentCtx,list);
        lView.setAdapter(mAdapter);
        lView.setOnItemClickListener(new ListItemClickListener());
        Button btn = (Button) rootView.findViewById(R.id.btnAdd);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                edit = (EditText) rootView.findViewById(R.id.txtItem);
                if (!edit.getText().toString().equals("")) {
                    (new BasketPostRequestTask())
                            .execute("http://grabztestenv.elasticbeanstalk.com/baskets?phoneId=myPhone-4");
                }
            }
        });

        return rootView;
    }
    private class ListItemClickListener implements
            AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            selectItem(position,"Basket");
        }
    }

    private void selectItem(int position, String msg) {
        Toast.makeText(
                parentCtx,
                msg+" [" + position
                        + "]", Toast.LENGTH_SHORT).show();
    }
/*
    private class AsyncListViewLoader extends
            AsyncTask<String, Void, List<BasketDto>> {
        private final ProgressDialog dialog = new ProgressDialog(
                parentCtx);

        @Override
        protected void onPostExecute(List<BasketDto> result) {
            super.onPostExecute(result);

            Log.d(LOG_PREFIX, "In Post Execute " + String.valueOf(result.size()));
//            dialog.dismiss();
            if (result != null) {
                TextView tv = (TextView) rootView.findViewById(R.id.emptyTxt);
                tv.setVisibility(View.INVISIBLE);
                mAdapter.addAll(result);
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            dialog.setMessage("Fetching items on the aisle...");
//            dialog.show();
        }

        @Override
        protected List<BasketDto> doInBackground(String... params) {
            List<BasketDto> result = new ArrayList<BasketDto>();

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
                JSONArray arr = new JSONArray(JSONResp);
                Log.d(LOG_PREFIX, arr.toString());
                BasketDto bDto;
                BasketModel bModel;
                for (int i = 0; i < arr.length(); i++) {
                    bDto = new BasketDto();
                    bModel = new BasketModel();
                    bModel.setName(arr.getJSONObject(i).getJSONObject("basket").getString("name"));
//                    bModel.set_id("??");
                    bModel.setItemIds(new ArrayList<String>());
                    bDto.setBasketItem(bModel);
                    result.add(bDto);
                }

                Log.d(LOG_PREFIX, String.valueOf(result.size()));

                return result;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

        private String parseItems(JSONObject obj) throws JSONException {

            String name = obj.getString("name");

            return name;
        }

    }
*/
    public class BasketListAdapter extends ArrayAdapter<BasketDto>  {

        android.app.FragmentManager fm;
        public BasketListAdapter(Context ctx,
                                List<BasketDto> itemList) {
            super(ctx, R.layout.basket_names, itemList);
            fm = getActivity().getFragmentManager();
        }

        public View getView(final int position, View convertedView, final ViewGroup parent) {
            View v = convertedView;
            LayoutInflater inflater = (LayoutInflater) parentCtx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.basket_names, parent, false);

            BasketDto basket = list.get(position);
            TextView bName = (TextView) v.findViewById(R.id.basketNames);
            bName.setText(basket.getBasket().getName());
            ImageButton del = (ImageButton) v.findViewById(R.id.basketDelBtn);
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectItem(position,"Delete");
                    DeletionAlertDialog newFragment = DeletionAlertDialog.newInstance(
                            android.R.string.dialog_alert_title, position);
                    newFragment.show(fm, "dialog");

                }
            });
            final ImageButton edit = (ImageButton) v.findViewById(R.id.basketEditBtn);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectItem(position,"Edit");
                    EditingDialog editDiolog =  EditingDialog.newInstance(R.string.rename, position);
                    editDiolog.show(fm,"Edit");
                }
            });
            return v;
        }
    }

    public static class DeletionAlertDialog extends DialogFragment {
        Context mContext;
        public DeletionAlertDialog(){
            mContext = getActivity();
        }
        public static DeletionAlertDialog newInstance(int title, int position) {
            DeletionAlertDialog frag = new DeletionAlertDialog();
            Bundle args = new Bundle();
            args.putInt("title", title);
            args.putInt("position",position);
            frag.setArguments(args);
            return frag;
        }
        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle("Deletion Confirmation")
                    .setMessage("Delete this basket permanently?")
                    .setIcon(android.R.drawable.ic_menu_delete)
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing (will close dialog)
                            Log.i("FragmentAlertDialog", "Negative click!"+getArguments().getInt("position"));
                        }
                    })
                    .setPositiveButton(android.R.string.yes,  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do something
                            BasketDto clickedBasket = list.get(getArguments().getInt("position"));
                            List<LinkDto> clickedLinks = clickedBasket.getLinks();
                            for (LinkDto link : clickedLinks){
                                if (link.getMethod().equals("DELETE")){
                                    (new BasketDeleteRequestTask())
                                            .execute(getString(R.string.awsLink)+link.getHref(),String.valueOf(getArguments().getInt("position")));
                                    break;
                                }
                            }
//                            list.remove(getArguments().getInt("position"));
//                            mAdapter.notifyDataSetChanged();
                            Log.i("FragmentAlertDialog", "Positive click! "+getArguments().getInt("position"));
                        }
                    })
                    .create();
        }
    }
    public static class EditingDialog extends DialogFragment {
        Context mContext;
        public EditingDialog(){
            mContext = getActivity();
        }
        public static EditingDialog newInstance(int title, int position) {
            EditingDialog frag = new EditingDialog();
            Bundle args = new Bundle();
            args.putInt("title", title);
            args.putInt("position",position);
            frag.setArguments(args);
            return frag;
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.edit_name_fragment,container);
            final EditText text = (EditText) view.findViewById(R.id.nameEdit);
            String oldName = list.get(getArguments().getInt("position")).getBasket().getName();
            text.setText(oldName);
            text.requestFocus();
            getDialog().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            getDialog().setTitle(getArguments().getInt("title"));
            ImageButton btn = (ImageButton) view.findViewById(R.id.renameDone);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BasketDto basket = list.get(getArguments().getInt("position"));
                    basket.getBasket().setName(text.getText().toString());
                    list.set(getArguments().getInt("position"),basket);
                    mAdapter.notifyDataSetChanged();
                    dismiss();
                }
            });
            return view;

        }
    }

    private class BasketListGetRequestTask extends AsyncTask<String, Void, BasketDto[]> {
        @Override
        protected BasketDto[] doInBackground(String... params) {
            try {
                final String url = params[0];

                RestTemplate restTemplate = new RestTemplate();
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                restTemplate.getMessageConverters().add(mapper);
                BasketDto[] baskets = restTemplate.getForObject(url, BasketDto[].class);
                return baskets;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(BasketDto[] baskets) {
            if (baskets != null) {
                TextView tv = (TextView) rootView.findViewById(R.id.emptyTxt);
                tv.setVisibility(View.INVISIBLE);
                //TODO: Clearing the list is not right look for a better solution
                mAdapter.clear();
                mAdapter.addAll(baskets);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class BasketPostRequestTask extends AsyncTask<String, Void, BasketDto> {
        @Override
        protected BasketDto doInBackground(String... params) {
            try {
                String url= params[0];
                RestTemplate restTemplate = new RestTemplate();
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                restTemplate.getMessageConverters().add(mapper);
                Map<String,String> bName = new HashMap<String, String>();
                bName.put("name",edit.getText().toString());
                BasketDto basket = restTemplate.postForObject(url,bName,BasketDto.class);
                return basket;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(BasketDto basket) {
            if (basket != null) {
                TextView tv = (TextView) rootView.findViewById(R.id.emptyTxt);
                tv.setVisibility(View.INVISIBLE);
                edit.setText("");
                //TODO: Clearing the list is not right look for a better solution
                mAdapter.add(basket);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
    private static class BasketDeleteRequestTask extends AsyncTask<String, Void, String> {

        int position;
        @Override
        protected String doInBackground(String... params) {
            try {
                String url= params[0];
                position = Integer.parseInt(params[1]);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.delete(url);
                return "";
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String dummy) {
            //TODO: Clearing the list is not right look for a better solution
            list.remove(position);
            mAdapter.notifyDataSetChanged();
        }
    }
}

