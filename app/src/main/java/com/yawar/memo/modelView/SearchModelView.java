package com.yawar.memo.modelView;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yawar.memo.adapter.SearchAdapter;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.fragment.SearchFragment;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.ChatRoomRespone;
import com.yawar.memo.model.SearchRespone;
import com.yawar.memo.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SearchModelView extends ViewModel {

    public MutableLiveData<ArrayList<SearchRespone>> liveSearchResponeList;
    public ArrayList<SearchRespone> searchResponeArrayList;
    public MutableLiveData<Boolean> loading;


    public SearchModelView() {
        liveSearchResponeList = new MutableLiveData<>();
        searchResponeArrayList = new ArrayList<>();
        loading = new MutableLiveData<>(false);

    }


    @SuppressLint("CheckResult")
    public MutableLiveData<ArrayList<SearchRespone>> search(String searchParameter, String page, String myId) {
        if(searchParameter.isEmpty()){
            loading.postValue(true);
        }
        Single<String> observable = RetrofitClient.getInstance().getapi().search(searchParameter,page,myId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(s->{
            loading.setValue(false);

            JSONObject respObj = null;

            System.out.println("search"+searchParameter);
            try {
                if(searchResponeArrayList!=null){
                    searchResponeArrayList.clear();

                }

            respObj = new JSONObject(s);
            JSONArray jsonArray = (JSONArray) respObj.get("data");
            System.out.println(jsonArray.length()+"jsonArray.length()");

            for (int i = 0; i <= jsonArray.length() - 1; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String phone = jsonObject.getString("phone");
                String name = jsonObject.getString("first_name")+" "+jsonObject.getString("last_name");
                String secretNumber = jsonObject.getString("sn");
                String image = jsonObject.getString("image");
                String token = jsonObject.getString("token");
                String blockedFor = jsonObject.getString("blocked_for");

                searchResponeArrayList.add(new SearchRespone(id, name, secretNumber, image, phone,token,blockedFor,true));


            }

         liveSearchResponeList.setValue(searchResponeArrayList);



        } catch (JSONException e) {
            e.printStackTrace();
        }



        } ,s -> {
            liveSearchResponeList.setValue(null);
            loading.setValue(false);

        });


        return liveSearchResponeList;
    }




}
