package org.emud.walkthrough.stub;

import org.emud.walkthrough.R;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.resulttype.ResultGUIResolver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class MaxMoveGUI implements ResultGUIResolver {
	

	@Override
	public View getDetailView(LayoutInflater inflater, Result result) {
		View view = null;

		view = inflater.inflate(R.layout.base_result_detail, null);

		((ImageView) view.findViewById(R.id.result_icon)).setImageResource(R.drawable.ic_action_done);
		((TextView) view.findViewById(R.id.result_title)).setText(R.string.rt_max_move_title);
		((TextView) view.findViewById(R.id.result_unit)).setText(R.string.rt_max_move_unit);
		((TextView) view.findViewById(R.id.result_value)).setText("" + ((ResultMaxMove) result).getMaxAcceleration());
		
		return view;
	}

	@Override
	public ListAdapter getListAdapter(Context context) {
		return new MaxMoveListAdapter(context);
	}

	@Override
	public String getTitle() {
		return "Máxima aceleración";
	}
}
