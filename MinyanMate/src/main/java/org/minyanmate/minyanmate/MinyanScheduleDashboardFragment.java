package org.minyanmate.minyanmate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MinyanScheduleDashboardFragment extends Fragment {

	public MinyanScheduleDashboardFragment() {
        super();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_minyan_dashboard, container, false);

		
		
		return rootView;
	}
	
}
