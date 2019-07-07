package algorithm;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;


public class GeoRecommendation {
	public List<Item> recommemdationItems(String userId, double lat, double lon){
		List<Item> recommendedItems = new ArrayList<>();
		DBConnection connection = DBConnectionFactory.getConnection();
		
		Set<String> favoriteItemIds=connection.getFavoriteItemIds(userId);
		Map<String, Integer> allCategories = new HashMap<>();
		
		for(String itemId:favoriteItemIds) {
			Set<String> categoriteSet = connection.getCategories(itemId);
			for(String cate:categoriteSet) {
				if(allCategories.containsKey(cate)) {
					allCategories.put(cate, allCategories.get(cate)+1);
				}else {
					allCategories.put(cate, 1);
				}
			}
		}
		
		List<Entry<String,Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
		Collections.sort(categoryList,(a,b) ->(Integer.compare(b.getValue(), a.getValue())));
		
		Set<Item> visitedItems = new HashSet<Item>();
		
		for(Entry<String,Integer> cate:categoryList) {
			List<Item> items = connection.searchItems(lat, lon, cate.getKey());
			List<Item> filteredItems = new ArrayList<Item>();
			for(Item item:items) {
				if(!favoriteItemIds.contains(item.getItemId())&&!visitedItems.contains(item)) {
					filteredItems.add(item);
				}
			}
			
			Collections.sort(filteredItems,(a,b)->(Double.compare(a.getDistance(),b.getDistance())));
			visitedItems.addAll(items);
			recommendedItems.addAll(filteredItems);
		}
		
		return recommendedItems;
		
		
		
	}
}
