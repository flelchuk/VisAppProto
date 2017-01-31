package com.example.christian.vappprotoneu;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import ezvcard.VCard;
import ezvcard.io.text.VCardReader;

public class ShowContactActivity extends AppCompatActivity {
    private Profile m_Profile;

    private void loadProfile(String v)
    {
        m_Profile = null;
        VCardReader rdr = new VCardReader(v);

        try
        {
            try {

                VCard vcard = rdr.readNext();
                // should be non-null :-)

                m_Profile = new Profile(vcard);

                // update UI
                TextView tv = (TextView)findViewById(R.id.textView3);
                tv.setText(vcard.write());

            }
            finally
            {
                rdr.close();
            }
        }
        catch (java.io.IOException ioe) {
            // should not happen :-)
        }
    }

    public void exportContact(View view) {

        //TODO: String parsen, vermutlich an anderer Stelle sinnvoller

        final int REQUEST_PERMISSION_WRITE_CONTACTS=1;

        //Wenn APILevel < 23, dann Rechte bei Installation erhalten,
        //sonst muss waehrend der  Laufzeit angefordert werden

        //Rechte zum Schreiben von Kontakten nicht vorhanden?
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            //Dann Rechte anfragen
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CONTACTS},
                    REQUEST_PERMISSION_WRITE_CONTACTS);
        }


        //Liste an auszufuehrenden Operationen anlegen
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        //Kontakt anlegen
        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        m_Profile.exportContact(ops, 0);

        // Liste mit Befehlen an Contact Provider ubergeben
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Toast.makeText(getApplicationContext(), "Kontakt " + m_Profile.DisplayName() + " exportiert", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), "Fehler: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Fehler", Toast.LENGTH_SHORT).show();
            //Log.i("EXE",e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contact);
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        */

        String v = (String)getIntent().getExtras().get("vcard");
        loadProfile(v);
    }

    public void done(View view)
    {
        finish();
    }

}
