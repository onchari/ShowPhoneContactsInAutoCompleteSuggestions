package com.example.user2.showphonecontactsinautocompletesuggestions;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
/*
- Read phone contact and store contact names in nameValueArr ArrayList and contact phone number in phoneValueArr ArrayList.
- Taking AutoCompleteTextView reference from auto_complete_string.xml file.
- Add nameValueArr ArrayList to adapter and add adapter to AutoCompleteTextView.
 */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    //Initialize variables
    AutoCompleteTextView autoCompleteTextView = null;
    private ArrayAdapter<String> arrayAdapter;

    //Store Contact values in the arrayList
    public  static ArrayList<String> phoneValueArr = new ArrayList<String>();
    public  static ArrayList<String> nameValueArr  = new ArrayList<String>();

    EditText toNumber = null;
    String toNumberValue = "";
    Cursor cursor;

    /*Called when the activity is first created*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button send = (Button) findViewById(R.id.Send);

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.toNumber);

        arrayAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item);
        autoCompleteTextView.setThreshold(1);

        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setOnItemSelectedListener(this);
        autoCompleteTextView.setOnItemClickListener(this);

        /**
         * Read contact data and add data to ArrrayAdapter used by autocomplete
         */
        readContactData();
    }

    private void readContactData() {
        try{
            String phoneNumber = "";
            ContentResolver content_Resolver = getBaseContext().getContentResolver();

            //Query to get contact
           Cursor cursor = content_Resolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);

            //If data was found in contacts
            if(cursor.getCount() > 0){
                Log.i("AutocompleteContacts", "Reading   contacts........");

                int k=0;
                String name = "";
                while (cursor.moveToNext()){
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    // check contact have phone
                    if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0){
                        //Create query to get phone number by contact Id
                        Cursor pCursor =content_Resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?", new  String[]{id},null);

                        int j =0;
                        while (pCursor.moveToNext()){

                            //sometimes get multiple data
                            if(j==0){
                                //get phone number
                                phoneNumber ="" +pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                // Add contacts names to adapter
                                arrayAdapter.add(name);

                                // Add ArrayList names to adapter
                                phoneValueArr.add(phoneNumber.toString());
                                nameValueArr.add(name.toString());

                                j++;
                                k++;
                            }
                        }//End while loop
                    }//end if
                }// end while
            }//end cursor value check

            cursor.close();

        }
        catch (Exception e){
            Log.i("AutoCompleteContact", "Exception : " + e);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // get array index value for th selected name
        int i = nameValueArr.indexOf("" + parent.getItemAtPosition(position));

        //if name exist in name arrayList
        if(i >= 0){
            //get phone number
            toNumberValue = phoneValueArr.get(i);

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            //show alert
            Toast.makeText(getBaseContext(), "Position :"+ parent.getItemAtPosition(position) + "Number : " + toNumberValue, Toast.LENGTH_LONG ).show();
            Log.d("AutocompleteContacts",
                    "Position:"+view+" Name:"+parent.getItemAtPosition(position)+" Number:"+toNumberValue);

        }
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
