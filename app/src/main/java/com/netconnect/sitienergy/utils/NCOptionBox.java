package com.netconnect.sitienergy.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.netconnect.sitienergy.R;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class NCOptionBox extends LinearLayout {

	private Context context;
	private LayoutParams params;

	private Map<Integer, TextView> itemMap = new LinkedHashMap<Integer, TextView>();
	private Set<Integer> allIdSet = new HashSet<Integer>();
	private Set<Integer> selectedIdSet = new HashSet<Integer>();
	private Set<Integer> disabledIdSet = new HashSet<Integer>();

	private int defaultItem[];
	private boolean multiSelect;
	private boolean mandatory;

	private OnValueChangeListener OnValueChangeListener;

	public NCOptionBox(Context context, AttributeSet attrs, int defStyleAttr) {

		super(context, attrs, defStyleAttr);
		this.context = context;
	}

	public NCOptionBox(Context context, AttributeSet attrs) {

		super(context, attrs);
		this.context = context;
	}

	public NCOptionBox(Context context) {

		super(context);
		this.context = context;
	}

	public void init(LinkedHashMap<Integer, String> map, String defaultItem, boolean multiSelect, boolean mandatory) {

		if(!NCUtils.isAny(defaultItem))
			this.defaultItem = NCUtils.stringToIntArray(defaultItem);

		this.multiSelect = multiSelect;
		this.mandatory = mandatory;

		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;

		for(Map.Entry<Integer, String> entry : map.entrySet()) {
			final int key = entry.getKey();

			TextView textView = new TextView(context);
			textView.setId(key);
			textView.setText(entry.getValue());
			textView.setTextSize(13);
			itemMap.put(key, textView);
			allIdSet.add(key);

			textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if(disabledIdSet.contains(key))
						return;

					if(selectedIdSet.contains(key)) {
						if(!isMandatory() || selectedIdSet.size() > 1)
							selectedIdSet.remove(key);
					}
					else {
						if(isMultiSelect())
							selectedIdSet.add(key);
						else {
							selectedIdSet.clear();
							selectedIdSet.add(key);
						}
					}
					refresh();
				}
			});
		}
		for(int i = 0; this.defaultItem != null && i < this.defaultItem.length; i++)
			selectedIdSet.add(this.defaultItem[i]);
	}

	public void setSelectedKeys(String ids) {

		this.selectedIdSet = NCUtils.toIntSet(ids);
		refresh();
	}

	public void setSelectedKey(int id) {

		this.selectedIdSet.clear();
		this.selectedIdSet.add(id);
		refresh();
	}

	public void disable(int id) {

		if(id == -1) {
			disabledIdSet.clear();
			for(int key : allIdSet)
				itemMap.get(key).setTextColor(getResources().getColor(R.color.black));
		}
		else {
			disabledIdSet.add(id);
			for(int key : disabledIdSet)
				itemMap.get(key).setTextColor(getResources().getColor(R.color.grey_hint));
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		super.onLayout(changed, l, t, r, b);
		if(itemMap.size() > 0) {
			removeAllViews();
			int pWidth = getWidth();
			int cWidth = (pWidth / itemMap.size());

			for(Map.Entry<Integer, TextView> entry : itemMap.entrySet()) {
				TextView textView = entry.getValue();
				textView.setWidth(cWidth);
				textView.setLayoutParams(params);
				textView.setGravity(Gravity.CENTER);
				addView(textView);
				params.setMargins(1, 0, 0, 0);
			}
			refresh();
		}
	}

	private boolean isMandatory() {

		return mandatory;
	}

	private boolean isMultiSelect() {

		return multiSelect;
	}

	public String getSelectedKeys() {

		return NCUtils.toString(selectedIdSet);
	}

	public int getSelectedKey() {

		return NCUtils.toInteger(getSelectedKeys());
	}

	public void refresh() {

		for(int id : allIdSet) {
			TextView textView = itemMap.get(id);
			if(selectedIdSet.contains(id)) {
				textView.setTextColor(getResources().getColor(R.color.white));
				textView.setBackgroundColor(getResources().getColor(R.color.red));
			}
			else {
				textView.setTextColor(getResources().getColor(R.color.black));
				textView.setBackgroundColor(getResources().getColor(R.color.iob_unselected));
			}
		}

		if(OnValueChangeListener != null)
			OnValueChangeListener.onChange(this);
	}

	public OnValueChangeListener getOnValueChangeListener() {

		return OnValueChangeListener;
	}

	public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener) {

		OnValueChangeListener = onValueChangeListener;
	}

	public interface OnValueChangeListener {

		public void onChange(NCOptionBox v);
	}

	public void reset() {

		selectedIdSet.clear();
		for(int i = 0; this.defaultItem != null && i < this.defaultItem.length; i++)
			selectedIdSet.add(this.defaultItem[i]);
		refresh();
	}
}
