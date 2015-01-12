/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.JTA.recipeshoppinglist;

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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.JTA.recipeshoppinglist.IngredientIndivAdapter.ViewHolder;

public class StableArrayAdapter extends ArrayAdapter<String> {

    final int INVALID_ID = -1;
    
    Context c;
    ViewHolder viewHolder;
    ArrayList<String> list = new ArrayList<String>();

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

    public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
        super(context, textViewResourceId, objects);
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
        c = context;
        list = (ArrayList<String>)objects;
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        String item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    
    static class ViewHolder{
		TextView text;
	}
    
public View getView(int position, View convertView, ViewGroup parent){
	TextView hi = (TextView)super.getView(position, convertView, parent);	
	hi.setText(list.get(position).toString().substring(1));
	
	if(list.get(position).charAt(0)=='1'){
		hi.setBackgroundColor(Color.parseColor("#33CC33"));
	}
	else if(list.get(position).charAt(0)=='2'){
		hi.setBackgroundColor(Color.parseColor("#FF0066"));
	}
	else if(list.get(position).charAt(0)=='3'){
		hi.setBackgroundColor(Color.parseColor("#E89643"));
	}
	else if(list.get(position).charAt(0)=='4'){
		hi.setBackgroundColor(Color.parseColor("#FFFF66"));
	}
	else if(list.get(position).charAt(0)=='5'){
		hi.setBackgroundColor(Color.parseColor("#FFFFCC"));
	}
	else if(list.get(position).charAt(0)=='6'){
		hi.setBackgroundColor(Color.parseColor("#0066FF"));
	}
	else if(list.get(position).charAt(0)=='7'){
		hi.setBackgroundColor(Color.parseColor("#FF0000"));
	}
	else if(list.get(position).charAt(0)=='8'){
		hi.setBackgroundColor(Color.parseColor("#CC3300"));
	}
	else if(list.get(position).charAt(0)=='0'){
		hi.setBackgroundColor(Color.parseColor("#CC00FF"));
	}
	
	return hi;
	}
	
}
