package com.example.devyankshaw.whatsappcloneapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class WhatsAppUsers extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private ArrayList<String> arrayList;
    private ArrayAdapter arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_app_users);

        listView = findViewById(R.id.listView);//This listView is used to populate with the parseUsers of the server
        arrayList = new ArrayList();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);//(context, row or  item list, data)

        listView.setOnItemClickListener(WhatsAppUsers.this);
        final TextView txtLoadingUsers = findViewById(R.id.txtLoadingUsers);
        final SwipeRefreshLayout mySwipeRefreshLayout = findViewById(R.id.swipeContainer);

        try {//Here try block is used because if initially there is no user then parseQuery will not work and it may lead to app crash
            ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();

            parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());//Adding query not to accept the current username

            parseQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {//There is no errors then if blocks executes
                        if (objects.size() > 0) {//There must be atleast one object of type ParseUser then if block executes

                            for (ParseUser user : objects) {//Iterating over each ParseUsers in the server

                                arrayList.add(user.getUsername());//This arrayList holds the username of the users except the current user
                            }

                            listView.setAdapter(arrayAdapter);/*By this the listView will know that we have an array adapter
                                                             and the listView will go ahead and update itself based
                                                             on the data that is sent from array adapter.*/
                            txtLoadingUsers.animate().alpha(0).setDuration(1000);//This textView will  be disappeared in 1s
                            listView.setVisibility(View.VISIBLE);
                        }else {
                            txtLoadingUsers.animate().alpha(0).setDuration(1000);//This textView will  be disappeared in 1s
                            FancyToast.makeText(WhatsAppUsers.this, "No Users", Toast.LENGTH_LONG,FancyToast.INFO,true).show();
                        }

                    }
                }
            });
        }catch (Exception e){
            txtLoadingUsers.animate().alpha(0).setDuration(1000);//This textView will  be disappeared in 1s
            FancyToast.makeText(this, "No Users", Toast.LENGTH_LONG,FancyToast.INFO,true).show();

        }

        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {//This onRefresh() is notified when users pulls to fresh then it will the executes the codes under it

                try {//Here try block is used because if initially there is no user then parseQuery will not work and it may lead to app crash
                    ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
                    parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());//Adding query not to accept the current username

                    parseQuery.whereNotContainedIn("username", arrayList);//We don't want to get the users that are already inside the arrayList instead of we want the new users which are are not there in arrayList

                    parseQuery.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (objects.size() > 0) {//If there are new users then this if block executes
                                if (e == null) {//There must be atleast one object of type ParseUser then if block executes

                                    for (ParseUser user : objects) {//Iterating over each ParseUsers in the server

                                        arrayList.add(user.getUsername());//This arrayList holds the username of the users except the current user
                                    }

                                    arrayAdapter.notifyDataSetChanged();//As some new users/data are added to the arrayList so this notifyDataSetChanged() is called and henece the adapter will help to show the updated data to the user
                                    if(mySwipeRefreshLayout.isRefreshing()){//If user try to refresh and it is refreshing then if block executes
                                        mySwipeRefreshLayout.setRefreshing(false);//Stops refreshing
                                    }
                                }else {
                                    txtLoadingUsers.animate().alpha(0).setDuration(1000);//This textView will  be disappeared in 1s
                                    FancyToast.makeText(WhatsAppUsers.this, "No Users", Toast.LENGTH_LONG,FancyToast.INFO,true).show();
                                }

                            }else {//If there are no users who signed up then else block will execute
                                if(mySwipeRefreshLayout.isRefreshing()){//If user try to refresh and it is refreshing then if block executes
                                    mySwipeRefreshLayout.setRefreshing(false);//Stops refreshing
                                }
                            }
                        }
                    });
                }catch (Exception e){
                    txtLoadingUsers.animate().alpha(0).setDuration(1000);//This textView will  be disappeared in 1s
                    FancyToast.makeText(WhatsAppUsers.this, "No Users", Toast.LENGTH_LONG,FancyToast.INFO,true).show();

                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //MenuInflater menuInflater = getMenuInflater();//inflating or adding menu to our social media activity
        //or
        getMenuInflater().inflate(R.menu.my_menu, menu);//inflating or adding menu to our social media activity

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutUserItem){

            FancyToast.makeText(WhatsAppUsers.this, ParseUser.getCurrentUser().getUsername() + " is logged out!", Toast.LENGTH_LONG, FancyToast.INFO, true).show();
            ParseUser.getCurrentUser().logOut();
            finish();//In order to not show the Social Media Activity after user has logged out
            Intent intent = new Intent(WhatsAppUsers.this, LoginActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(WhatsAppUsers.this, WhatsAppChatActivity.class);
        intent.putExtra("selectedUser", arrayList.get(position));
        startActivity(intent);
    }
}
