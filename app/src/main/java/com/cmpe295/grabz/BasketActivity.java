package com.cmpe295.grabz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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


public class BasketActivity extends Activity {

    static Context parentCtx;
    static ArrayList<ItemDto> searchedItems = new ArrayList<ItemDto>();
    static ArrayList<ItemDto> basketItems = new ArrayList<ItemDto>();
    static SearchItemAdapter searchAdapter;
    static BasketItemAdapter basketAdapter;
    String getLink;
    static String putLink;
    String delLink;
    static String HOST;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentCtx = this;
        setContentView(R.layout.activity_basket);
        Intent intentExtras = getIntent();
        Bundle bundle = intentExtras.getExtras();
        getLink = bundle.getString("basketGet","No Get");
        putLink = bundle.getString("basketPut","No Put");
        delLink = bundle.getString("basketDel","No Del");
        setTitle(bundle.getString("basketName","Unnamed"));
        HOST = getString(R.string.awsLink);
        (new BasketItemGetRequestTask()).execute(HOST+getLink);
        final ListView lv = (ListView) findViewById(R.id.basketItemList);
        //TODO: New adaptor that has item, aisle#
        basketAdapter = new BasketItemAdapter(parentCtx,basketItems);
        lv.setAdapter(basketAdapter);
        
/*
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_basket, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.addItem:
                new AddItemDiolog().show(getFragmentManager(), "Tag");
                return true;
            case R.id.action_settings:
                return true;
            default:
                super.onOptionsItemSelected(item);
                return true;
        }
    }

    public static class AddItemDiolog extends DialogFragment {
        public AddItemDiolog() {
        }
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.fargment_add_item,null);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(view);
            builder.setTitle("Add Item");
//            getDialog().setTitle("Add Item");
            SearchView search = (SearchView) view.findViewById(R.id.searchText);
            search.setQueryHint("Start typing to search...");
            final ListView lv = (ListView) view.findViewById(R.id.searchList);
            searchAdapter = new SearchItemAdapter(parentCtx,
                    searchedItems);
            lv.setAdapter(searchAdapter);
            lv.setOnItemClickListener(new ListItemClickListener());
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.length() > 3){
                        (new SearchItemGetRequestTask()).execute(HOST
                                +"/items?searchText="+newText);
                        lv.setVisibility(view.VISIBLE);
                    }
                    else{
                        lv.setVisibility(view.INVISIBLE);
                        searchedItems.clear();
                        searchAdapter.notifyDataSetChanged();
                    }
                    return false;
                }
            });
            builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    searchedItems.clear();
                    searchAdapter.notifyDataSetChanged();
                    dismiss();
                }
            });
            return builder.create();
        }
    }

    private static void selectItem(int position, String msg) {
        Toast.makeText(
                parentCtx,
                msg + " [" + position
                        + "]", Toast.LENGTH_SHORT).show();
    }

    public static class SearchItemAdapter extends ArrayAdapter<ItemDto>  {
        public SearchItemAdapter (Context ctx,
                                  List<ItemDto> itemList) {
            super(ctx, R.layout.fragment_search_item, itemList);
        }
        public View getView(final int position, View convertedView, final ViewGroup parent) {
            View v = convertedView;
            LayoutInflater inflater = (LayoutInflater) parentCtx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.fragment_search_item, parent, false);
            ItemDto item = searchedItems.get(position);
            TextView itemName = (TextView) v.findViewById(R.id.searchItemName);
            itemName.setText(item.getItem().getName());

            return v;
        }
    }

    private static class ListItemClickListener implements
            AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            (new AddItemRequestTask()).execute(HOST + putLink,String.valueOf(position),
                    searchedItems.get(position).getItem().getItemId());
        }
    }
    private static class SearchItemGetRequestTask extends AsyncTask<String, Void, ItemDto[]> {
        HttpStatus responseCode;

        @Override
        protected ItemDto[] doInBackground(String... params) {
            try {
                final String url = params[0];
                Log.d("URI Search",url);
                // Set the Accept header
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
                HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                restTemplate.getMessageConverters().add(mapper);
                ResponseEntity<ItemDto[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity,ItemDto[].class);
                ItemDto[] items = responseEntity.getBody();
                responseCode =  responseEntity.getStatusCode();
                Log.d("GET Task on", url+" " + responseCode.toString());
                return items;
            }catch (Exception e) {
                Log.e("SearchItemGetTask", e.getMessage(), e);
                return null;
            }
        }
        @Override
        protected void onPostExecute(ItemDto[] items) {
            searchAdapter.clear();
            if (items.length != 0 && responseCode == HttpStatus.OK) {
                searchAdapter.addAll(items);
            }
            searchAdapter.notifyDataSetChanged();
        }
    }

    private static class AddItemRequestTask extends AsyncTask<String, Void, String> {
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
                bName.put("action","add_item");
                bName.put("itemId", name);
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
                basketItems.add(searchedItems.get(position));
                basketAdapter.notifyDataSetChanged();
            }
        }
    }
    public static class BasketItemAdapter extends ArrayAdapter<ItemDto> {
        public BasketItemAdapter(Context ctx,
                                 List<ItemDto> itemList) {
            super(ctx, R.layout.fragment_search_item, itemList);
        }

        public View getView(final int position, View convertedView, final ViewGroup parent) {
            View v = convertedView;
            LayoutInflater inflater = (LayoutInflater) parentCtx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.basket_item_row_layout, parent, false);
            ItemDto item = basketItems.get(position);
            TextView itemName = (TextView) v.findViewById(R.id.basketItemName);
            itemName.setText(item.getItem().getName());
            TextView itemAisle = (TextView) v.findViewById(R.id.basketItemAisle);
            //TODO: get the Ailse after NFC tap detected.

            return v;
        }
    }

    private static class BasketItemGetRequestTask extends AsyncTask<String, Void, ItemDto[]> {
        HttpStatus responseCode;

        @Override
        protected ItemDto[] doInBackground(String... params) {
            try {
                final String url = params[0];
                // Set the Accept header
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
                HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                restTemplate.getMessageConverters().add(mapper);
                ResponseEntity<ItemDto[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity,ItemDto[].class);
                ItemDto[] items = responseEntity.getBody();
                responseCode =  responseEntity.getStatusCode();
                Log.d("GET Task on", url+" " + responseCode.toString());
                return items;
            }catch (Exception e) {
                Log.e("SearchItemGetTask", e.getMessage(), e);
                return null;
            }
        }
        @Override
        protected void onPostExecute(ItemDto[] items) {
            basketAdapter.clear();
            searchedItems.clear();
            if (items.length != 0 && responseCode == HttpStatus.OK) {
                basketAdapter.addAll(items);
            }
            basketAdapter.notifyDataSetChanged();
        }
    }
}
