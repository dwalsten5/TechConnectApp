package org.centum.techconnect.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import org.centum.techconnect.R;

public class IntroTutorial extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDepthAnimation();
        showSkipButton(true);
        setProgressIndicator();
        setBarColor(getResources().getColor(R.color.colorPrimary));


        int white = getResources().getColor(android.R.color.white);
        int black = getResources().getColor(android.R.color.black);
        int darkGreen = getResources().getColor(R.color.colorPrimaryDark);
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_welcome),
                getString(R.string.tutorial_msg_welcome),
                R.drawable.tech_connect_app_icon, white, black, black));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_self_help_home),
                getString(R.string.tutorial_msg_self_help_home),
                R.drawable.home, darkGreen));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_select_room_device),
                getString(R.string.tutorial_msg_select_room_device),
                R.drawable.expanded, darkGreen));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_add_notes),
                getString(R.string.tutorial_msg_add_notes),
                R.drawable.addnotes, darkGreen));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_troubleshoot),
                getString(R.string.tutorial_msg_troubleshoot),
                R.drawable.tutorial_options, darkGreen));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_tutorials),
                getString(R.string.tutorial_msg_tutorials),
                R.drawable.tutorial_help_options, darkGreen));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_finish_repair),
                getString(R.string.tutorial_msg_finish_repair),
                R.drawable.tutorial_success, darkGreen));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_additional_features),
                getString(R.string.tutorial_msg_additional_features),
                R.drawable.tutorial_swipe, darkGreen));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

}