package com.example.garry.myinfo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String[] SELF_PROJECTION = new String[] {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_ALTERNATIVE,

        };

        Cursor cursor = getContentResolver().query( ContactsContract.Profile.CONTENT_URI, SELF_PROJECTION, null, null, null);
        cursor.moveToFirst();
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        if (cursor.getCount() > 0) {
           sb.append("Phone ID :");
           sb.append(cursor.getString(0));
           sb.append("\n");
           sb.append("Owner Name : ");
           sb.append(cursor.getString(1));
           sb.append("\n");
            sb.append("HAS_PHONE_NUMBER : ");
            sb.append(cursor.getString(2));
            sb.append("\n");
            sb.append("DISPLAY_NAME_PRIMARY : ");
            sb.append(cursor.getString(3));
            sb.append("\n");
            sb.append("DISPLAY_NAME_ALTERNATIVE : ");
            sb.append(cursor.getString(4));
            sb.append("\n");
        } else {
            sb.append("No Phone Info");
            sb.append("\n");
        }



        String userName = getUsername();
        if (null == userName) {
            sb.append("Google username not found");
            sb.append("\n");
        } else {
            sb.append("Google username :");
            sb.append(getUsername());
            sb.append("\n");

        }


        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
        if (tm.getSimState() != TelephonyManager.SIM_STATE_UNKNOWN){
            sb.append("Have Sim Card");
            sb.append("\n");
        } else {
            sb.append("No Sim Card");
            sb.append("\n");
        }


        sb.append("Line Number = ");
        sb.append(tm.getLine1Number());
        sb.append("\n");

        sb.append(" IMSI number / SubscriberId = ");
        sb.append(tm.getSubscriberId());
        sb.append("\n");



        int simState = tm.getSimState();
        String simCountry = null;
        String simOperatorCode = null;
        String simOperatorName = null;
        String simSerial = null;

        switch (simState) {

            case (TelephonyManager.SIM_STATE_ABSENT): break;
            case (TelephonyManager.SIM_STATE_NETWORK_LOCKED): break;
            case (TelephonyManager.SIM_STATE_PIN_REQUIRED): break;
            case (TelephonyManager.SIM_STATE_PUK_REQUIRED): break;
            case (TelephonyManager.SIM_STATE_UNKNOWN): break;
            case (TelephonyManager.SIM_STATE_READY): {

                // Get the SIM country ISO code
                simCountry = tm.getSimCountryIso();

                // Get the operator code of the active SIM (MCC + MNC)
                simOperatorCode = tm.getSimOperator();

                // Get the name of the SIM operator
                simOperatorName = tm.getSimOperatorName();

                // Get the SIM’s serial number
                simSerial = tm.getSimSerialNumber();
            }
        }

        sb.append("SIM Country Code = ");
        sb.append(simCountry);
        sb.append("\n");

        sb.append("SIM Operator Code = ");
        sb.append(simOperatorCode);
        sb.append("\n");

        sb.append("SIM Operator Name = ");
        sb.append(simOperatorName);
        sb.append("\n");

        sb.append("SIM Serial Number = ");
        sb.append(simSerial);
        sb.append("\n");


        Cursor cursor2 = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor2.moveToFirst()) { // must check the result to prevent exception
            do {
                String msgData = "";
                for(int idx=0;idx<cursor2.getColumnCount();idx++)
                {
                    msgData += " " + cursor2.getColumnName(idx) + ":" + cursor2.getString(idx);
                }

                sb.append("SMS");
                sb.append(msgData);
                sb.append("\n");

            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }



        OwnerInfo ownerInfo = new OwnerInfo(this);
        sb.append("\n");
        sb.append("Owner Info\n");
        sb.append("==========\n");
        sb.append("id : " + ownerInfo.id + "\n");
        sb.append("email : " + ownerInfo.email + "\n");
        sb.append("phone : " + ownerInfo.phone + "\n");
        sb.append("account name : " + ownerInfo.accountName + "\n");
        sb.append("name : " + ownerInfo.name + "\n");


        Log.i("INFO",sb.toString());

        TextView text = (TextView) findViewById(R.id.info);
        text.setText(sb.toString());
    }

    public String getUsername() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

            if (parts.length > 1)
                return parts[0];
        }
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
