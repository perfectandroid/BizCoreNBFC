package com.perfect.nbfc.Helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.perfect.nbfc.R;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.provider.Settings.Secure;


public class BizcoreApplication extends Application {


    public static final String SHARED_PREF = "loginsession";
    public static final String SHARED_PREF1 = "Agent_ID";
    public static final String SHARED_PREF2 = "Agent_Name";
    public static final String SHARED_PREF3 = "CusMobile";
    public static final String SHARED_PREF4 = "token";
    public static final String SHARED_PREF5 = "username";
    public static final String SHARED_PREF6= "Transaction_ID";
    public static final String SHARED_PREF7= "Archive_ID";
    public static final String SHARED_PREF8= "logintime";
    public static final String SHARED_PREF9= "mpin";

    private static final String PACKAGE = "package";
    public static final boolean DEBUG = true;
    public static final String EXCEPTION_NO_IMEI                        = "No imei";
   // public static final String TEST_IMEI                                = "IMEI999";/*IMEI999 "IMEI002" "IMIE001""CRDI007" "IMEI009" "IMEI004"*/
    public static final int TRANSACTION_TIME_OUT                        = /*60000*/600000;
    public static final int AGENT_TIME_OUT                              = 300000/* 10000*/;
    private static final String ASCII                                   = "ASCII";
    public static final String SERVICE_NOT_AVAILABLE                    = "Service is not available";
    public static final int READ_PHONE_STATE_REQUEST                    = 1001;
    public static final int REQUEST_AUTHENTICATION_WITHDRAWAL           = 100;
    public static final int REQUEST_AUTHENTICATION_OTHER_FUNDTRANSFER   = 200;
    public static final int REQUEST_AUTHENTICATION_BALANCE_ENQUIRY      = 300;
    public static final int REQUEST_AUTHENTICATION_DEPOSIT              = 400;
    public static final int REQUEST_AUTHENTICATION_MINISTATEMENT        = 500;
    public static final int REQUEST_AUTHENTICATION_OWN_FUNDTRANSFER     = 600;
    private static final int FLAG_NO_PHONEPERMISSION                    = 655;

    public static final int REQUEST_ACCOUNT_FETCHING        = 21;
    public static final int REQUEST_MINISTATEMENT           = 38;
    public static final int REQUEST_BALANCE_ENQUIRY         = 31;
    public static final int REQUEST_CARDED_ACC_FETCHING     = 32;
    public static final String REQUEST_VERIFICATION_CALL    = "09";
    public static final String REQUEST_WITHDRAWAL           = "01";

    public static final String PROCESSING_CODE              = "Processing_Code";
    public static final String EXTENDED_PRIMARY_ACC_NO      = "Extended_Primary_AccountNumber";
    public static final String CUSTOMER_NUMBER              = "Customer_Number";
    public static final String ACCOUNT_IDENTIFICATION_1     = "AccountIdentification1";
    public static final String FROM_MODULE                  = "From_Module";
    public static final String CARD_ACCEPTOR_TERMINAL_CODE  = "Card_Acceptor_Terminal_IDCode";
    public static final String REQUEST_MESSAGE              = "RequestMessage";
    public static final String SYSTEM_TRACE_AUDIT_NO        = "SystemTrace_AuditNumber";
    public static final String NARRATION                    = "Narration";
    public static final String AGENT_ID                     = "Agent_ID";
    public static final String TOKEN                        = "Token";
    public static final String CARD_LESS                    = "CardLess";
    public static final String VERIFY_OTP                   = "VerifyOTP";
    public static final String AUTH_ID                      = "Auth_ID";
    public static final String TEMP_CARD_NO                 = "0000000000000000";
    public static final String TEMP_CUST_NO                 = "000000000000";
    public static final String TEMP_ACC_NO                  = "000000000000";
    public static final String CURRENT_DATE                 = "CurrentDate";
    public static final String TRANS_DATE                   = "TransDate";
    public static final String AMOUNT                       = "Amount";
    public static final String DATA                         = "data";
    public static final String LINE_NO                      = "line_no";
    public static final String CARD_SWIPE_SUCCESS           = "SUCCESS";
    public static final String OF_LNE_CUSTOMER              = "offlinecustomer";
    public static final String LAST_UPDATE_LIST_DATE        = "last_sync_date";
    public static final String LAST_SYNC_LIST_DATE          = "last_list_sync_date";


    private static BizcoreApplication mInstance;
    private TelephonyManager telephonyManager;
    private static BizcoreApplication mBizcoreApplication;



