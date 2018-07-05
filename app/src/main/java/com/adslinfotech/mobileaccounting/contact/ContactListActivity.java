package com.adslinfotech.mobileaccounting.contact;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.adslinfotech.mobileaccounting.utils.AppConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ContactListActivity extends SimpleAccountingActivity implements OnItemClickListener {
  private ArrayList<ContactBean> list = new ArrayList();
  private ListView listView;
  private ContactAdapter mAdapter;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.activity_contact_list);
    this.listView = (ListView) findViewById(R.id.list);
    this.listView.setOnItemClickListener(this);
    ContentResolver cr = getContentResolver();
    Cursor phones = getContentResolver().query(Phone.CONTENT_URI, null, null, null, null);
    while (phones.moveToNext()) {
      String name = phones.getString(phones.getColumnIndex("display_name"));
      String phoneNumber = phones.getString(phones.getColumnIndex("data1"));
      String id = phones.getString(phones.getColumnIndex("contact_id"));
      ContactBean objContact = new ContactBean();
      objContact.setEmail(id);
      objContact.setName(name);
      objContact.setPhoneNo(phoneNumber);
      this.list.add(objContact);
    }
    phones.close();
    this.mAdapter = new ContactAdapter(this, this.list);
    this.listView.setAdapter(this.mAdapter);
    if (this.list == null || this.list.size() == 0) {
      showToast("No Contact Found!!!");
    } else {
      Collections.sort(this.list, new Comparator<ContactBean>() {
        public int compare(ContactBean lhs, ContactBean rhs) {
          return lhs.getName().compareTo(rhs.getName());
        }
      });
    }
  }

  private void showToast(String msg) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
  }

  public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
    ContactBean dao = (ContactBean) this.mAdapter.getItem(position);
    String email = "";
    try {
      Cursor emailCur = getContentResolver().query(Email.CONTENT_URI, null, "contact_id = ?", new String[]{dao.getEmail()}, null);
      while (emailCur.moveToNext()) {
        email = emailCur.getString(emailCur.getColumnIndex("data1"));
      }
      emailCur.close();
    } catch (Exception e) {
    }
    dao.setEmail(email);
    Intent intent = new Intent();
    intent.putExtra(AppConstants.ACCOUNT_SELECTED, dao);
    setResult(-1, intent);
    finish();
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.menu2, menu);
    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
    searchView.setSearchableInfo(((SearchManager) getSystemService(Context.SEARCH_SERVICE)).getSearchableInfo(getComponentName()));
    searchView.setOnQueryTextListener(new OnQueryTextListener() {
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      public boolean onQueryTextChange(String newText) {
        if (newText != null) {
          ContactListActivity.this.mAdapter.getFilter().filter(newText.toString().trim());
        } else {
          ContactListActivity.this.mAdapter.getFilter().filter("");
        }
        return false;
      }
    });
    menu.findItem(R.id.menu_add).setVisible(false);
    menu.findItem(R.id.menu_search).setVisible(true);
    return true;
  }
}
