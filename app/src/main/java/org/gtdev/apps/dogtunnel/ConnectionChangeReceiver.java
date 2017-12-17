package org.gtdev.apps.dogtunnel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by henorvell on 10/15/17.
 */

public class ConnectionChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String log = intent.toUri(Intent.URI_INTENT_SCHEME).toString();
        Log.d("DogTunnel", log);
        //Toast.makeText(context, log, Toast.LENGTH_LONG).show();
        if(context == null || !(context instanceof SettingsActivity))
            return;
        //ConnectivityManager conn =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        SettingsActivity mActivity = (SettingsActivity) context;

        mActivity.isVPNRunning = VpnWrapper.isVpnRunning();

        FloatingActionButton fab = (FloatingActionButton) mActivity.findViewById(R.id.MainEnable);
        if(fab!=null){
            fab.setBackgroundTintList(ColorStateList.valueOf(mActivity.getResources().getColor(mActivity.isVPNRunning?R.color.holo_green_light:R.color.holo_red_dark)));
            fab.setImageResource(mActivity.isVPNRunning?R.drawable.ic_conok_24dp:R.drawable.ic_discon_24dp);
        }
    }

}
