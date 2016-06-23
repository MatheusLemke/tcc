package br.com.lemke.tcc.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.com.lemke.tcc.elmsecond.R;

/**
 * Created by lemke on 13/06/16.
 */
public class InputAttributeValueDialog extends DialogFragment
{
    private String attributeName;
    private int position;

    private EditText editText;

    private AlertDialog alertDialog;

    public interface InputAttributeValuePositiveListener
    {
        void onPositiveButtonClick(String inputText, int position);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {
        editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Set " + attributeName)
                .setView(editText)
                .setPositiveButton(R.string.confirm, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                    }
                });

        alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialog)
            {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (editText.getText().toString().matches(""))
                            editText.requestFocus();
                        else
                        {
                            InputAttributeValuePositiveListener activity = (InputAttributeValuePositiveListener) getActivity();
                            activity.onPositiveButtonClick(editText.getText().toString(), position);
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });
        return alertDialog;

    }

    public void setAttributeName(String attributeName)
    {
        this.attributeName = attributeName;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }
}
