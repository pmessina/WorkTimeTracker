package com.worktimetracker.worktimetracker;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.worktimetracker.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class to populate an ExpandableListView with spreadsheet names and the worksheets in them
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter 
{
	private Context context;
	private List<String> listDataHeader;
	private HashMap<String, ArrayList<String>> listDataChild;
	
	public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, ArrayList<String>> listDataChild)
	{
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }

	@Override
	public Object getChild(int groupPosition, int childPosition) 
	{
		String ldh = listDataHeader.get(groupPosition);
        ArrayList<String> ldc = listDataChild.get(ldh);
		
		return ldc.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) 
	{
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) 
	{
		String childText = (String)getChild(groupPosition, childPosition);
		
		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.explist_item, null);
		}
		
		TextView textListChild = (TextView)convertView.findViewById(R.id.tvListItem);
		textListChild.setText(childText);
			
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) 
	{
        String ldh = listDataHeader.get(groupPosition);
        ArrayList<String> ldc = listDataChild.get(ldh);
		
		return ldc.size();
	}

	@Override
	public Object getGroup(int groupPosition) 
	{
		return listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() 
	{
		return listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) 
	{
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) 
    {
		String headerTitle = (String) getGroup(groupPosition);
		
        if (convertView == null) 
        {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.explist_group, null);
        }
 
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.tvListGroup);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
 
        return convertView;

	}

	@Override
	public boolean hasStableIds() 
	{
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

}
