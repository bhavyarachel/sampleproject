package com.example.bhavya.places.ui.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;

import com.example.bhavya.places.R;
import com.example.bhavya.places.pojoclass.placesdetailspojoclass.PlacesDetailsPojoClass;
import com.example.bhavya.places.pojoclass.userdetailspojoclass.UserDetails;
import com.example.bhavya.places.ui.adapter.FavPlacesListViewAdpater;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static com.example.bhavya.places.ui.activity.SignUpActivity.PREF_FILE_NAME;
import static com.example.bhavya.places.ui.activity.SignUpActivity.PREF_LOGIN;

/**
 * A simple {@link Fragment} subclass.
 *
 * Shows the favorite places selected by the user as a list view with few details about the place.
 */
public class FavouritePlacesFragment extends Fragment {

    @BindView(R.id.listviewfavorites)
    ListView mFavoritesListView;


    private String useremail;
    private LinkedHashMap hMap;

    public FavouritePlacesFragment() {
        //Required empty constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_favourite_places, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        getDataFromPreferences();
        readFileOfUser();
        showFavPlacesListView(hMap);
    }

    /**
     * To get the email id of the user for creating the file directory path
     */
    private void getDataFromPreferences() {
        SharedPreferences mSharedPreferences = getContext().getSharedPreferences(PREF_FILE_NAME,
                MODE_PRIVATE);
        String email = mSharedPreferences.getString(PREF_LOGIN, getString(R.string.rerrew));

        String obj = mSharedPreferences.getString(email, "");
        Gson gson = new Gson();
        UserDetails newuser = gson.fromJson(obj, UserDetails.class);
        useremail = newuser.getEmail();
    }

    /**
     * Read the file of the user.
     * If file doesnot exist, create the file; Else read from the file and store in hash map
     */
    private void readFileOfUser() {
        try {
            File mainFolder = new File(Environment.getExternalStorageDirectory(), getString(R
                    .string.setrootpath));
            if (!mainFolder.exists()) {
                mainFolder.mkdir();
            }
            try {
                File file = new File(mainFolder, useremail + getString(R.string.textfile_extension));

                if (file.exists()) {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    hMap = (LinkedHashMap) objectInputStream.readObject();
                    objectInputStream.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (OptionalDataException e) {
                e.printStackTrace();
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To show the places marked as favorite as a listview inflated using custom_info_window layout
     * @param hashMap
     */
    public void showFavPlacesListView(LinkedHashMap<String, PlacesDetailsPojoClass> hashMap) {

        FavPlacesListViewAdpater adapter = new FavPlacesListViewAdpater(getContext(), R.layout
                .custom_info_window, hashMap);
        mFavoritesListView.setAdapter(adapter);
    }

}