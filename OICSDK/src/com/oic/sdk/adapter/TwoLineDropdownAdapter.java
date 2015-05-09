package com.oic.sdk.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.oic.sdk.R;
import com.oic.sdk.data.Node;
import com.oic.sdk.view.util.OicColor;

public class TwoLineDropdownAdapter extends BaseAdapter implements Filterable {

	private List<Node>originalData = null;
    private List<Node>filteredData = null;
	
	private LayoutInflater mInflater = null;
	private Activity activity;
	
	private Node startNode = null;
	private ItemFilter mFilter = new ItemFilter();
	
	public TwoLineDropdownAdapter(Activity a, ArrayList<Node> items) {
		originalData = items;
		filteredData = items;
		
		activity = a;
		mInflater = LayoutInflater.from(activity);
	}
	
	public void setStartNode(Node start){
		this.startNode = start;
	}
	
	public int getCount() {
		if(filteredData == null){
			return 0;
		}
        return filteredData.size();
    }
	
	public Object getItem(int position) {
		if(filteredData==null){
			return null;
		}
        return filteredData.get(position);
    }

	public static class ViewHolder {

		public TextView title;
		public TextView description;
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {

			holder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.item_search, parent, false);
			holder.title = (TextView) convertView.findViewById(R.id.item);
			holder.description = (TextView) convertView.findViewById(R.id.desc);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Node item = (Node)getItem(position);
		String title = item+"";
		if(title.length() == 0 || "null".equals(title)){
			holder.title.setText(item.getIconShortName());
		}else{
			holder.title.setText(item.toString()+"");
		}
		
		holder.description.setText(item.getLocationType());

		return convertView;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Filter getFilter() {
		return mFilter;
	}
	
	@SuppressLint("DefaultLocale")
	private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Node> list = originalData;

            int count = list.size();
            final ArrayList<Node> nlist = new ArrayList<Node>();
            Node filterableNode ;
            
            ArrayList<FilterNodeObj> listFilter = new ArrayList<TwoLineDropdownAdapter.FilterNodeObj>();
            for(String commonLocType: OicColor.COMMON_LOCATION_TYPE){
            	listFilter.add(new FilterNodeObj(commonLocType));
            }

            for (int i = 0; i < count; i++) {
                filterableNode = list.get(i);
                
                boolean isCommonNode = false;
                for(FilterNodeObj obj: listFilter){
//                	Log.e("filterring", startNode.toString());
                	if(null != startNode && obj.locType.equals(filterableNode.getLocationType())){
                		if(obj.minNode == null){
                			obj.minNode = filterableNode;
                		}
                		float currDistance = filterableNode.dist(startNode);
                		// calculate nearby node
                		if(currDistance < obj.minDis){
                			obj.minNode = filterableNode;
                			obj.minDis = currDistance;
                		}
                		isCommonNode = true;
                		break;
                	}
                }
                if(isCommonNode){
                	continue;
                }
                else if(!filterableNode.isVisibleTag()){
                	continue;
                }
                else if (filterableNode.toString().toLowerCase().contains(filterString)) {
                    nlist.add(filterableNode);
                }
            }
            
            for(FilterNodeObj obj: listFilter){
            	if(obj.minNode!=null){
            		nlist.add(0,obj.minNode);
            	}
            }
            
            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Node>) results.values;
            notifyDataSetChanged();
        }

    }
	
	public static class FilterNodeObj {
		public String locType;
		public Node minNode;
		public float minDis;
		
		public FilterNodeObj(String locType) {
			this.locType = locType;
			minNode = null;
			minDis = Float.MAX_VALUE;
		}
	}
}