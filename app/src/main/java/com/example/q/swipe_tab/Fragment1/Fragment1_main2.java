package com.example.q.swipe_tab.Fragment1;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.q.swipe_tab.R;


public class Fragment1_main2 extends AppCompatActivity {
    private EditText mFirstname;
    private EditText mLastname;
    private EditText mEmailAddress;
    private EditText mPhonenumber;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment1_main2);
        this.button = (Button) findViewById(R.id.button);
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAdd_Contact_onClick(button);


            }

        });
    }

    public void btnAdd_Contact_onClick (View view){

        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        EditText mFirstname = (EditText) findViewById(R.id.txtfirstname);
        EditText mLastname = (EditText) findViewById(R.id.txtlastname);
        EditText mEmailAddress = (EditText) findViewById(R.id.txtEmail);
        EditText mPhoneNumber = (EditText) findViewById(R.id.txtTelephone);

        intent
                .putExtra(ContactsContract.Intents.Insert.EMAIL, mEmailAddress.getText())
                .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .putExtra(ContactsContract.Intents.Insert.PHONE, mPhoneNumber.getText())
                .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .putExtra(ContactsContract.Intents.Insert.NAME, mFirstname.getText() + " " + mLastname.getText());
        startActivity(intent);
        finish();

    }
}





