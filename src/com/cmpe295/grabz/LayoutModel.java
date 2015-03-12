package com.cmpe295.grabz;

import java.util.ArrayList;
import java.util.List;

public class LayoutModel {

	private String _id;

	private List<AisleItem> aisleItems = new ArrayList<AisleItem>();

	private String aisleNum;

	private String tagId;

	private String outletId;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public List<AisleItem> getAisleItems() {
		return aisleItems;
	}

	public void setAisleItems(List<AisleItem> aisleItems) {
		this.aisleItems = aisleItems;
	}

	public String getAisleNum() {
		return aisleNum;
	}

	public void setAisleNum(String aisleNum) {
		this.aisleNum = aisleNum;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public LayoutModel(String _id, List<AisleItem> aisleItems, String aisleNum,
			String tagId, String outletId) {
		super();
		this._id = _id;
		this.aisleItems = aisleItems;
		this.aisleNum = aisleNum;
		this.tagId = tagId;
		this.outletId = outletId;
	}

	public String getOutletId() {
		return outletId;
	}

	public void setOutletId(String outletId) {
		this.outletId = outletId;
	}

}
