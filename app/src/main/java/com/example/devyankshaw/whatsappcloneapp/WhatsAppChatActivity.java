package com.example.devyankshaw.whatsappcloneapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class WhatsAppChatActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView chatListView;
    private ArrayList<String> chatsList;//Holds all the chats
    private ArrayAdapter adapter;//Sets the list of chats on to the screen
    private String selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_app_chat);

        //Stores the user's name with whom the current user is going to chat with which we get from the previous activity
        selectedUser = getIntent().getStringExtra("selectedUser");
        FancyToast.makeText(this, "Chat with " + selectedUser + " now!!!", Toast.LENGTH_LONG, FancyToast.INFO,true).show();

        findViewById(R.id.btnSend).setOnClickListener(this);

        chatListView = findViewById(R.id.chatListView);//In order to access the UI components i.e listView from the xml file, we create a reference of it.
        chatsList = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, chatsList);
        chatListView.setAdapter(adapter);//It is applied here in order to update our chatActivity/listView instantly

        final SwipeRefreshLayout mySwipeRefreshLayout = findViewById(R.id.chatRefresh);

        try {
            //Stores query when sender is the current user and the recipient is the selected user
            final ParseQuery<ParseObject> firstUserChatQuery = ParseQuery.getQuery("Chat");
            //Stores query when sender is the selected user and the recipient is the current user
            final ParseQuery<ParseObject> secondUserChatQuery = ParseQuery.getQuery("Chat");

            firstUserChatQuery.whereEqualTo("waSender", ParseUser.getCurrentUser().getUsername());
            firstUserChatQuery.whereEqualTo("waTargetRecipient", selectedUser);

            secondUserChatQuery.whereEqualTo("waSender", selectedUser);
            secondUserChatQuery.whereEqualTo("waTargetRecipient", ParseUser.getCurrentUser().getUsername());

            //Stores all the above queries
            ArrayList<ParseQuery<ParseObject>> allQueries = new ArrayList<>();
            allQueries.add(firstUserChatQuery);
            allQueries.add(secondUserChatQuery);

            ParseQuery<ParseObject> myQuery = ParseQuery.or(allQueries);//Here or is used because (in brackets) it needs an arrayList of ParseQuery objects i.e myQuery holds the above two queries
            myQuery.orderByAscending("createdAt");//Older message to newer message

            myQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {

                        for (ParseObject chatObject : objects) {

                            String waMessage = chatObject.get("Message") + "";
                            if (chatObject.get("waSender").equals(ParseUser.getCurrentUser().getUsername())) {
                                waMessage = ParseUser.getCurrentUser().getUsername() + ": " + waMessage;
                            }
                            if (chatObject.get("waSender").equals(selectedUser)) {
                                waMessage = selectedUser + ": " + waMessage;
                            }

                            chatsList.add(waMessage);
//                            adapter.notifyDataSetChanged();//It is not the best place to write this code because its not a good programming practise to update/refresh our adapter after each execution of for loop
                        }
                        adapter.notifyDataSetChanged();//It is the better place
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    //Stores query when sender is the current user and the recipient is the selected user
                    final ParseQuery<ParseObject> firstUserChatQuery = ParseQuery.getQuery("Chat");
                    //Stores query when sender is the selected user and the recipient is the current user
                    final ParseQuery<ParseObject> secondUserChatQuery = ParseQuery.getQuery("Chat");

                    firstUserChatQuery.whereEqualTo("waSender", ParseUser.getCurrentUser().getUsername());
                    firstUserChatQuery.whereEqualTo("waTargetRecipient", selectedUser);

                    secondUserChatQuery.whereEqualTo("waSender", selectedUser);
                    secondUserChatQuery.whereEqualTo("waTargetRecipient", ParseUser.getCurrentUser().getUsername());

                    //Stores all the above queries
                    ArrayList<ParseQuery<ParseObject>> allQueries = new ArrayList<>();
                    allQueries.add(firstUserChatQuery);
                    allQueries.add(secondUserChatQuery);

                    ParseQuery<ParseObject> myQuery = ParseQuery.or(allQueries);//Here or is used because (in brackets) it needs an arrayList of ParseQuery objects i.e myQuery holds the above two queries
                    myQuery.orderByDescending("createdAt");//newer message to older message
                    myQuery.getFirst();

                    myQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (objects.size() > 0 && e == null) {

                                for (ParseObject chatObject : objects) {

                                    String waMessage = chatObject.get("Message") + "";
                                    if (chatObject.get("waSender").equals(ParseUser.getCurrentUser().getUsername())) {
                                        waMessage = ParseUser.getCurrentUser().getUsername() + ": " + waMessage;
                                    }
                                    if (chatObject.get("waSender").equals(selectedUser)) {
                                        waMessage = selectedUser + ": " + waMessage;
                                    }

                                    adapter.add(waMessage);
//                            adapter.notifyDataSetChanged();//It is not the best place to write this code because its not a good programming practise to update/refresh our adapter after each execution of for loop
                                }
                                adapter.notifyDataSetChanged();//It is the better place
                                if(mySwipeRefreshLayout.isRefreshing()){//If user try to refresh and it is refreshing then if block executes
                                    mySwipeRefreshLayout.setRefreshing(false);//Stops refreshing
                                }
                            }else {
                                if(mySwipeRefreshLayout.isRefreshing()){//If user try to refresh and it is refreshing then if block executes
                                    mySwipeRefreshLayout.setRefreshing(false);//Stops refreshing
                                }
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
     }

    @Override
    public void onClick(View v) {

        //Sending message
        final EditText edtMessage = findViewById(R.id.edtSend);

        ParseObject chat = new ParseObject("Chat");
        chat.put("waSender", ParseUser.getCurrentUser().getUsername());
        chat.put("waTargetRecipient", selectedUser);
        chat.put("Message", edtMessage.getText().toString());
        chat.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){

                    FancyToast.makeText(WhatsAppChatActivity.this, "Message Sent", Toast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                    chatsList.add(ParseUser.getCurrentUser().getUsername() + ": " + edtMessage.getText().toString());
                    adapter.notifyDataSetChanged();//In order to notify some data has changed and then update it by itself
                    edtMessage.setText("");//Empty the edtMessage
                }
            }
        });

    }
}
