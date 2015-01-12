package com.JTA.recipeshoppinglist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ListScreen extends FragmentActivity {
	//0 = etc, 1 = produce, 2 = meats, 3 = pastries, 4 = cheeses, 5 = dairy, 6 = beverages, 7 = snacks, 8 = spices
	ArrayList<String> list = new ArrayList<String>();
	IngredientIndivAdapter adapter;
	ArrayList<ArrayList<Ingredient>> inList;
	View popupView;
	ClipboardManager clipBoard;
	ClipData myClip;
	PopupWindow popupWindow;
	Dialog diag;
	IngredientFragment ifrag;
	ArrayList<String> sortList = new ArrayList<String>();
	ArrayList<String> ingreList = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_screen);
	
		Intent intent = getIntent();
		String prev_but = intent.getStringExtra("hi");
		
		sortList = read_ingredients("SortOrder.txt");
		inList = readIngredientList();
		if(prev_but.compareTo("new")!=0){
		list = read_ingredients("SavedIngredients.txt");
		}
		
		if(sortList.isEmpty()){
			sortList.add("1Produce");
			sortList.add("2Meats");
			sortList.add("3Pastries");
			sortList.add("4Cheeses");
			sortList.add("5Dairy");
			sortList.add("6Beverages");
			sortList.add("7Snacks");
			sortList.add("8Spices");
			sortList.add("0Etc");
		}
		
		adapter = new IngredientIndivAdapter(this,list);
		final ListView lv = (ListView)findViewById(R.id.listview1);
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		lv.setAdapter(adapter);
		
		Button remvButn = (Button)findViewById(R.id.remove_item);
		remvButn.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				remove_ingredient_button(adapter,lv);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds item	s to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_screen, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onStop(){
		super.onStop();
		write_ingredients(list,"SavedIngredients.txt");
		write_ingredients(sortList,"SortOrder.txt");
	}
	
	public void onPause(){
		super.onPause();
		write_ingredients(list,"SavedIngredients.txt");
		write_ingredients(sortList,"SortOrder.txt");
	}
	
	public void onDestroy(){
		super.onDestroy();
		write_ingredients(list,"SavedIngredients.txt");
		write_ingredients(sortList,"SortOrder.txt");
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_list_screen,
					container, false);
			return rootView;
		}
	}
	
	public void add_button(View v){
		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		View popupView = layoutInflater.inflate(R.layout.add_button,null);
		final PopupWindow popupWindow = new PopupWindow(popupView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		Button btnAddI = (Button)popupView.findViewById(R.id.add_ingredient);
		Button btnAddR = (Button)popupView.findViewById(R.id.add_recipe);
		Button btnAddC = (Button)popupView.findViewById(R.id.add_cancel);
		
		btnAddI.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				popupWindow.dismiss();
				add_ingredient_button(v);
			}
		});
		
		btnAddR.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				popupWindow.dismiss();
				add_recipe_button(v);
			}
		});
		
		btnAddC.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				popupWindow.dismiss();
			}
		});
		popupWindow.showAtLocation(v,Gravity.CENTER,0,0);
	}
	
	public void add_recipe_button(View v){
		android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
		ifrag = new IngredientFragment();
		ifrag.show(fm, "hi");
		fm.executePendingTransactions();
		diag = ifrag.getDialog();
		Button btnAddC= (Button)diag.findViewById(R.id.addr_cancel);	
		Button btnSub = (Button)diag.findViewById(R.id.rsubmit);
		
		btnAddC.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				ifrag.dismiss();
			}
		});
		
		btnSub.setOnClickListener(new Button.OnClickListener(){
			EditText recipe_url = (EditText)ifrag.getDialog().findViewById(R.id.recipe_url_text);
			public void onClick(View v){  
				if(recipe_url.getText().toString().matches("")) checkList(null);
				else{
				ArrayList<ArrayList<String>> iList = new ArrayList<ArrayList<String>>();
				final InRet ire = new InRet(v);
					ire.execute(recipe_url.getText().toString());
					Handler handler = new Handler();
					handler.postDelayed(new Runnable(){
						public void run(){
							if(ire.getStatus()==AsyncTask.Status.RUNNING){
								ire.dialog.dismiss();
								AlertDialog.Builder aboutDialog = new AlertDialog.Builder(ListScreen.this);
							    aboutDialog.setMessage(R.string.time_out);
							    aboutDialog.setNegativeButton(R.string.close_button, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {	
									dialog.cancel();
									ifrag.dismiss();
								}
								});
							    AlertDialog dialog = aboutDialog.create();
							    dialog.show();
							}
						}
					}, 10000);
			}
	}
});
	
	}
	
	public void sort_items(View v){
		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		View popupView = layoutInflater.inflate(R.layout.sort_list,null);
		final PopupWindow popupWindow = new PopupWindow(popupView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		Button btnSub = (Button)popupView.findViewById(R.id.sort_button2);
		
		final StableArrayAdapter sAdapter = new StableArrayAdapter(this,R.layout.sort_text,sortList);
		final DynamicListView sortListView = (DynamicListView)popupView.findViewById(R.id.listview);
		
		sortListView.setCheeseList(sortList);
		sortListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		sortListView.setAdapter(sAdapter);
		
		btnSub.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				sortList = sortListView.mCheeseList;
				sortIngredients(adapter,list,sortList);
				adapter.notifyDataSetChanged();
				popupWindow.dismiss();
			}
		});
		
		popupWindow.setFocusable(true);
		popupWindow.showAtLocation(v,Gravity.CENTER, 0, 0);
	}
	
	public void add_recipe_ingredients(View v,ArrayList<ArrayList<String>> rList){
		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		View popupView = layoutInflater.inflate(R.layout.recipe_ingredients,null);
		final PopupWindow popupWindow = new PopupWindow(popupView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		Button btnSub = (Button)popupView.findViewById(R.id.rrsubmit);
		Button btnRmv = (Button)popupView.findViewById(R.id.remove_itemR);
		TextView eText = (TextView)popupView.findViewById(R.id.recipe_serving);
		
		ArrayList<String> rawIngredientList = new ArrayList<String>();
		for(int i = 0; i < rList.get(2).size(); i++){
			if(rList.get(3).get(i)!="0") rawIngredientList.add("0"+rList.get(3).get(i)+" "+rList.get(2).get(i));
			else rawIngredientList.add("0"+rList.get(2).get(i));
		}
		
		final IngredientIndivAdapter rAdapter = new IngredientIndivAdapter(this,rawIngredientList);
		final ListView liv = (ListView)popupView.findViewById(R.id.recipe_list);
		liv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		liv.setAdapter(rAdapter);
		
		eText.setText(rList.get(1).get(0));
		
		btnRmv.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				remove_ingredient_button(rAdapter,liv);
			}
		});
		
		btnSub.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				for(int i = 0; i < rAdapter.getCount(); i++){
					String iText = rAdapter.getItem(i).substring(1);
					String iName;
					if(iText.lastIndexOf(" ")!= -1){
						iName = iText.substring(iText.lastIndexOf(" ")+1,iText.length());
					}
					else iName = iText;
					iName.trim();
					if(iName.length()!=0 && Character.isLetter(iName.charAt(0))){
						adapter.add(getIngId(iName)+iText);
						sortIngredients(adapter,list,sortList);
					}
					else{
						adapter.add(0+iText);
						sortIngredients(adapter,list,sortList);
					}
					adapter.notifyDataSetChanged();
				}
				popupWindow.dismiss();
				rAdapter.clear();
			}
		});
		
		popupWindow.setFocusable(true);
		popupWindow.showAtLocation(v,Gravity.CENTER, 0, 0);
	}
	
		
	public void checkList(ArrayList<ArrayList<String>> li){
		if(li != null){
			ifrag.dismiss();
			Log.d("hi",ListScreen.this.toString());
			add_recipe_ingredients(new View(ListScreen.this),li);
		}
		else{
			AlertDialog.Builder aboutDialog = new AlertDialog.Builder(diag.getContext());
		    aboutDialog.setMessage(R.string.invalid_url);
		    aboutDialog.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {	
				dialog.cancel();
				ifrag.dismiss();
			}
			});
		    aboutDialog.setPositiveButton(R.string.retry_button, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {	
					dialog.cancel();
					
				}
				});
		    AlertDialog dialog = aboutDialog.create();
		    dialog.show();
		}
	}
	
	
	public void add_ingredient_button(View v){
		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		popupView = layoutInflater.inflate(R.layout.add_ingredient_pop,null);
		final PopupWindow popupWindow = new PopupWindow(popupView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		Button btnAddC = (Button)popupView.findViewById(R.id.addi_cancel);
		Button btnSub = (Button)popupView.findViewById(R.id.isubmit);
		final AutoCompleteTextView ingredient_name = (AutoCompleteTextView)popupView.findViewById(R.id.ingredient_name_text);
		ArrayAdapter<String> iAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ingreList);
		//ingredient_name.setAdapter(iAdapter);
		popupWindow.setFocusable(true);
		popupWindow.update();
		
		btnAddC.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				popupWindow.dismiss();
			}
		});
		
		btnSub.setOnClickListener(new Button.OnClickListener(){
			
			EditText ingredient_amt = (EditText)popupView.findViewById(R.id.ingredient_amount_text);
			public void onClick(View v){
				
				String iText = ingredient_name.getText().toString().trim();
				String iAmt = ingredient_amt.getText().toString();
				String iName;
				
				if(iText.lastIndexOf(" ")!= -1){
					iName = iText.substring(iText.lastIndexOf(" ")+1,iText.length());
				}
				else iName = iText;
				
				if(Character.isLetter(iName.charAt(0))){
					adapter.add(getIngId(iName)+iAmt+" "+iText);
					sortIngredients(adapter,list,sortList);
				}
				else{
					adapter.add(0+iAmt+" "+iText);
					sortIngredients(adapter,list,sortList);
				}
				adapter.notifyDataSetChanged();
				popupWindow.dismiss();
			}
		});
		
		popupWindow.showAtLocation(v,Gravity.CENTER,0,0);
	}
	
	public void remove_ingredient_button(IngredientIndivAdapter a,ListView listV){
		
		for(int i = a.getSelectedNames().size()-1; i > -1; i--){
			a.remove(a.getSelectedNames().get(i));
			a.getSelectedNames().remove(i);
		}
		
		
		for(int k = 0; k < a.getCount(); k++){
			View vi = listV.getChildAt(k);
			CheckBox c = (CheckBox)vi.findViewById(R.id.ingredient_check);
			c.setChecked(false);
		}
		
		a.notifyDataSetChanged();
	}
	
	public int getIngId(String s){
		String t = s.toLowerCase(Locale.ENGLISH);
		Log.d("steak1",""+inList.get(1).toString());
		for(Ingredient ing: inList.get((t.charAt(0)-97))){
			if(ing.getName().equals(t)) return ing.getPos();
		}
		return 0;
	}
	
	public ArrayList<ArrayList<Ingredient>> readIngredientList(){
		ArrayList<ArrayList<Ingredient>> ingredientList = new ArrayList<ArrayList<Ingredient>>();
		for(int isize = 0; isize < 26; isize++){
			ingredientList.add(new ArrayList<Ingredient>());
		}
		AssetManager assetmanager = getAssets();
		InputStream is = null;
		
		try{
			is = assetmanager.open("ingredient_names");
		}
		catch (IOException e){
			e.printStackTrace();
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		String line = "";
		
		try{
			while((line = reader.readLine()) != null){
				ingreList.add(line.substring(1));
				ingredientList.get(line.charAt(1)-97).add(new Ingredient(line.charAt(0)-48,line.substring(1,line.length())));
			}
			reader.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		return ingredientList;
	}
	
	public void write_ingredients(ArrayList<String> iList, String fileLoc){
		try{
			String path = ListScreen.this.getFilesDir().getAbsolutePath();
			File saved_ing = new File(path, fileLoc);
			if(!saved_ing.exists()){
				saved_ing.createNewFile();
			}
			FileWriter fw = new FileWriter(saved_ing);
			PrintWriter writer = new PrintWriter(fw);
			for(int i = 0; i < iList.size(); i++){
				writer.println(iList.get(i));
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public ArrayList<String> read_ingredients(String fileLoc){
		ArrayList<String> iList = new ArrayList<String>();
		String path = ListScreen.this.getFilesDir().getAbsolutePath();
		File saved_ing = new File(path, fileLoc);
		try{
			if(!saved_ing.exists()){
				saved_ing.createNewFile();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(saved_ing), Charset.forName("UTF-8")));
			String line = "";
			while((line = reader.readLine()) != null){
				iList.add(line);
			}
			reader.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return iList;
	}
	
	public void sortIngredients(ArrayAdapter<String> a, ArrayList<String> ingredientList, ArrayList<String> sortOrder){
		ArrayList<String> newList = new ArrayList<String>();
		for(String x: sortOrder){
			char firstX = x.charAt(0);
			for(String y: ingredientList){
				if(firstX == y.charAt(0)){
					newList.add(y);
				}
			}
		}
		a.clear();
		a.addAll(newList);
	}
	
	
	static class Ingredient{
		
		String n;
		int p;
		public Ingredient(int pos, String name){
			n = name;
			p = pos;
		}
		public String getName(){
			return n;
		}
		public int getPos(){
			return p;
		}
		public String toString(){
			return n + " " + p;
		}
		
	}
	private class InRet extends AsyncTask<String,Void,ArrayList<ArrayList<String>>>{
		public ProgressDialog dialog;
		
		ArrayList li;
		
		
		public InRet(View v){
			this.dialog= new ProgressDialog(ListScreen.this);
			
		}
		
		protected void onPreExecute(){
			this.dialog.setMessage("Retrieving Ingredients!");
			this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			this.dialog.setCanceledOnTouchOutside(false);
			this.dialog.show();
		}
		
		protected ArrayList<ArrayList<String>> doInBackground(String... params){
			IngredientRetriever iret = new IngredientRetriever(params[0]);
			return iret.retrieveIngredients();
		}
		
		protected void onPostExecute(ArrayList<ArrayList<String>> l){
			if(dialog.isShowing()){
				dialog.dismiss();
			}
			checkList(l);
		}
	}
}
