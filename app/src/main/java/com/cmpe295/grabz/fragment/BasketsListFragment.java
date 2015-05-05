package com.cmpe295.grabz.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpe295.grabz.Dto.BasketDto;
import com.cmpe295.grabz.Dto.LinkDto;
import com.cmpe295.grabz.R;
import com.cmpe295.grabz.activity.BasketActivity;
import com.cmpe295.grabz.activity.MainActivity;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
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
    static View rootView;
    EditText edit;
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
        parentCtx = getActivity().getApplicationContext();
        rootView = inflater.inflate(R.layout.baskets_list, container,
                false);
        SharedPreferences settings = parentCtx.getSharedPreferences(MainActivity.PREFS_NAME, 0);
        final String deviceId = settings.getString("deviceId", null);
/*        TelephonyManager telephonyManager = (TelephonyManager)this.
                getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId();
        Log.d("DEVICE ID",deviceId);*/
        ListView lView = (ListView) rootView.findViewById(R.id.basketList);
        TextView emptyMsg = (TextView) rootView.findViewById(R.id.emptyTxt);
        if (deviceId!= null)
            (new BasketListGetRequestTask())
                .execute("http://grabztestenv.elasticbeanstalk.com/baskets?phoneId="+deviceId);
        if (list.size() == 0)
            emptyMsg.setVisibility(View.VISIBLE);
        else
            emptyMsg.setVisibility(View.INVISIBLE);

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new BasketListAdapter(parentCtx,list);
        lView.setAdapter(mAdapter);
        lView.setOnItemClickListener(new ListItemClickListener());
        ImageButton btn = (ImageButton) rootView.findViewById(R.id.btnAdd);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                edit = (EditText) rootView.findViewById(R.id.txtItem);
                if (!edit.getText().toString().equals("")) {
                    (new BasketPostRequestTask())
                            .execute("http://grabztestenv.elasticbeanstalk.com/baskets?phoneId="+deviceId);
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
            //TODO: Initiate new activity
            Intent basketActivity = new Intent(parentCtx,BasketActivity.class);
            BasketDto selectedBasket = list.get(position);
            String basketGet = null;
            String basketDel = null;
            String basketPut = null;
            List<LinkDto> clickedLinks = selectedBasket.getLinks();
            for (LinkDto link : clickedLinks){
                if (link.getMethod().equals("GET")){
                    basketGet = link.getHref();
                }
                if (link.getMethod().equals("DELETE")){
                    basketDel = link.getHref();
                }
                if (link.getMethod().equals("PUT")){
                    basketPut = link.getHref();
                }
            }
            Bundle bundle = new Bundle();
            bundle.putString("basketName",selectedBasket.getBasket().getName());
            bundle.putString("basketGet",basketGet);
            bundle.putString("basketDel",basketDel);
            bundle.putString("basketPut",basketPut);
            basketActivity.putExtras(bundle);
            startActivity(basketActivity);
        }
    }

    private void selectItem(int position, String msg) {
        Toast.makeText(
                parentCtx,
                msg+" [" + position
                        + "]", Toast.LENGTH_SHORT).show();
    }

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
                    DeletionAlertDialog newFragment = DeletionAlertDialog.newInstance(
                            android.R.string.dialog_alert_title, position);
                    newFragment.show(fm, "dialog");

                    }
                });
            final ImageButton edit = (ImageButton) v.findViewById(R.id.basketEditBtn);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
            String basketName = list.get(getArguments().getInt("position")).getBasket().getName();
            return new AlertDialog.Builder(getActivity())
                    .setTitle("Deletion Confirmation")
                    .setMessage("Delete \""+basketName+"\" permanently?")
                    .setIcon(android.R.drawable.ic_menu_delete)
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing (will close dialog)
                            Log.i("FragmentAlertDialog", "Negative click!"+getArguments().getInt("position"));
                        }
                    })
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do something
                            BasketDto clickedBasket = list.get(getArguments().getInt("position"));
                            List<LinkDto> clickedLinks = clickedBasket.getLinks();
                            for (LinkDto link : clickedLinks) {
                                if (link.getMethod().equals("DELETE")) {
                                    (new BasketDeleteRequestTask())
                                            .execute(getString(R.string.awsLink) + link.getHref(), String.valueOf(getArguments().getInt("position")));
                                    break;
                                }
                            }
