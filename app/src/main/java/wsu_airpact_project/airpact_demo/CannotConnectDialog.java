package wsu_airpact_project.airpact_demo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Agent1729 on 9/3/2015.
 */
public class CannotConnectDialog extends DialogFragment
{

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Cannot connect to server")
				.setMessage("A connection to the AIRPACT server cannot be established. Please check your internet settings and try again.")
				.setNeutralButton("Ok", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
					}
				})
				;
		return builder.create();
	}
}
