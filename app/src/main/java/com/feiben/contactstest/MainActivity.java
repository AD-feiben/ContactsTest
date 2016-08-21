package com.feiben.contactstest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    ListView contactsView;

    ArrayAdapter<String> adapter;

    List<String> contactsList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactsView = (ListView) findViewById(R.id.contacts_view);
        showContacts();
    }

    private void showContacts() {
        // 检查SDK版本并判断权限是否授权
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED) {

            //请求权限，之后会回调onRequestPermissionsResult()
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            //Android版本小于6.0或者已经授权
            getContacts();
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactsList);
            contactsView.setAdapter(adapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //权限同意授权，然后重新调用showContacts()
                showContacts();
            } else {
                //不同意授权，用Toast提示用户
                Toast.makeText(this, "Until you grant the permission, we canot display the contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getContacts() {
        Cursor cursor = null;
        //查询联系人数据
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                //获取联系人姓名
                String displayName = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                //获取联系人姓名
                String number = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));

                contactsList.add(displayName + "\n" + number);
            }
            cursor.close();
        }
    }
}
