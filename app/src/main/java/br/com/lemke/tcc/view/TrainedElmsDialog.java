package br.com.lemke.tcc.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.io.File;

import br.com.lemke.tcc.elmsecond.R;
import br.com.lemke.tcc.filemanipulation.FileManipulation;

/**
 * Created by lemke on 09/06/16.
 */
public class TrainedElmsDialog extends DialogFragment
{
    private String[] elmFolderNames;
    private File[] elmFolders;

    public interface TrainedElmsListener
    {
        void onElmChoosed(String elmName);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        FileManipulation fileManipulation = new FileManipulation();
        elmFolders = fileManipulation.getTrainedElms(getContext());
        elmFolderNames = new String[elmFolders.length];
        for (int i = 0; i < elmFolderNames.length; i++)
            elmFolderNames[i] = elmFolders[i].getName();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_elm)
                .setItems(elmFolderNames, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        TrainedElmsListener activity = (TrainedElmsListener) getActivity();
                        activity.onElmChoosed(elmFolderNames[which]);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        dismiss();
    }
}