    public static synchronized  BizcoreApplication getInstance(){
        if ( mBizcoreApplication == null ){
            mBizcoreApplication = new BizcoreApplication();
        }
        return mBizcoreApplication;
    }
    public static Application getAppContext() {
        return mInstance;
    }

    public void deviceId(Activity activity) {
        telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
            return;
        }
    }
    public static String encryptMessage(final String message )
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {


        int ivSize = 16;
        byte[] iv = new byte[ivSize];
        byte[] clean = message.getBytes();
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        String key = /*getEncryptString()*/"fenerbachemonaco";

        byte[] keyBytes = key.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(clean);

        byte[] encryptedIVAndText = new byte[ivSize + encrypted.length];
        System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize);
        System.arraycopy(encrypted, 0, encryptedIVAndText, ivSize, encrypted.length);
        return new String( Base64.encode( encryptedIVAndText, Base64.DEFAULT), ASCII );

    }

   /* public DeviceAppDetails getDeviceAppDetails( final Context context ) throws PackageManager.NameNotFoundException, SecurityException {
        final DeviceAppDetails deviceAppDetails = new DeviceAppDetails();
        final PackageInfo packageInfo =context.getPackageManager().getPackageInfo( context.getPackageName(), 0);
        deviceAppDetails.setAppVersion( packageInfo.versionCode );
        deviceAppDetails.setVersionName( packageInfo.versionName );
        return deviceAppDetails;

    }
*/
    public DeviceAppDetails getDeviceAppDetails( final Context context ) throws PackageManager.NameNotFoundException, SecurityException {
        final DeviceAppDetails deviceAppDetails = new DeviceAppDetails();
        final PackageInfo packageInfo =context.getPackageManager().getPackageInfo( context.getPackageName(), 0);
        deviceAppDetails.setAppVersion( packageInfo.versionCode );
        deviceAppDetails.setVersionName( packageInfo.versionName );


        return deviceAppDetails;

    }


  //LIVE
    @SuppressLint("ObsoleteSdkInt")
    public DeviceAppDetails1 getDeviceAppDetails1(final Context context ) throws PackageManager.NameNotFoundException, SecurityException {
        final DeviceAppDetails1 deviceAppDetails1 = new DeviceAppDetails1();
        String deviceId = "";
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceAppDetails1.setImei(deviceId);
        return deviceAppDetails1;

    }



  /*  @SuppressLint("ObsoleteSdkInt")
    public DeviceAppDetails1 getDeviceAppDetails1(final Context context ) throws PackageManager.NameNotFoundException, SecurityException {
        final DeviceAppDetails1 deviceAppDetails1 = new DeviceAppDetails1();
        final PackageInfo packageInfo =context.getPackageManager().getPackageInfo( context.getPackageName(), 0);
        deviceAppDetails1.setAppVersion( packageInfo.versionCode );
        deviceAppDetails1.setVersionName( packageInfo.versionName );
        String deviceId = "";
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceAppDetails1.setImei(deviceId);
        return deviceAppDetails1;

    }*/



    //PERFECT DEMO
   /* public DeviceAppDetails getDeviceAppDetails(final Context context) throws PackageManager.NameNotFoundException, SecurityException {
        final boolean proceedFlag = askPermission((Activity) context, BizcoreApplication.READ_PHONE_STATE_REQUEST);
        if (!proceedFlag)
            return new DeviceAppDetails();
        final DeviceAppDetails deviceAppDetails = new DeviceAppDetails();
        final PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        deviceAppDetails.setAppVersion(packageInfo.versionCode);
        deviceAppDetails.setVersionName(packageInfo.versionName);

        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(android.content.Context.TELEPHONY_SERVICE);

        deviceAppDetails.setImei("IMEI100");

        return deviceAppDetails;

    }

   */


    /*Ask permission to access phone data, like IMEI no and version code of app*/
    public static boolean askPermission(final Activity activity, final int code) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_PHONE_STATE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("To use Bizcore on your device, please enable the 'phone' permission. Do you want to go to settings?")

                        .setIcon(ContextCompat.getDrawable(activity, R.drawable.bank_icon))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                try {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts(PACKAGE, activity.getPackageName(), null);
                                    intent.setData(uri);
                                    activity.startActivity(intent);

                                } catch (Exception e) {
                                    if (BizcoreApplication.DEBUG)
                                        Log.e("exc", e.toString());
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Do nothing
                            }
                        })
                        .setCancelable(false);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, code);
            }
            return false;
        }
        return true;
    }


}
