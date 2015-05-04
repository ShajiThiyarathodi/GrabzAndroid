package com.cmpe295.grabz.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpe295.grabz.Dto.BasketItemDetailDto;
import com.cmpe295.grabz.Dto.ItemDto;
import com.cmpe295.grabz.R;
import com.cmpe295.grabz.domain.BasketItemDetail;
import com.cmpe295.grabz.fragment.AisleItemsFragment;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BasketActivity extends Activity {

    static Context parentCtx;
    static ArrayList<ItemDto> searchedItems = new ArrayList<ItemDto>();
    static ArrayList<BasketItemDetailDto> basketItems = new ArrayList<BasketItemDetailDto>();
    static SearchItemAdapter searchAdapter;
    static BasketItemAdapter basketAdapter;
    String getLink;
    static String putLink;
    static String delLink;
    static String HOST;
    static private TextView emptyMsg;
    public static final String ITEM_ID = "Item_Id";
//    static Map<String,Boolean> basketItemIds = new HashMap<String, Boolean>();
//    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentCtx = this;
        setContentView(R.layout.activity_basket);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intentExtras = getIntent();
        Bundle bundle = intentExtras.getExtras();
        getLink = bundle.getString("basketGet","No Get");
        putLink = bundle.getString("basketPut","No Put");
        delLink = bundle.getString("basketDel","No Del");
        setTitle(bundle.getString("basketName","Unnamed"));
        HOST = getString(R.string.awsLink);
        (new BasketItemGetRequestTask()).execute(HOST+getLink);
        final ListView lv = (ListView) findViewById(R.id.basketItemList);
        basketAdapter = new BasketItemAdapter(parentCtx,basketItems);
        lv.setAdapter(basketAdapter);
        lv.setOnItemClickListener(new ItemListItemClickListener());
        emptyMsg = (TextView) findViewById(R.id.listEmptyTxt);
        if (basketItems.size() == 0)
            emptyMsg.setVisibility(View.VISIBLE);
        else
            emptyMsg.setVisibility(View.INVISIBLE);
        
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
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.deleteAllItem:
                (new DeleteAllItemRequestTask()).execute(HOST+putLink);
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
            SearchView search = (SearchView) view.findViewById(R.id.searchText);
            search.setQueryHint("Start typing to search...");
            search.setIconifiedByDefault(false);
            final ListView lv = (ListView) view.findViewById(R.id.searchList);
            searchAdapter = new SearchItemAdapter(parentCtx,
                    searchedItems);
            lv.setAdapter(searchAdapter);
            lv.setOnItemClickListener(new SearchListItemClickListener());
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.length() > 1){
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

    private static class SearchListItemClickListener implements
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
        long start,end;
        @Override
        protected ItemDto[] doInBackground(String... params) {
            try {
                start = System.currentTimeMillis();
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
            end = System.currentTimeMillis();
            Log.d("Time to Search", end-start+" ms");
            if (items.length != 0 && responseCode == HttpStatus.OK) {
                searchAdapter.addAll(items);
            }
            searchAdapter.notifyDataSetChanged();
        }
    }

    private static class AddItemRequestTask extends AsyncTask<String, Void, String> {
        int position;
        String itemId;
        HttpStatus responseCode;
        long start,end;

        @Override
        protected String doInBackground(String... params) {
            try {
                start = System.currentTimeMillis();
                String url= params[0];
                position = Integer.parseInt(params[1]);
                itemId = params[2];
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setContentType(new MediaType("application","json"));
                RestTemplate restTemplate = new RestTemplate();
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                restTemplate.getMessageConverters().add(mapper);
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                Map<String,String> bName = new HashMap<String, String>();
                bName.put("action","add_item");
                bName.put("itemId", itemId);
                HttpEntity<Map> requestEntity = new HttpEntity<Map>(bName,requestHeaders);
                ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity,String.class);
//                restTemplate.put(url,bName);
                responseCode =  responseEntity.getStatusCode();
                Log.d("PUT Task on", url+" " + responseCode.toString() + " " + responseEntity.getBody());
                return "";
            }
            catch (HttpClientErrorException e){
                Log.e("AddItemRequestTask", e.getMessage(), e);
                responseCode = e.getStatusCode();
            }
            catch (Exception e) {
                Log.e("AddItemRequestTask", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String dummy) {
            end = System.currentTimeMillis();
            Log.d("Time to Add Item", end-start+" ms");
            if (responseCode == HttpStatus.OK) {
                ItemDto desirableItem = searchedItems.get(position);
                BasketItemDetailDto newBasketItem = new BasketItemDetailDto();
                newBasketItem.setLinks(desirableItem.getLinks());
                newBasketItem.setBasketItemDetail(new BasketItemDetail(
                        desirableItem.getItem().getItemId(),desirableItem.getItem().getName(),
                        false,"?"));
                basketItems.add(newBasketItem);
                basketAdapter.notifyDataSetChanged();
                emptyMsg.setVisibility(View.INVISIBLE);
                Toast.makeText(parentCtx,
                        "Item Added", Toast.LENGTH_SHORT).show();
            }
            else if (responseCode == HttpStatus.CONFLICT){
                Toast.makeText(parentCtx,
                        "Item's been already added", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public static class BasketItemAdapter extends ArrayAdapter<BasketItemDetailDto> {
        public BasketItemAdapter(Context ctx,
                                 List<BasketItemDetailDto> itemList) {
            super(ctx, R.layout.fragment_search_item, itemList);
        }

        public View getView(final int position, View convertedView, final ViewGroup parent) {
            View v = convertedView;
            LayoutInflater inflater = (LayoutInflater) parentCtx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.basket_item_row_layout, parent, false);
            BasketItemDetailDto item = basketItems.get(position);
            final TextView itemName = (TextView) v.findViewById(R.id.basketItemName);
            itemName.setText(item.getBasketItemDetail().getName());
            ImageButton delete = (ImageButton)v.findViewById(R.id.itemDelBtn);
            delete.setFocusable(false);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    (new DeleteItemRequestTask()).execute(HOST +delLink ,String.valueOf(position),
                            basketItems.get(position).getBasketItemDetail().getItemId());
                }
            });
            CheckBox check = (CheckBox) v.findViewById(R.id.basketItemCB);
            check.setChecked(basketItems.get(position).getBasketItemDetail().getCollected());
            if (check.isChecked()) {
                v.setBackgroundResource(R.drawable.item_checked_background);
                itemName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else {
                v.setBackgroundResource(R.drawable.item_unchecked_background);
            }
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    View row = (View) buttonView.getParent();
                    if (isChecked) {
                        row.setBackgroundResource(R.drawable.item_checked_background);
                        itemName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        (new CheckItemRequestTask()).execute(HOST +putLink ,String.valueOf(position),
                                basketItems.get(position).getBasketItemDetail().getItemId());
                        basketItems.get(position).getBasketItemDetail().setCollected(true);
                    } else {
                        row.setBackgroundResource(R.drawable.item_unchecked_background);
                        //android.R.color.transparent
                        itemName.setPaintFlags(itemName.getPaintFlags()
                                & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        (new UncheckItemRequestTask()).execute(HOST +putLink ,String.valueOf(position),
                                basketItems.get(position).getBasketItemDetail().getItemId());
                        basketItems.get(position).getBasketItemDetail().setCollected(false);
                    }
                }
            });
            TextView itemAisle = (TextView) v.findViewById(R.id.basketItemAisle);
            itemAisle.setText(basketItems.get(position).getBasketItemDetail().getAisleNum());
            return v;
        }
    }

    private static class BasketItemGetRequestTask extends AsyncTask<String, Void, BasketItemDetailDto[]> {
        HttpStatus responseCode;
        long start,end;

        @Override
        protected BasketItemDetailDto[] doInBackground(String... params) {
            try {
                start = System.currentTimeMillis();
                final String url = params[0];
                // Set the Accept header
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
                HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                restTemplate.getMessageConverters().add(mapper);
                ResponseEntity<BasketItemDetailDto[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity,BasketItemDetailDto[].class);
                BasketItemDetailDto[] items = responseEntity.getBody();
                responseCode =  responseEntity.getStatusCode();
                Log.d("GET Task on", url+" " + responseCode.toString());
                return items;
            }catch (Exception e) {
                Log.e("BasketItemGet", e.getMessage(), e);
                return null;
            }
        }
        @Override
        protected void onPostExecute(BasketItemDetailDto[] items) {
            end = System.currentTimeMillis();
            Log.d("Time to Get B-Items", end-start+" ms");
            basketAdapter.clear();
            if (items != null && responseCode == HttpStatus.OK) {
                if (items.length != 0) {
                    basketAdapter.addAll(items);
                    basketAdapter.notifyDataSetChanged();
                    emptyMsg.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private static class DeleteItemRequestTask extends AsyncTask<String, Void, String> {
        int position;
        String itemId;
        HttpStatus responseCode;
        long start,end;

        @Override
        protected String doInBackground(String... params) {
            try {
                start = System.currentTimeMillis();
                String url= params[0];
                position = Integer.parseInt(params[1]);
                itemId = params[2];
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setContentType(new MediaType("application","json"));
                RestTemplate restTemplate = new RestTemplate();
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                restTemplate.getMessageConverters().add(mapper);
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                Map<String,String> bName = new HashMap<String, String>();
                bName.put("action","remove_item");
                bName.put("itemId", itemId);
                HttpEntity<Map> requestEntity = new HttpEntity<Map>(bName,requestHeaders);
                ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity,String.class);
//                restTemplate.put(url,bName);
                responseCode =  responseEntity.getStatusCode();
                Log.d("Delete Task on", url+" " + responseCode.toString() + " " + responseEntity.getBody());
                return "";
            } catch (Exception e) {
                Log.e("DeleteItemRequestTask", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String dummy) {
            end = System.currentTimeMillis();
            Log.d("Time to DeleteItem", end-start+" ms");
            if (responseCode == HttpStatus.OK) {
//                basketItemIds.put(basketItems.get(position).getBasketItemDetail().getItemId(),false);
                basketItems.remove(position);
                basketAdapter.notifyDataSetChanged();
                if (basketItems.size()==0){
                    emptyMsg.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private static class CheckItemRequestTask extends AsyncTask<String, Void, String> {
        int position;
        String itemId;
        HttpStatus responseCode;
        long start,end;

        @Override
        protected String doInBackground(String... params) {
            try {
                start = System.currentTimeMillis();
                String url= params[0];
                position = Integer.parseInt(params[1]);
                itemId = params[2];
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setContentType(new MediaType("application","json"));
                RestTemplate restTemplate = new RestTemplate();
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                restTemplate.getMessageConverters().add(mapper);
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                Map<String,String> bName = new HashMap<String, String>();
                bName.put("action","set_collected");
                bName.put("itemId", itemId);
                HttpEntity<Map> requestEntity = new HttpEntity<Map>(bName,requestHeaders);
                ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity,String.class);
//                restTemplate.put(url,bName);
                responseCode =  responseEntity.getStatusCode();
                Log.d("Check Task on", url+" " + responseCode.toString() + " " + responseEntity.getBody());
                return "";
            } catch (Exception e) {
                Log.e("CheckItemRequestTask", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String dummy) {
            end = System.currentTimeMillis();
            Log.d("Time to CheckItem", end-start+" ms");
            if (responseCode != HttpStatus.OK) {
                Log.e("Check  FAILED","Inside check item. Failed to check"+ basketItems.get(position).getBasketItemDetail().getName());
            }
        }
    }
    private static class UncheckItemRequestTask extends AsyncTask<String, Void, String> {
        int position;
        String itemId;
        HttpStatus responseCode;
        long start,end;

        @Override
        protected String doInBackground(String... params) {
            try {
                start = System.currentTimeMillis();
                String url= params[0];
                position = Integer.parseInt(params[1]);
                itemId = params[2];
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setContentType(new MediaType("application","json"));
                RestTemplate restTemplate = new RestTemplate();
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                restTemplate.getMessageConverters().add(mapper);
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                Map<String,String> bName = new HashMap<String, String>();
                bName.put("action","unset_collected");
                bName.put("itemId", itemId);
                HttpEntity<Map> requestEntity = new HttpEntity<Map>(bName,requestHeaders);
                ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity,String.class);
//                restTemplate.put(url,bName);
                responseCode =  responseEntity.getStatusCode();
                Log.d("Check Task on", url+" " + responseCode.toString() + " " + responseEntity.getBody());
                return "";
            } catch (Exception e) {
                Log.e("UncheckItemRequestTask", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String dummy) {
            end = System.currentTimeMillis();
            Log.d("Time to UnCheckItem", end-start+" ms");
            if (responseCode != HttpStatus.OK) {
                Log.e("Uncheck  FAILED","Inside check item. Failed to uncheck"+ basketItems.get(position).getBasketItemDetail().getName());
            }
        }
    }
    private class ItemListItemClickListener implements
            AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Intent intent = new Intent(parentCtx,
                    ItemDetailActivity.class);

            intent.putExtra(ITEM_ID, basketItems.get(position).getBasketItemDetail().getItemId());
            intent.putExtra(AisleItemsFragment.SOURCE, "basketItem");
            startActivity(intent);

        }
    }

    private static class DeleteAllItemRequestTask extends AsyncTask<String, Void, String> {
        int position;
        String itemId;
        HttpStatus responseCode;
        long start,end;

        @Override
        protected String doInBackground(String... params) {
            try {
                start = System.currentTimeMillis();
                String url= params[0];
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setContentType(new MediaType("application","json"));
                RestTemplate restTemplate = new RestTemplate();
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                restTemplate.getMessageConverters().add(mapper);
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                Map<String,String> bName = new HashMap<String, String>();
                bName.put("action","remove_all_items");
                HttpEntity<Map> requestEntity = new HttpEntity<Map>(bName,requestHeaders);
                ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity,String.class);
//                restTemplate.put(url,bName);
                responseCode =  responseEntity.getStatusCode();
                Log.d("Delete ALL Task on", url+" " + responseCode.toString() + " " + responseEntity.getBody());
                return "";
            } catch (Exception e) {
                Log.e("DeleteItemRequestTask", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String dummy) {
            end = System.currentTimeMillis();
            Log.d("Time to DeleteItem", end-start+" ms");
            if (responseCode == HttpStatus.OK) {
                Toast.makeText(parentCtx,"All items deleted successfully",Toast.LENGTH_SHORT);
                basketItems.clear();
                basketAdapter.notifyDataSetChanged();
            }
            else
                Toast.makeText(parentCtx,"All items deletion failed!",Toast.LENGTH_SHORT);
        }
    }
}

/*
* Clean all items call
* introduce checkbox boolean for each item in basket to persist item status
* introduce aisleNumber for each item in basket
* nested API call to set aisles number in each basket on NFC tap
*
*
xxxhdpi: 1280x1920 px
xxhdpi: 960x1600 px
xhdpi: 640x960 px
hdpi: 480x800 px
mdpi: 320x480 px
ldpi: 240x320 px
* */