//                            list.remove(getArguments().getInt("position"));
//                            mAdapter.notifyDataSetChanged();
                            Log.i("FragmentAlertDialog", "Positive click! " + getArguments().getInt("position"));
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
                    BasketDto clickedBasket = list.get(getArguments().getInt("position"));
                    List<LinkDto> clickedLinks = clickedBasket.getLinks();
                    for (LinkDto link : clickedLinks){
                        if (link.getMethod().equals("PUT")){
                            (new BasketRenameRequestTask()).execute(getString(R.string.awsLink)+
                                    link.getHref(),String.valueOf(getArguments().getInt("position"))
                                    ,text.getText().toString());
                            break;
                        }
                    }
                    dismiss();
                }
            });
            return view;
        }
    }

    public static class BasketListGetRequestTask extends AsyncTask<String, Void, BasketDto[]> {
        HttpStatus responseCode;
        @Override
        protected BasketDto[] doInBackground(String... params) {
            try {
                final String url = params[0];
                // Set the Accept header
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
                HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                restTemplate.getMessageConverters().add(mapper);
//                BasketDto[] baskets = restTemplate.getForObject(url, BasketDto[].class);
                ResponseEntity<BasketDto[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity,BasketDto[].class);
                BasketDto[] baskets = responseEntity.getBody();
                responseCode =  responseEntity.getStatusCode();
                Log.d("GET Task on", url+" " + responseCode.toString());
                return baskets;
            } catch (Exception e) {
                Log.e("BasketListGetRequest", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(BasketDto[] baskets) {
            if (baskets != null && responseCode == HttpStatus.OK) {
                if (baskets.length!=0) {
                    TextView tv = (TextView) rootView.findViewById(R.id.emptyTxt);
                    tv.setVisibility(View.INVISIBLE);
                    //TODO: Clearing the list is not right look for a better solution. Shouldn't it be list.addAll or list.clear ?
                    mAdapter.clear();
                    mAdapter.addAll(baskets);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private class BasketPostRequestTask extends AsyncTask<String, Void, BasketDto> {
        HttpStatus responseCode;
        @Override
        protected BasketDto doInBackground(String... params) {
            try {
                String url= params[0];
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
                RestTemplate restTemplate = new RestTemplate();
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                restTemplate.getMessageConverters().add(mapper);
                Map<String,String> bName = new HashMap<String, String>();
                bName.put("name",edit.getText().toString());
                HttpEntity<Map> requestEntity = new HttpEntity<Map>(bName,requestHeaders);
//                BasketDto basket = restTemplate.postForObject(url,bName,BasketDto.class);
                ResponseEntity<BasketDto> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,BasketDto.class);
                BasketDto basket = responseEntity.getBody();
                responseCode =  responseEntity.getStatusCode();
                Log.d("POST Task on", url+" " + responseCode.toString());
                return basket;
            } catch (Exception e) {
                Log.e("BasketPostRequestTask", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(BasketDto basket) {
            if (basket != null && responseCode == HttpStatus.CREATED) {
                TextView tv = (TextView) rootView.findViewById(R.id.emptyTxt);
                tv.setVisibility(View.INVISIBLE);
                edit.setText("");
                list.add(basket);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
    private static class BasketDeleteRequestTask extends AsyncTask<String, Void, String> {
        int position;
        HttpStatus responseCode;
        @Override
        protected String doInBackground(String... params) {
            try {
                String url= params[0];
                position = Integer.parseInt(params[1]);
                // Set the Accept header
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
                HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity,String.class);
                responseCode =  responseEntity.getStatusCode();
                Log.d("Delete Task on", url+" " + responseCode.toString());
                return "";
            } catch (Exception e) {
                Log.e("BasketDeleteRequestTask", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String dummy) {
            if (responseCode == HttpStatus.OK) {
                list.remove(position);
                mAdapter.notifyDataSetChanged();
                if (list.size()==0){
                    TextView tv = (TextView) rootView.findViewById(R.id.emptyTxt);
                    tv.setVisibility(View.VISIBLE);
                }

            }
        }
    }

    private static class BasketRenameRequestTask extends AsyncTask<String, Void, String> {
        int position;
        String name;
        HttpStatus responseCode;
        @Override
        protected String doInBackground(String... params) {
            try {
                String url= params[0];
                position = Integer.parseInt(params[1]);
                name = params[2];
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setContentType(new MediaType("application","json"));
                RestTemplate restTemplate = new RestTemplate();
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                restTemplate.getMessageConverters().add(mapper);
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                Map<String,String> bName = new HashMap<String, String>();
                bName.put("action","rename_basket");
                bName.put("basketName", name);
                HttpEntity<Map> requestEntity = new HttpEntity<Map>(bName,requestHeaders);
                ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity,String.class);
//                restTemplate.put(url,bName);
                responseCode =  responseEntity.getStatusCode();
                Log.d("PUT Task on", url+" " + responseCode.toString() + " " + responseEntity.getBody());
                return "";
            } catch (Exception e) {
                Log.e("BasketRenameRequestTask", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String dummy) {
            if (responseCode == HttpStatus.OK) {
                list.get(position).getBasket().setName(name);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}

