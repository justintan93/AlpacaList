package com.JTA.recipeshoppinglist;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import static java.util.Arrays.asList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;


public class IngredientRetriever{

	//Variables
	String address;
	
	List<String> measureConstants = asList("cup","cups","tablespoon","tablespoons","teaspoon","teaspoons","pound","pounds","ounce","ounces","gram","grams");
	List<String> numbers = asList("1","2","3","4","5","6","7","8","9");
	//Constructors
	public IngredientRetriever(){
		address = "";
	}
	
	public IngredientRetriever(String ad){
		address = ad;
	}
	
	//Public Methods
	public void setAddress(String ad){
		address = ad;
	}
	
	public ArrayList<ArrayList<String>> retrieveIngredients(){
		//declarations
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		Document websiteDoc;
		Elements ingredientNames = new Elements();
		Elements ingredientAmounts = new Elements();
		String servingSize = null;
		String websiteName;
		ArrayList<String> name = new ArrayList<String>();
		ArrayList<String> serving = new ArrayList<String>();
		
		if(address == "") return null;
		
		//retrieval 
		try{
			//create document from html at address
			websiteDoc = Jsoup.connect(address).get();
			//create URI from url to get the domain name and add the name to the list
			websiteName = new URI(address).getHost();
			name.add(websiteName);
			list.add(name);
			//use domain names to see how to parse through the html to get the ingredients
			
			//mobile allrecipes
			if(websiteName.compareTo("m.allrecipes.com")==0){
				servingSize = websiteDoc.select("input[ng-model=servings]").attr("value");
				ingredientNames = websiteDoc.select("span[class=recipe-ingred_txt added]");
				serving.add(servingSize);
				list.add(serving);
				
				ArrayList<ArrayList<String>> fList = foodNetworkParse(ingredientNames);
				ArrayList<String> l = convertWOCom(fList.get(0));
				l = removePar(l);
				list.add(l);
				list.add(fList.get(1));
				
			}
			//allrecipes
			else if(websiteName.compareTo("allrecipes.com")==0){
				servingSize = websiteDoc.select("div[id=zoneIngredients]").attr("data-originalservings");
				ingredientNames = websiteDoc.select("span[class=ingredient-name]");
				ingredientAmounts = websiteDoc.select("span[class=ingredient-amount]");
				
				serving.add(servingSize);
				list.add(serving);
				list.add(convertString(ingredientNames));
				list.add(convertString(ingredientAmounts));
			}
			//foodnetwork
			else if(websiteName.compareTo("www.foodnetwork.com")==0){
				ingredientNames = websiteDoc.select("li[itemprop=ingredients]");
				servingSize = websiteDoc.select("dd[itemprop=recipeYield]").first().text();
				int stopIndex;
				if(Character.isDigit(servingSize.charAt(0))){
					if(servingSize.indexOf(' ') > servingSize.indexOf('-') && servingSize.indexOf('-')!=-1) stopIndex = servingSize.indexOf('-');
					else stopIndex = servingSize.indexOf(' ');
					int serve = Integer.parseInt(servingSize.substring(0,stopIndex));
					if(servingSize.contains("dozen")) serve *= 12;
					serving.add(Integer.toString(serve));
				}
				else{
					serving.add(servingSize);
				}
				list.add(serving);
				
				ArrayList<ArrayList<String>> fList = foodNetworkParse(ingredientNames);
				ArrayList<String> l = convertWOCom(fList.get(0));
				l = removePar(l);
				list.add(l);
				list.add(fList.get(1));
			}
			else{
				return null;
			}
		}
		
		catch(IOException e ){
			return null;
		}
		catch(URISyntaxException e ){
			return null;
		}
		catch(IllegalArgumentException e){
			return null;
		}
		return list;
	}
	
	public ArrayList<ArrayList<String>> foodNetworkParse(Elements e){
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		ArrayList<String> iName = new ArrayList<String>();
		ArrayList<String> iAmount = new ArrayList<String>();
		String ingredient;
		int splitIndex=0;
		
		for(int i = 0; i < e.size(); i++){
			ingredient = e.get(i).text();
			if(numbers.contains(String.valueOf(ingredient.charAt(0)))){//if number is first char
				splitIndex = ingredient.indexOf(' ');
				if(Character.isDigit(ingredient.charAt(ingredient.indexOf(' ')+1))){//if "3 5-pound"
					System.out.println(ingredient);
					int sec_index = ingredient.substring(ingredient.indexOf(' ')+1).indexOf(' ');
					if(measureConstants.contains(ingredient.substring(sec_index+1, ingredient.indexOf(' ', sec_index+1)))){
						iAmount.add(ingredient.substring(0,ingredient.substring(sec_index+1).indexOf(' ')+1));
						iName.add(ingredient.substring(ingredient.substring(sec_index+1).indexOf(' ')+1,ingredient.length()));
					}
					else{	
						splitIndex = ingredient.indexOf(' ',ingredient.indexOf(' ')+1)+1;
						iAmount.add(ingredient.substring(0,ingredient.indexOf(' ',splitIndex)));
						iName.add(ingredient.substring(ingredient.indexOf(' ',splitIndex)+1,ingredient.length()));
					}
				}
				else if(measureConstants.contains(ingredient.substring(splitIndex+1,ingredient.indexOf(' ', splitIndex+1)))){
					iAmount.add(ingredient.substring(0,ingredient.indexOf(' ', splitIndex+1)));
					iName.add(ingredient.substring(ingredient.indexOf(' ', splitIndex+1)+1,ingredient.length()));
				}
				else{
					iAmount.add(ingredient.substring(0,splitIndex));
					iName.add(ingredient.substring(splitIndex+1,ingredient.length()));
				}
			}
			else{
				iAmount.add("0");
				iName.add(ingredient);
			}
		}
		list.add(iName);
		list.add(iAmount);
		return list;
	}
	
	public ArrayList<String> convertWOCom(ArrayList<String> l){
		for(int i = 0; i < l.size(); i++){
			if(l.get(i).lastIndexOf(",")!=-1){
				l.set(i,l.get(i).substring(0,l.get(i).indexOf(",")));
			}
		}

		return l;
	}
	
	public ArrayList<String> removePar(ArrayList<String> l){
		for(int i = 0; i < l.size(); i++){
			if(l.get(i).charAt(l.get(i).length()-1)==')'){
				l.set(i,l.get(i).substring(0,l.get(i).lastIndexOf('(')));
			}
		}
		return l;
	}
	
	public ArrayList<String> convertString(Elements e){
		ArrayList<String> list = new ArrayList<String>();
		for(int i = e.size()-1; i > -1; i--){
			String t = e.get(i).text();
			
			if(t.lastIndexOf(",")!=-1){
				t=t.substring(0,t.indexOf(",")+1);
			}
			if(t.charAt(t.length()-1)==')'){
				t = t.substring(0,t.lastIndexOf('('));
			}
			
			list.add(t);
		}
		return list;
	}
	
	
}
