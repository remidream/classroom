package edu.ccucsie.classroom.search;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

@SuppressLint("NewApi")
public class SubmitDialogFragment extends DialogFragment {

	static SubmitDialogFragment newInstance(String title) {
		SubmitDialogFragment fragment = new SubmitDialogFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		fragment.setArguments(args);
		return fragment;
	}

	@SuppressLint("NewApi")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = getArguments().getString("title");

		return new AlertDialog.Builder(getActivity()).setTitle(title)
				.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						((SubmitFormActivity) getActivity()).doPositiveClick();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@SuppressLint("NewApi")
					public void onClick(DialogInterface dialog, int whichButton) {
						((SubmitFormActivity) getActivity()).doNegativeClick();
					}
				}).create();
	}
}
