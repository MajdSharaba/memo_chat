package com.yawar.memo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.fragment.SearchFragment;
import com.yawar.memo.model.SearchRespone;

import java.util.ArrayList;



    public class SearchAdapter extends RecyclerView.Adapter<com.yawar.memo.adapter.SearchAdapter.ViewHolders> {
        ///Initialize variable
         SearchFragment searchFragment;
        Activity activity;
        float textSize = 14.0F ;
        SharedPreferences sharedPreferences ;

        ArrayList<SearchRespone> arrayList;
        private CallbackInterface mCallback;
        public interface CallbackInterface{

            /**
             * Callback invoked when clicked
             * @param position - the position
             * @param searchRespone - the text to pass back
             */
            void onHandleSelection(int position, SearchRespone searchRespone);
            void onClickItem(int position, SearchRespone searchRespone);

        }


        ///Create constructor
        public SearchAdapter(SearchFragment searchFragment,Activity activity, ArrayList<SearchRespone> arrayList) {
            this.searchFragment = searchFragment;
            this.activity = activity;
            this.arrayList = arrayList;
            try{
                mCallback = searchFragment;
            }catch(ClassCastException ex){
                //.. should log the error or throw and exception
            }
//            notifyDataSetChanged();
        }


        @NonNull
        @Override
        public com.yawar.memo.adapter.SearchAdapter.ViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ///Initialize view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);

            sharedPreferences = activity.getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);

            SearchAdapter.ViewHolders holder = new SearchAdapter.ViewHolders(view);

            return holder;
        }


        @SuppressLint("RecyclerView")
        @Override
        public void onBindViewHolder(@NonNull com.yawar.memo.adapter.SearchAdapter.ViewHolders holder, int position) {
            try {
               if(arrayList.size()>0) {

                   SearchRespone model = arrayList.get(holder.getAdapterPosition());
                   holder.tvName.setText(model.getName());
                   holder.tvNumber.setText(model.getSecretNumber());
                   System.out.println(model.getImage());

                   // Glide.with(holder.imageView.getContext()).load(model.getImage()).into(holder.imageView);
                   if (!model.getImage().isEmpty()) {
                       Glide.with(holder.imageView.getContext()).load(AllConstants.imageUrl + model.getImage()).error(activity.getDrawable(R.drawable.th)).into(holder.imageView);
                   }
                   if (!contactExists(model.getPhone())) {
                       holder.button.setVisibility(View.VISIBLE);
                   } else {
                       holder.button.setVisibility(View.INVISIBLE);
                   }
                   holder.itemView.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           if (mCallback != null) {
                               mCallback.onClickItem(holder.getAdapterPosition(), arrayList.get(holder.getAdapterPosition()));
                           }
                       }
                   });
                   holder.button.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           if (mCallback != null) {
                               mCallback.onHandleSelection(holder.getAdapterPosition(), arrayList.get(holder.getAdapterPosition()));
                           }
                       }
                   });
//            holder.button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    System.out.println("majdno Erooooor");
//                    Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
//                    contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
//
//                    contactIntent
//                            .putExtra(ContactsContract.Intents.Insert.NAME, model.getName())
//                            .putExtra(ContactsContract.Intents.Insert.PHONE, model.getPhone());
//
//
//                }
//            });

               }
            }catch (Exception e){
                System.out.println("problemmmmmm");
            }


        }
//        @Override
//        protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//            super.onActivityResult(requestCode, resultCode, intent);
//
//            if (requestCode == 1)
//            {
//                if (resultCode == Activity.RESULT_OK) {
//                    Toast.makeText(this, "Added Contact", Toast.LENGTH_SHORT).show();
//                }
//                if (resultCode == Activity.RESULT_CANCELED) {
//                    Toast.makeText(this, "Cancelled Added Contact", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public  boolean contactExists(String number){
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(number));

            String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};

            Cursor cur = activity.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
            try {
                if (cur.moveToFirst()) {
                    // if contact are in contact list it will return true
                    System.out.println("true");
                    return true;
                }} finally {
                if (cur != null)
                    cur.close();
            }
            //if contact are not match that means contact are not added
            return  false;
        }

        public void addToContact (String name, String number){

                            ArrayList <ContentProviderOperation> ops = new ArrayList < ContentProviderOperation > ();

                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());

                //------------------------------------------------------ Names
                if (name != null) {
                    System.out.println("name added");
                    ops.add(ContentProviderOperation.newInsert(
                            ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(
                                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                    name).build());
                }

                //------------------------------------------------------ Mobile Number
                if (number != null) {
                    ops.add(ContentProviderOperation.
                            newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                            .build());
                }
            // Asking the Contact provider to create a new contact
            try {
                activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
                notifyDataSetChanged();


        }


        public class ViewHolders extends RecyclerView.ViewHolder {
            ///Initialize variable
            TextView tvName;
            TextView tvNumber;
            ImageView imageView;

            Button button;
            public ViewHolders(@NonNull View itemView) {
                super(itemView);
                ////Assign variable
                tvName = itemView.findViewById(R.id.tv_name);
                tvName.setTextSize(textSize);
                tvName.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

                tvNumber = itemView.findViewById(R.id.tv_number);
                tvNumber.setTextSize(textSize);
                tvNumber.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


                imageView = itemView.findViewById(R.id.iv_image);
                button = itemView.findViewById(R.id.btn_add);
            }
        }
    }


