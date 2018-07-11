package com.example.q.swipe_tab.Fragment1;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.q.swipe_tab.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment1_main extends Fragment implements View.OnClickListener /*implements View.OnClickListener*/ {

    private ListView lstNames;
    private TextView Individual_Contact;
    private TextView Individual_Contact2;
    private TextView Individual_Contact3;
    private CardView CardView1;
    private CardView CardView2;
    private CardView CardView3;
    private ArrayList<JSONObject> myJSONs;
    private ImageView Phonebutton;
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int REQUEST_CALL = 1;

    public static Fragment1_Adapter adapter;

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab_x, fab1, fab2, fab3;
    private LinearLayout fab1_ex, fab2_ex, fab3_ex;
    TextView cover;

    public static Fragment1_main newInstance(){
        Fragment1_main fragment = new Fragment1_main();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate((savedInstanceState));
    }

    //@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1_main, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View model = getView();

        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);

        cover = model.findViewById(R.id.cover);

        fab = model.findViewById(R.id.fab);
        fab_x = model.findViewById(R.id.fab_x);
        fab1 = model.findViewById(R.id.fab1);
        fab1_ex = model.findViewById(R.id.fab1_ex);
        fab2 = model.findViewById(R.id.fab2);
        fab2_ex = model.findViewById(R.id.fab2_ex);
        fab3 = model.findViewById(R.id.fab3);
        fab3_ex = model.findViewById(R.id.fab3_ex);

        fab.setOnClickListener(this);
        cover.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);

        // Find the list view
        this.lstNames = (ListView) model.findViewById(R.id.lstNames);
        this.Individual_Contact = (TextView) model.findViewById(R.id.Individual_Contact);
        this.Individual_Contact2 = (TextView) model.findViewById(R.id.Individual_Contact2);
        this.Individual_Contact3 = (TextView) model.findViewById(R.id.Individual_Contact3);
        this.CardView1 = (CardView) model.findViewById(R.id.CardView1);
        this.CardView2 = (CardView) model.findViewById(R.id.CardView2);
        this.CardView3 = (CardView) model.findViewById(R.id.CardView3);


        this.lstNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lstNames.setVisibility(View.INVISIBLE);
                CardView1.setVisibility(View.VISIBLE);
                CardView2.setVisibility(View.VISIBLE);
                CardView3.setVisibility(View.VISIBLE);
                Individual_Contact.setVisibility(View.VISIBLE);
                Individual_Contact.setText("Not enough contact details1");
                Individual_Contact2.setVisibility(View.VISIBLE);
                Individual_Contact2.setText("Not enough contact details2");
                Individual_Contact3.setVisibility(View.VISIBLE);
                Individual_Contact3.setText("Not enough contact details3");

                JSONObject JASON = myJSONs.get(i);
                System.out.println(JASON);

                try {
                    Object A = JASON.get("name");
                    Individual_Contact.setText( "" + A + "" );
                    Object B = JASON.get("number");
                    Individual_Contact2.setText("" + B + "" );
                    Object C = JASON.get("email");
                    Individual_Contact3.setText("" + C + "" );
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    System.out.println("error");
                }

            }
        });

        this.Individual_Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lstNames.setVisibility(View.VISIBLE);
                Individual_Contact.setVisibility(View.INVISIBLE);
                Individual_Contact2.setVisibility(View.INVISIBLE);
                Individual_Contact3.setVisibility(View.INVISIBLE);
                CardView1.setVisibility(View.INVISIBLE);
                CardView2.setVisibility(View.INVISIBLE);
                CardView3.setVisibility(View.INVISIBLE);

            }
        });

        this.Individual_Contact2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mNum = Individual_Contact2.getText().toString();
                String tel ="tel:" + mNum;
                startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));
            }
        });

        this.Individual_Contact3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                // email setting 배열로 해놔서 복수 발송 가능
                String mailaddress = Individual_Contact3.getText().toString();
                System.out.println(mailaddress);
                email.putExtra(Intent.EXTRA_EMAIL, new String[] {"dfdffd"});
                email.putExtra(Intent.EXTRA_SUBJECT,"Subject");
                email.putExtra(Intent.EXTRA_TEXT,mailaddress);
                startActivity(email);

            }
        });

// Read and show the contacts
        showContacts();
    }

    public void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            myJSONs = mySort(getContactNames());
            adapter = new Fragment1_Adapter(getContext(), R.layout.fragment1_row, myJSONs);
            lstNames.setAdapter(adapter);
        }

    }

    public ArrayList<JSONObject> mySort(ArrayList<JSONObject> myList)
    {
        ArrayList<JSONObject> sortedList = new ArrayList<JSONObject>();
        int sz0 = myList.size();
        int sz = 0;
        for(int x = 0; x <= sz0-1 ; x++){
            sz = myList.size();
            int z = 0;
            for(int y = 1; y <= sz-1 ; y++) {
                try {
                    String first = myList.get(z).get("name").toString();
                    String second = myList.get(y).get("name").toString();
                    if (first.compareToIgnoreCase(second) > 0)
                        z = y;
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            sortedList.add(myList.remove(z));
        }
        return sortedList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(getContext(), "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Read the name of all the contacts.
     *
     * @return a list of names.
     */
    private ArrayList<JSONObject> getContactNames() {
        ArrayList<JSONObject> contacts = new ArrayList<>();
        // Get the ContentResolver
        ContentResolver cr = getActivity().getContentResolver();
        // Get the Cursor of all the contacts
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        // Move the cursor to first. Also check whether the cursor is empty or not.
        try {
            if (cursor.moveToFirst()) {
                // Iterate through the cursor
                do {
                    // Get the contacts name
                    JSONObject tmpJson = new JSONObject();

                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    tmpJson.put("name",name);
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String phone = null;
                    Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    if (hasPhone > 0) {
                        Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        if (cp != null && cp.moveToFirst()) {
                            phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            cp.close();
                        }
                    }
                    tmpJson.put("number",phone);

                    String email = null;
                    Cursor ce = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    if (ce != null && ce.moveToFirst()) {
                        email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        ce.close();
                    }
                    tmpJson.put("email",email);
                    contacts.add(tmpJson);
                } while (cursor.moveToNext());
            }
            // Close the curosor
            cursor.close();
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        return contacts;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
            case R.id.cover:
                anim();
                break;
            case R.id.fab_x:
                anim();
            case R.id.fab1:
                Intent intent = new Intent(getActivity(), Fragment1_main2.class);
                startActivity(intent);
                anim();
                break;
            case R.id.fab2:
                anim();
                //Toast.makeText(getContext(), "DOWNLOAD Complete", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fab3:
                anim();
                //Toast.makeText(getContext(), "UPLOAD Complete", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void anim() {
        if (isFabOpen) {
            fab.setVisibility(View.VISIBLE);
            fab_x.setVisibility(View.INVISIBLE);
            fab1_ex.startAnimation(fab_close);
            fab2_ex.startAnimation(fab_close);;
            fab3_ex.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            cover.setVisibility(View.INVISIBLE);
            isFabOpen = false;
        } else {
            fab.setVisibility(View.INVISIBLE);
            fab_x.setVisibility(View.VISIBLE);
            fab1_ex.startAnimation(fab_open);
            fab2_ex.startAnimation(fab_open);
            fab3_ex.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            cover.setVisibility(View.VISIBLE);
            isFabOpen = true;
        }
    }
}
