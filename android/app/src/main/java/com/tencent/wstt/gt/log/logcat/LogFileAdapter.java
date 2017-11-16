package com.tencent.wstt.gt.log.logcat;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.utils.GTUtils;

public class LogFileAdapter extends ArrayAdapter<CharSequence> {
	
	private List<CharSequence> objects;
	private int checked;
	private boolean multiMode;
	private boolean[] checkedItems;
	private int resId;
	
	public LogFileAdapter(Context context, List<CharSequence> objects, int checked, boolean multiMode) {
		
		super(context, -1, objects);
		this.objects = objects;
		this.checked = checked;
		this.multiMode = multiMode;
		if (multiMode) {
			checkedItems = new boolean[objects.size()];
		}
		resId = multiMode? R.layout.pi_checkbox_dropdown_filename : R.layout.pi_spinner_dropdown_filename;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		Context context = parent.getContext();
		
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(resId, parent, false);
		}
		
		CheckedTextView text1 = (CheckedTextView) view.findViewById(android.R.id.text1);
		TextView text2 = (TextView) view.findViewById(android.R.id.text2);
		
		CharSequence filename = objects.get(position);

		text1.setText(filename);
		
		
		if (multiMode) {
			text1.setChecked(checkedItems[position]);
		} else {
			text1.setChecked(checked == position);
		}
		
		Date lastModified = SaveLogHelper.getLastModifiedDate(filename.toString());	
		text2.setText(GTUtils.getGpsSaveTime(lastModified));
		
		return view;
	}
	
	public void checkOrUncheck(int position) {
		checkedItems[position] = !checkedItems[position];
		notifyDataSetChanged();
	}
	
	public boolean[] getCheckedItems() {
		return checkedItems;
	}
}
