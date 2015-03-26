package com.cmpe295.grabz;

import java.util.List;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AisleItemAdapter extends ArrayAdapter<AisleItem>  {
	
	private List<AisleItem> itemList;
	private Context context;
	private int[] listItemBackground = new int[] { R.drawable.list_background1,
			R.drawable.list_background2 };
	
	public List<AisleItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<AisleItem> itemList) {
		this.itemList = itemList;
		
	}
	
	public AisleItemAdapter(Context ctx,
			List<AisleItem> itemList) {
		super(ctx, R.layout.item_row_layout, itemList);
		this.itemList = itemList;
		this.context = ctx;
	}
	public View getView(int position, View convertedView, ViewGroup parent) {
		View v = convertedView;
		ItemHolder holder = new ItemHolder();

		if (convertedView == null) {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.item_row_layout, parent, false);

			TextView itemNameListV = (TextView) v.findViewById(R.id.itemName);
			TextView priceListV = (TextView) v
					.findViewById(R.id.price);
			holder.itemNameView = itemNameListV;
			holder.priceView = priceListV;

			v.setTag(holder);
		} else
			holder = (ItemHolder) v.getTag();

		int listItemBackgroundPosition = position % listItemBackground.length;
		v.setBackgroundResource(listItemBackground[listItemBackgroundPosition]);

		AisleItem aisleItem = itemList.get(position);  
		holder.itemNameView.setText(aisleItem.getItemId());
		holder.priceView.setText(String.valueOf(aisleItem.getPrice()));

		return v;
	}
	
	private static class ItemHolder {
		public TextView itemNameView;
		public TextView priceView;
	}


}
