package com.yawar.memo.modelView;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.CallModel;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.ChatRoomRespone;
import com.yawar.memo.model.SearchRespone;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.retrofit.RetrofitClient;
import com.yawar.memo.utils.BaseApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CallHistoryModelView extends ViewModel {

    BaseApp baseApp = BaseApp.getInstance();



    public MutableLiveData<ArrayList<CallModel>> callModelListMutableLiveData;
    public final ArrayList<CallModel> callModelsList ;
    SimpleDateFormat format;
    Date date;

    @SuppressLint("SimpleDateFormat")
    public CallHistoryModelView() {
        callModelListMutableLiveData = new MutableLiveData<>();
        callModelsList = new ArrayList<>();
        format = new SimpleDateFormat("mm:ss");
    }

    @SuppressLint("CheckResult")
    public void loadData(String my_id) {
        System.out.println("loadDataCall"+my_id);

        Single<String> observable = RetrofitClient.getInstance(AllConstants.base_node_url).getapi().getMyCalls(my_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(s->{
            System.out.println("loadDataCall"+s);
            JSONArray jsonArray = null;
            try {
                 jsonArray = new JSONArray(s);

                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String userName = jsonObject.getString("first_name")+" "+jsonObject.getString("last_name");
                    String id = jsonObject.getString("id");
                    String caller_id = jsonObject.getString("caller");
                    String call_type = jsonObject.getString("call_type");
                    String call_status = jsonObject.getString("call_state");

                    String image = jsonObject.getString("profile_image");
                    String answer_id = jsonObject.getString("answer");
                    String duration = jsonObject.getString("duration");
                    String createdAt = jsonObject.getString("call_time");




                    callModelsList.add(new CallModel(id, userName, caller_id, image,call_type, answer_id, call_status,duration,createdAt));



                    }

                callModelListMutableLiveData.setValue(callModelsList);



            } catch (JSONException e) {
                e.printStackTrace();
                callModelListMutableLiveData.setValue(null);
            }


        } ,s -> {
            System.out.println("loadDataCallerrorr"+s);
            callModelListMutableLiveData.setValue(null);
        });


    }

    Date getDateFromString(String dateString){

        try {
             date = format.parse(dateString);
            System.out.println(date+"dateeeeee");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}