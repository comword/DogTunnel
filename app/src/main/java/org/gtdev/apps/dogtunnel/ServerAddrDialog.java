package org.gtdev.apps.dogtunnel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import findgo.Findgo;

/**
 * Created by henorvell on 12/17/17.
 */

public class ServerAddrDialog extends DialogPreference {

    String server_addr = "";

    public ServerAddrDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.findaddr_dialoglayout);
    }

    public String getServerAddr() {
        return server_addr;
    }

    public void setServerAddr(String s) {
        server_addr = s;
        persistString(s);
        callChangeListener(s);
        setSummary(s);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        final AlertDialog alertDialog = (AlertDialog) getDialog();
        Button neutralButton = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Finding...", Toast.LENGTH_SHORT).show();
                EditText edit_range = (EditText) getDialog().findViewById(R.id.addr_range);
                EditText edit_pattern = (EditText) getDialog().findViewById(R.id.pattern);
                new findJob().execute(new FindTaskParams(edit_pattern.getText().toString(), edit_range.getText().toString(), getDialog()));
            }
        });
        EditText edit_seraddr = (EditText) alertDialog.findViewById(R.id.server_addr);
        edit_seraddr.setText(server_addr);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        builder.setNeutralButton(R.string.button_FIND, null);
        builder.setPositiveButton(R.string.button_OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText edit_server = (EditText)getDialog().findViewById(R.id.server_addr);
                server_addr = edit_server.getText().toString();
                setServerAddr(server_addr);
            }
        });
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setServerAddr(restorePersistedValue ? getPersistedString(server_addr) : (String) defaultValue);
    }

    private static class FindTaskParams {
        public String Fig;
        public String Range;
        public Dialog dialog;
        FindTaskParams(String f, String r, Dialog dia) {
            Fig = f;
            Range = r;
            dialog = dia;
        }
    }

    private class findJob extends AsyncTask<FindTaskParams,Integer,String> {
        Dialog dialog = null;
        @Override
        protected String doInBackground(FindTaskParams... Params) {
            dialog = Params[0].dialog;
            return wrap_find(Params[0].Fig,Params[0].Range);
        }

        @Override
        protected void onPostExecute(String result) {
            if(result==null){
                Toast.makeText(getContext(), R.string.find_notfound, Toast.LENGTH_SHORT).show();
                return;
            }
            EditText edit_server = (EditText) dialog.findViewById(R.id.server_addr);
            edit_server.setText(result);
            edit_server.requestFocus();
        }

        public String wrap_find(String Fig, String range) {
            String res = "";
            Findgo.setFigtag(Fig);
            res = Findgo.dofind(range);
            return res;
        }

    }
}
