package com.JTA.recipeshoppinglist;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class IngredientIndivAdapter extends ArrayAdapter<String> {
	
	static class ViewHolder{
		CheckBox text;
	}
	
	ArrayList<String> list = new ArrayList<String>();
	Context context;
	ViewHolder viewHolder;
	ArrayList<String> selectedNames = new ArrayList<String>();
	
	public IngredientIndivAdapter(Context context, ArrayList<String> iList){
		super(context,R.layout.ingredient_row,iList);
		this.list=iList;
		this.context=context;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		
		if(convertView == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			convertView = inflater.inflate(R.layout.ingredient_row, parent, false);
		
			viewHolder = new ViewHolder();
			viewHolder.text = (CheckBox) convertView.findViewById(R.id.ingredient_check);
			
			convertView.setTag(viewHolder);
			
		}
		else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		viewHolder.text.setText(list.get(position).substring(1));
		viewHolder.text.setTag(Character.getNumericValue((list.get(position).charAt(0))));
		if(list.get(position).charAt(0)=='1'){
			viewHolder.text.setBackgroundColor(Color.parseColor("#33CC33"));
		}
		else if(list.get(position).charAt(0)=='2'){
			viewHolder.text.setBackgroundColor(Color.parseColor("#FF0066"));
		}
		else if(list.get(position).charAt(0)=='3'){
			viewHolder.text.setBackgroundColor(Color.parseColor("#E89643"));
		}
		else if(list.get(position).charAt(0)=='4'){
			viewHolder.text.setBackgroundColor(Color.parseColor("#FFFF66"));
		}
		else if(list.get(position).charAt(0)=='5'){
			viewHolder.text.setBackgroundColor(Color.parseColor("#FFFFCC"));
		}
		else if(list.get(position).charAt(0)=='6'){
			viewHolder.text.setBackgroundColor(Color.parseColor("#0066FF"));
		}
		else if(list.get(position).charAt(0)=='7'){
			viewHolder.text.setBackgroundColor(Color.parseColor("#FF0000"));
		}
		else if(list.get(position).charAt(0)=='8'){
			viewHolder.text.setBackgroundColor(Color.parseColor("#CC3300"));
		}
		else if(list.get(position).charAt(0)=='0'){
			viewHolder.text.setBackgroundColor(Color.parseColor("#CC00FF"));
		}
		viewHolder.text.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
				if(isChecked){
					selectedNames.add(buttonView.getTag().toString()+buttonView.getText().toString());
					
				}
				else{
					selectedNames.remove(buttonView.getTag().toString()+buttonView.getText().toString());
				}
			}
		});
		//viewHolder.text.setText(list.get(position).substring(1,list.get(position).length()));
		return convertView;
	}
	
	public ArrayList<String> getSelectedNames(){
		return selectedNames;
	}
	

}
