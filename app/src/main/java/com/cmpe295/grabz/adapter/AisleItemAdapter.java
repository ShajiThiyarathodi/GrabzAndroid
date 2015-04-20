package com.cmpe295.grabz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmpe295.grabz.Dto.AisleItemDto;
import com.cmpe295.grabz.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class AisleItemAdapter extends ArrayAdapter<AisleItemDto>  {
	
	private List<AisleItemDto> itemList;
	private Context context;
	private int[] listItemBackground = new int[] { R.drawable.list_background1,
			R.drawable.list_background2 };
    ImageLoader imageLoader;
	
	public List<AisleItemDto> getItemList() {
		return itemList;
	}
	public void setItemList(List<AisleItemDto> itemList) {
		this.itemList = itemList;
		
	}
	
	public AisleItemAdapter(Context ctx,
			List<AisleItemDto> itemList) {
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
            ImageView imageListV = (ImageView) v
                    .findViewById(R.id.thumbImage);
			holder.itemNameView = itemNameListV;
			holder.priceView = priceListV;
            holder.imageView = imageListV;

			v.setTag(holder);
		} else
			holder = (ItemHolder) v.getTag();

		int listItemBackgroundPosition = position % listItemBackground.length;
		v.setBackgroundResource(listItemBackground[listItemBackgroundPosition]);

        AisleItemDto aisleItem = itemList.get(position);
		holder.itemNameView.setText(aisleItem.getAisleItem().getName());
        String price = '$'+ String.valueOf(aisleItem.getAisleItem().getPrice());
		holder.priceView.setText(price);
        String imageUrl = aisleItem.getAisleItem().getImageUrl();
        imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(imageUrl, holder.imageView);

        return v;
	}
	
	private static class ItemHolder {
		public TextView itemNameView;
		public TextView priceView;
        public ImageView imageView;
	}


}
