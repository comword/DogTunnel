package org.gtdev.apps.dogtunnel;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by henorvell on 12/12/17.
 */

public class VpnWrapper {

    static String packagename = "";

    public static void stop(){
        Process pro = getSUProcess();
        DataOutputStream os = new DataOutputStream(pro.getOutputStream());
        try {
            os.writeBytes(BuildCmd("--stop")+"exit\n");
            os.flush();
            pro.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try{
                if (os != null)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setup(String s, String u, String p, String dns, String Route){
        Process pro = getSUProcess();
        DataOutputStream os = new DataOutputStream(pro.getOutputStream());
        try {
            os.writeBytes(BuildCmd("--setup "+s+" "+u+" "+p+" "+dns+" "+Route)+"exit\n");
            os.flush();
            pro.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try{
                if (os != null)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String test(){
        Process pro = getSUProcess();
        String result = "";
        if(pro == null)
            return result;
        DataOutputStream os = new DataOutputStream(pro.getOutputStream());
        InputStream iS = null;
        try {
            os.writeBytes(BuildCmd("--test")+"exit\n");
            os.flush();
            pro.waitFor();
            iS = pro.getInputStream();
            if (iS.available() > 0)
                 result = getStringFromIO(iS);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try{
                if (os != null)
                    os.close();
                if (iS != null)
                    iS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static boolean isVpnRunning() {
        ArrayList<String> networkList = new ArrayList<>();
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    networkList.add(networkInterface.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(String s:networkList)
            if(s.contains("ppp"))
                return true;
        return false;
    }

    private static Process getSUProcess() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su 1000");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return process;
        //DataOutputStream os = new DataOutputStream(process.getOutputStream());
    }

    private static String BuildCmd(String para){
        return "CLASSPATH=`find /data/app -name base.apk | grep "+packagename+"` /system/bin/app_process64 /system/bin org.gtdev.apps.dogtunnel.mVpnService "+para+"\n";
    }

    private static String getStringFromIO(InputStream inputStream) {
        BufferedReader br = null;
        String result = null;
        try {
            String temp;
            StringBuilder sb = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return result;
    }
}
