package org.emud.walkthrough.cadence;

import org.emud.walkthrough.R;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.resulttype.ResultGUIResolver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class CadenceGUI implements ResultGUIResolver {

	@Override
	public View getDetailView(LayoutInflater inflater, Result result) {
		View view = null;

		view = inflater.inflate(R.layout.base_result_detail, null);

		((ImageView) view.findViewById(R.id.result_colorbrand)).setBackgroundResource(getColorBrandResource());
		((TextView) view.findViewById(R.id.result_title)).setText(R.string.rt_cadence_title);
		((TextView) view.findViewById(R.id.result_unit)).setText(R.string.rt_cadence_unit);
		((TextView) view.findViewById(R.id.result_value)).setText("" + String.format("%.2f", ((Cadence) result).getCadence()));
		
		return view;
	}

	@Override
	public ListAdapter getListAdapter(Context context) {
		return new CadenceListAdapter(context);
	}

	@Override
	public String getTitle() {
		return "Ritmo";
	}

	@Override
	public int getColorBrandResource() {
		return R.color.result_cadence;
	}
}
