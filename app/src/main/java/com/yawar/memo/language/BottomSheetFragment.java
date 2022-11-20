package com.yawar.memo.language;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.yawar.memo.R;
import com.yawar.memo.ui.settingPage.SettingsFragment;
import com.yawar.memo.language.helper.LocaleHelper;
import com.yawar.memo.ui.dashBoard.DashBord;


import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class BottomSheetFragment extends BottomSheetDialogFragment{

    ImageView tickEnglish;
    ImageView tickEgy;

    SettingsFragment main;
    Locale myLocale;
    String currentLang;
    String currentLanguage = "en";


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        //Set the custom view
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_bottom_sheet, null);
        dialog.setContentView(view);
        main = new SettingsFragment();

        //removing all tick mark from the layout
        tickEnglish = view.findViewById(R.id.tick_english);
        tickEnglish.setVisibility(View.GONE);
        tickEgy = view.findViewById(R.id.tick_egy);
        tickEgy.setVisibility(View.GONE);



        currentLanguage = getActivity().getIntent().getStringExtra(currentLang);

        //case for making the tick mark alive
        switch(LocaleHelper.getLanguage(getActivity()))
        {
            case "en":
                tickEnglish.setVisibility(View.VISIBLE);
                break;
            case "ar":
                tickEgy.setVisibility(View.VISIBLE);
                break;
            default:
                System.out.println("no match");
        }

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        final CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    String state = "";

                    switch (newState) {
                        case BottomSheetBehavior.STATE_DRAGGING: {
                            state = "DRAGGING";
                            break;
                        }
                        case BottomSheetBehavior.STATE_SETTLING: {
                            state = "SETTLING";
                            break;
                        }
                        case BottomSheetBehavior.STATE_EXPANDED: {
                            state = "EXPANDED";
                            break;
                        }
                        case BottomSheetBehavior.STATE_COLLAPSED: {
                            state = "COLLAPSED";
                            break;
                        }
                        case BottomSheetBehavior.STATE_HIDDEN: {
                            dismiss();
                            state = "HIDDEN";
                            break;
                        }
                    }
                //    Toast.makeText(getContext(), "Bottom Sheet State Changed to: " + state, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        }

        //close icon of bottom sheet
        ImageView imageViewClose = view.findViewById(R.id.imageView);
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //to close the bottom sheet
                ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_HIDDEN);

            }
        });

        //onclick on english language
        ImageView flagEnglish = view.findViewById(R.id.flagView_english);
        flagEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("en");
                //to close the bottom sheet
                ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        //onclick on arabic language
       ImageView flagEgypt = view.findViewById(R.id.flagView_egy);
        flagEgypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("ar");
                //to close the bottom sheet
                ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });



    }

    //to change the language and refresh the screen
    public void setLocale(String localeName) {
        if (!localeName.equals(currentLanguage)) {
            Context context = LocaleHelper.setLocale(getActivity(), localeName);
            //Resources resources = context.getResources();
            LocaleHelper.setLocale(context, localeName);
            myLocale = new Locale(localeName);
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(getActivity(), DashBord.class);
            refresh.putExtra(currentLang, localeName);
            getActivity().finish();
            startActivity(refresh);
        } else {
            //Toast.makeText(getActivity(), "Language already selected!", Toast.LENGTH_SHORT).show();
        }
    }
}
