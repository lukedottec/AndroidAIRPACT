package wsu_airpact_project.airpact_demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by Agent1729 on 4/22/2015
 */
public class TooFarDialog extends DialogFragment implements AdapterView.OnItemSelectedListener
{
	private String name="";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.too_far_dialog, null);
		builder.setTitle("Select a Site")
				.setView(view)
				.setPositiveButton("Use Site", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						//Yes
						mListener.onDialogUseSite(TooFarDialog.this, name);
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						//Cancel
						//Do nothing
					}
				});

		//String[] items = new String[Globals.siteList.sites.size()];
		String[] items = new String[5];
		name=Globals.siteList.sites.get(0).Name;
		for (int i = 0; i < 5; i++)
			items[i] = Globals.siteList.sites.get(i).Name;
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
		Spinner spinner = (Spinner)view.findViewById(R.id.spinnerintoofar);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		return builder.create();
	}

	public interface TooFarDialogListener
	{
		public void onDialogUseSite(TooFarDialog dialog, String name);
	}
	TooFarDialogListener mListener;
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try
		{
			mListener = (TooFarDialogListener)activity;
		}
		catch (ClassCastException e)
		{
			Log.d("TooFarDialog", "ClassCastException...");
			throw new ClassCastException(activity.toString()+" must implement TooFarDialogListener");
		}
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
	{
		name=Globals.siteList.sites.get(pos).Name;
	}
	public void onNothingSelected(AdapterView<?> parent) {}
}
