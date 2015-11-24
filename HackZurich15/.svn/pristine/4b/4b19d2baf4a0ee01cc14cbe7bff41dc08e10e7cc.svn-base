package com.phonewars;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class CustomListAdapter extends ArrayAdapter<String> {
 
	private final Activity context;
	private final String[] itemname;
	//private final Integer[] imgid;
	private final Drawable[] imgid;
	
	public CustomListAdapter(Activity context, String[] itemname, Drawable[] imgid) {//Integer[]
		super(context, R.layout.mylist, itemname);		
		this.context=context;
		this.itemname=itemname;
		this.imgid=imgid;
	}
	
	public View getView(int position,View view,ViewGroup parent) {
		LayoutInflater inflater=context.getLayoutInflater();
		View rowView=inflater.inflate(R.layout.mylist, null,true);
		
		TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);
		
		String[] parts = itemname[position].split(": ");
		
		txtTitle.setText(parts[1]);
		//imageView.setImageResource(imgid[position]);
		imageView.setImageDrawable(imgid[position]);
		extratxt.setText("Time: "+parts[0]);
		return rowView;
		
	};
}
