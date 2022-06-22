package com.yawar.memo.fragment;

//import android.animation.Animator;
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.widget.AppCompatImageView;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.provider.OpenableColumns;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewAnimationUtils;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.Toast;
//
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.NetworkResponse;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.bumptech.glide.Glide;
//import com.devlomi.circularstatusview.CircularStatusView;
//import com.github.dhaval2404.imagepicker.ImagePicker;
//import com.gowtham.library.utils.LogMessage;
//import com.gowtham.library.utils.TrimType;
//import com.gowtham.library.utils.TrimVideo;
//import com.yawar.memo.sessionManger.ClassSharedPreferences;
//import com.yawar.memo.R;
//import com.yawar.memo.StoriesActivity;
//import com.yawar.memo.adapter.StatusAdapter;
//import com.yawar.memo.constant.AllConstants;
//import com.yawar.memo.model.ChatMessage;
//import com.yawar.memo.model.Status;
//import com.yawar.memo.model.UserSeen;
//import com.yawar.memo.model.UserStatus;
//import com.yawar.memo.utils.BaseApp;
//import com.yawar.memo.utils.FilePath;
//import com.yawar.memo.utils.FileUtil;
//import com.yawar.memo.utils.VolleyMultipartRequest;
//import com.yawar.memo.views.ConversationActivity;
//import com.yawar.memo.views.IntroActivity;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.text.DateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Observable;
//import java.util.Observer;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link StoriesFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class StoriesFragment extends Fragment  implements Observer {
//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//
//    public StoriesFragment() {
//        // Required empty public constructor
//    }
//
//    ArrayList<UserStatus> userStatusList = new ArrayList<>();
//    ArrayList<UserStatus> onlyUserStatusList = new ArrayList<>();
//    private RequestQueue rQueue;
//
//
//    final ArrayList<Status> statuses = new ArrayList<>();
//    private final ArrayList<String> resources = new ArrayList<String>();
//    BaseApp myBase;
//    StatusAdapter adapter;
//    ImageButton imageButton;
//    AppCompatImageView imageGallery;
//    AppCompatImageView takeImage;
//    AppCompatImageView takeVideo;
//    String myId;
//    ArrayList<UserSeen> postList = new ArrayList<>();
//
//
//
//    boolean viewVisability = false;
//    LinearLayout linearLayout;
//    int IMAGE_PICKER_SELECT = 100;
//    UserStatus myUserStatus;
//    CircleImageView circleImageView;
//    CircleImageView circleTakePicture;
//    ClassSharedPreferences classSharedPreferences;
//
//
//    final String TAG = "StoriesFragment";
//
//
//    LinearLayout linerMyStory;
//    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK &&
//                        result.getData() != null) {
//                    System.out.println("majd");
//                    Uri uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.getData()));
//                   String uriSt = uri.toString();
////                    String selectedFilePath = FilePath.getPath(getContext(), uri);
//
//                    File myFile = new File(uriSt);
//                    String uriString =Uri.fromFile(myFile).toString();
//                    circleImageView.setImageURI(uri);
//                    ArrayList<UserSeen> post = new ArrayList<>();
//
//                    post.add(new UserSeen(uri.toString()+"sh"));
//
//                    myUserStatus.getStatusList().add(new Status("1",myId,true, uri.toString(),"video",post));
//                    myBase.getStoriesObserve().setMyStatus(myUserStatus);
//
//                    System.out.println("   Uri uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.getData()));"+uriString);
//
//                    String displayName = null;
//
//                    if (uriString.startsWith("content://")) {
//                        System.out.println("uriString.startsWith(\"content://\")");
//                        Cursor cursor = null;
//                        try {
//                            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
//                            if (cursor != null && cursor.moveToFirst()) {
//                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                                Log.d("nameeeee>>>>  ", displayName);
//
//                                upload(displayName, Uri.parse(uriString),"video");
//                            }
//                        } finally {
//                            cursor.close();
//                        }
//                    } else if (uriString.startsWith("file://")) {
//
//                        displayName = myFile.getName();
//                        upload(displayName, Uri.parse(uriString),"video");
//
//                        Log.d("nameeeee>>>>  ", displayName);
//                    }
//                    else {
////                        upload("video", uri,"video");
//
//                    }
//                    Log.d(TAG, "Trimmed path:: " + uri);
//
//                } else
//                    LogMessage.v("videoTrimResultLauncher data is null");
//            });
//
//
//    public static StoriesFragment newInstance(String param1, String param2) {
//        StoriesFragment fragment = new StoriesFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_stories, container, false);
//        RecyclerView recyclerView = view.findViewById(R.id.rv);
//        myBase = (BaseApp) getActivity().getApplication();
//        myBase.getObserver().addObserver(this);
//        imageGallery = view.findViewById(R.id.image_pick);
//        circleImageView = view.findViewById(R.id.circle_image);
//        linerMyStory = view.findViewById(R.id.liner_my_story);
//        circleTakePicture = view.findViewById(R.id.circle_add);
//        linearLayout = view.findViewById(R.id.dataLayout);
//        takeImage = view.findViewById(R.id.take_image);
//        takeVideo = view.findViewById(R.id.take_video);
//        classSharedPreferences = new ClassSharedPreferences(getContext());
//        myId = classSharedPreferences.getUser().getUserId();
//
//
//
//
//
//
//        if (myBase.getStoriesObserve().getUserStatusList().isEmpty()) {
//
//
//            statuses.add(new Status(false, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4","video"));
//            statuses.add(new Status(false, "https://th.bing.com/th/id/OIP.PYc18o6D8pBo6zMh5pzOjAHaE8?pid=ImgDet&w=825&h=550&rs=1","image"));
//            statuses.add(new Status(false, "https://mcdn.wallpapersafari.com/small/68/44/NS02fQ.jpg","image"));
//
//
//            UserStatus userStatus = new UserStatus("majd ", false, statuses);
//            userStatusList.add(userStatus);
//            System.out.println("myBase.getObserver().setUserStatusList(userStatusList);");
//            myBase.getStoriesObserve().setUserStatusList(userStatusList);
//        } else {
//            userStatusList = myBase.getStoriesObserve().getUserStatusList();
//
//
//        }
//        if (myBase.getStoriesObserve().getMyStatus() == null) {
//
//            myUserStatus = new UserStatus("memo", false, new ArrayList<>());
//            myBase.getStoriesObserve().setMyStatus(myUserStatus);
//            getMyStory();
//        } else {
//            myUserStatus = myBase.getStoriesObserve().getMyStatus();
//            if (!myUserStatus.getStatusList().isEmpty())
//                circleImageView.setImageURI(Uri.parse(myBase.getStoriesObserve().getMyStatus().getStatusList().get(0).getUrl()));
//        }
//
//
//        adapter = new StatusAdapter(userStatusList);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(adapter);
//
//        adapter.setOnStatusClickListener(new StatusAdapter.OnStatusClickListener() {
//            @Override
//            public void onStatusClick(CircularStatusView circularStatusView, int pos) {
//                Bundle bundle = new Bundle();
//
//                Intent intent = new Intent(getContext(), StoriesActivity.class);
//                bundle.putInt("id", pos);
//
//
//                intent.putExtras(bundle);
//
//                startActivity(intent);
//
//                UserStatus userStatus = userStatusList.get(pos);
//                if (!userStatus.areAllSeen()) {
//                    for (int i = 0; i < userStatus.getStatusList().size(); i++) {
//                        Status status = userStatus.getStatusList().get(i);
//                        System.out.println(String.valueOf(i) + status.isSeen());
//                        if (!status.isSeen()) {
//                            //update view
//                            circularStatusView.setPortionColorForIndex(i, Color.GRAY);
//                            //update adapter to prevent changes when scrolling
//                            status.setSeen(true);
//                            break;
//                        }
//                    }
//                }
//            }
//        });
//        circleTakePicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (!viewVisability) {
//                    System.out.println("show dialog");
//                    showLayout();
//                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    hideLayout();
//                }
//            }
//
//
//        });
//
//        imageGallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                pickIntent.setType("image/* video/*");
//                startActivityForResult(pickIntent, IMAGE_PICKER_SELECT);
//
//            }
//        });
//        linerMyStory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                if (!myBase.getStoriesObserve().getMyStatus().getStatusList().isEmpty()) {
//
//                    Intent intent = new Intent(getContext(), StoriesActivity.class);
//                    bundle.putInt("id", -1);
//
//
//                    intent.putExtras(bundle);
//
//                    startActivity(intent);
//                }
//
//            }
//        });
//        takeImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ImagePicker.with(getActivity())
//                        .crop()	    			//Crop image(Optional), Check Customization for more option
//                        .compress(1024)
//                        .cameraOnly()			//Final image size will be less than 1 MB(Optional)
//                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
//                        .start();
//            }
//        });
//        takeVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                if(intent.resolveActivity(getActivity().getPackageManager())!=null) {
//                    startActivityForResult(intent, 100);
//                }
//
//            }
//        });
//
//
//        return view;
//
//    }
//    /////get my story from Api
//    private void getMyStory() {
//        System.out.println("my id"+myId);
//        final ProgressDialog progressDialo = new ProgressDialog(getContext());
//        // url to post our data
//        progressDialo.setMessage("Uploading, please wait...");
//        progressDialo.show();
//        // creating a new variable for our request queue
//        RequestQueue queue = Volley.newRequestQueue(getContext());
//        // on below line we are calling a string
//        // request method to post the data to our API
//        // in this we are calling a post method.
//        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.get_my_story_url, new com.android.volley.Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                progressDialo.dismiss();
//                try {
//
//
//                    JSONObject respObj = new JSONObject(response);
//                    System.out.println(respObj);
//                    JSONArray jsonArray = (JSONArray) respObj.get("myStore");
////                    JSONArray jsonArray = new JSONArray(respObj.getJSONArray("data"));
//                    System.out.println(jsonArray);
//                    String url = "";
//                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        String id= jsonObject.getString("id");
//                        String user_id = jsonObject.getString("user_id");
//                         url = jsonObject.getString("content");
//                        String type = jsonObject.getString("type");
//                        ArrayList<UserSeen> post =new ArrayList<>();
//                        myUserStatus.getStatusList().add(new Status(id,user_id,true, "http://192.168.1.9:8080/yawar_chat/uploads/storeFiles/"+url,type,post));
//
//
//                    }
//                    Glide.with(circleImageView).load("http://192.168.1.9:8080/yawar_chat/uploads/storeFiles/"+url).into(circleImageView);
//                    myBase.getStoriesObserve().setMyStatus(myUserStatus);
//
//                }
//            catch (JSONException e) {
//                e.printStackTrace();
//                // progressDialog.dismiss();
//            }
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // method to handle errors.
//                Toast.makeText(getContext(), "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                // below line we are creating a map for
//                // storing our values in key and value pair.
//                Map<String, String> params = new HashMap<String, String>();
//
//                // on below line we are passing our key
//                // and value pair to our parameters.
//                params.put("id",myId);
//
//
//                // at last we are
//                // returning our params.
//                return params;
//            }
//        };
//        // below line is to make
//        // a json object request.
//        queue.add(request);
//
//    }
//
//    private void showLayout() {
//        float radius = Math.max(linearLayout.getWidth(), linearLayout.getHeight());
//        Animator animator = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            animator = ViewAnimationUtils.createCircularReveal(linearLayout, linearLayout.getLeft(), linearLayout.getTop(), 0, radius * 2);
//        }
//        animator.setDuration(800);
//        linearLayout.setVisibility(View.VISIBLE);
//        viewVisability = true;
//        animator.start();
//
//    }
//
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public void hideLayout() {
//        System.out.println("hideLayout");
//        float radius = Math.max(linearLayout.getWidth(), linearLayout.getHeight());
//        Animator animator = ViewAnimationUtils.createCircularReveal(linearLayout, linearLayout.getLeft(), linearLayout.getTop(), radius * 2, 0);
//        animator.setDuration(800);
//        viewVisability = false;
//        linearLayout.setVisibility(View.INVISIBLE);
//
//
//        animator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                linearLayout.setVisibility(View.INVISIBLE);
//                System.out.println("View.INVISIBLE)");
//                viewVisability = false;
//
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//                System.out.println("onAnimationCancel");
//
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//                System.out.println("onAnimationCancel");
//
//            }
//        });
//    }
//
//
//    @Override
//    public void update(Observable observable, Object o) {
////        userStatusList.clear();
//        userStatusList = myBase.getStoriesObserve().getUserStatusList();
//        myUserStatus = myBase.getStoriesObserve().getMyStatus();
//        Glide.with(circleImageView).load(myUserStatus.getStatusList().get(0).getUrl()).into(circleImageView);
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        adapter.notifyDataSetChanged();
//
//        if(!myBase.getStoriesObserve().getMyStatus().getStatusList().isEmpty()){
//            Glide.with(circleImageView).load(myUserStatus.getStatusList().get(0).getUrl()).into(circleImageView);
//
//        }
//        else {
//            Glide.with(circleImageView).load(R.drawable.ic_person).into(circleImageView);
//
//        }
//        System.out.println("onResume");
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICKER_SELECT) {
//            hideLayout();
//            Uri selectedMediaUri = data.getData();
//            System.out.println(selectedMediaUri.toString());
//            if (selectedMediaUri.toString().contains("image")) {
//                System.out.println("imageeeeeeeeeeeeeee");
//                circleImageView.setImageURI(selectedMediaUri);
//                ArrayList<UserSeen> post = new ArrayList<>();
//                for(int i=0; i<3;i++){
//
//                post.add(new UserSeen("user"+i));}
//
//                myUserStatus.getStatusList().add(new Status("1",myId,true, selectedMediaUri.toString(),"image",post));
//                myBase.getStoriesObserve().setMyStatus(myUserStatus);
//                String uriString = selectedMediaUri.toString();
//                System.out.println("uriString"+uriString);
//                File myFile = new File(uriString);
//
//                String path = myFile.getAbsolutePath();
//
//
//                String displayName = null;
//
//                if (uriString.startsWith("content://")) {
//                    Cursor cursor = null;
//                    try {
//                        cursor = getActivity().getContentResolver().query(selectedMediaUri, null, null, null, null);
//                        if (cursor != null && cursor.moveToFirst()) {
//                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                            Log.d("nameeeee>>>>  ", displayName);
//
//                            upload(displayName, selectedMediaUri,"image");
//                        }
//                    } finally {
//                        cursor.close();
//                    }
//                } else if (uriString.startsWith("file://")) {
//                    displayName = myFile.getName();
//                    Log.d("nameeeee>>>>  ", displayName);
//                }
////                System.out.println( myBase.getStoriesObserve().myStatus.getStatusList().size());
//
//                System.out.println("imaggggggggggggggggggggggge");
//
//
//            } else {
//                System.out.println("videoooooooooooooooooooooo");
//                TrimVideo.activity(selectedMediaUri.toString())
//                        .setTrimType(TrimType.MIN_MAX_DURATION)
//                        .setMinToMax(1, 15) //seconds
//                        .start(this, startForResult);
//            }
//
//            System.out.println(selectedMediaUri.toString());
//        }
//    }
//
//    private void upload(final String pdfname, Uri pdffile,String type) {
//     System.out.println(type);
//
//        InputStream iStream = null;
//        try {
//
//            iStream = getActivity().getContentResolver().openInputStream(pdffile);
//            //"file:///storage/emulated/0/memo/1640514470604.3gp"
//            final byte[] inputData = getBytes(iStream);
//
//            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, AllConstants.upload,
//                    new Response.Listener<NetworkResponse>() {
//                        @Override
//                        public void onResponse(NetworkResponse response) {
//                            rQueue.getCache().clear();
//                            System.out.println(response.toString());
//
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(getContext().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }) {
//
//                /*
//                 * If you want to add more parameters with the image
//                 * you can do it here
//                 * here we have only one parameter with the image
//                 * which is tags
//                 * */
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("user_id", myId);
//                    params.put("type", type);
//
//
//                    return params;
//                }
//
//                /*
//                 *pass files using below method
//                 * */
//                @Override
//                protected Map<String, DataPart> getByteData() {
//                    Map<String, DataPart> params = new HashMap<>();
//
//                    params.put("store", new DataPart(pdfname, inputData));
//
//                    return params;
//                }
//            };
//
//
//            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
//                    0,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            rQueue = Volley.newRequestQueue(getActivity());
//            rQueue.add(volleyMultipartRequest);
//
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//    public byte[] getBytes(InputStream inputStream) throws IOException {
//        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
//        int bufferSize = 1024;
//        byte[] buffer = new byte[bufferSize];
//
//        int len = 0;
//        while ((len = inputStream.read(buffer)) != -1) {
//            byteBuffer.write(buffer, 0, len);
//        }
//        return byteBuffer.toByteArray();
//    }
//}