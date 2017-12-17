package org.gtdev.apps.dogtunnel;
/**
 * Created by henorvell on 10/17/17.
 */
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.os.Process;
import android.app.ActivityThread;
import android.util.Log;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.net.IConnectivityManager;

import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnProfile;

public class mVpnService {

    private static final IConnectivityManager mService = IConnectivityManager.Stub.asInterface(
            ServiceManager.getService(Context.CONNECTIVITY_SERVICE));
    private static ActivityThread at;
    private static Context mContext;

    public static final int PER_USER_RANGE = 100000;

    public static void main(String[] args) {
        Looper.prepare();
        at = ActivityThread.systemMain();
        mContext = at.getSystemContext();
        if (args.length > 0) {
            switch (args[0]) {
                case "--test":
                    System.out.print("Pong!");
                    break;
                case "--setup"://--setup <Server> <Username> <Password> <DNS> <Route>
                    if(args.length == 6)
                        setupVPN(args[1],args[2],args[3],args[4],args[5]);
                    break;
                case "--stop":
                    disconnect();
                    break;
                case "--status":
                    System.out.print(getStatus());
                    break;
            }
        }
        System.exit(0);
    }

    public static void setupVPN(String s, String u, String p, String dns, String Route){
        VpnProfile profile = new VpnProfile("DogTunnel");
        profile.name = "DogTunnel";
        profile.type = VpnProfile.TYPE_PPTP;
        profile.server = s.trim();
        profile.username = u;
        profile.password = p;
        profile.searchDomains = "";
        profile.dnsServers = dns.trim();
        profile.routes = Route.trim();
        profile.mppe = true;
        profile.saveLogin = false;
        try {
            connect(profile);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void connect(VpnProfile profile) throws RemoteException {
            mService.startLegacyVpn(profile);
    }

    public static void disconnect() {
        try {
            LegacyVpnInfo connected = mService.getLegacyVpnInfo(myUserId());
            if (connected != null && (connected.key.equals("DogTunnel"))) {
                mService.prepareVpn(null, VpnConfig.LEGACY_VPN, myUserId());
            }
        } catch (RemoteException e) {
            Log.e("DogTunnel", "Failed to disconnect", e);
        }
    }

    public static String getStatus() {
        try {
            LegacyVpnInfo connected = mService.getLegacyVpnInfo(myUserId());
            if(connected != null && connected.state == LegacyVpnInfo.STATE_CONNECTED)
                return connected.key;
        } catch (RemoteException e) {
            Log.e("DogTunnel", "Failed to get status", e);
        }
        return "";
    }

    /*public static List<VpnProfile> loadVpnProfiles(KeyStore keyStore) {
        final ArrayList<VpnProfile> result = new ArrayList<VpnProfile>();

        for (String key : keyStore.list(Credentials.VPN)) {
            final VpnProfile profile = VpnProfile.decode(key, keyStore.get(Credentials.VPN + key));
            if (profile != null) {
                result.add(profile);
                Log.i("DogTunnel",key);
            }
        }
        return result;
    }*/

    /*public static void clearLockdownVpn(Context context) {
        KeyStore.getInstance().delete(Credentials.LOCKDOWN_VPN);
        // Always notify ConnectivityManager after keystore update
        try {
            context.getSystemService(IConnectivityManager.class).updateLockdownVpn();
            //mService.updateLockdownVpn();
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }*/

    public static int myUserId() {
        return getUserId(Process.myUid());
    }

    public static int getUserId(int uid) {
            return uid / PER_USER_RANGE;
    }
}
