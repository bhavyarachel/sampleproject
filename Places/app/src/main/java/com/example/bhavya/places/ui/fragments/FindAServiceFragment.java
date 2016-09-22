package com.example.bhavya.places.ui.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;


import com.example.bhavya.places.R;
import com.example.bhavya.places.interfaces.FindServiceAPIInterface;
import com.example.bhavya.places.pojoclass.nearbyplacespojoclass.NearbyPlacesPojoClass;
import com.example.bhavya.places.pojoclass.userdetailspojoclass.UserDetails;
import com.example.bhavya.places.retrofit.Findserviceretrofit;
import com.example.bhavya.places.ui.activity.DrawerActivity;
import com.example.bhavya.places.ui.activity.ServiceResultActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

import static android.content.Context.MODE_PRIVATE;
import static com.example.bhavya.places.ui.activity.SignUpActivity.PREF_FILE_NAME;
import static com.example.bhavya.places.ui.activity.SignUpActivity.PREF_LOGIN;


/**
 * A simple {@link Fragment} subclass.
 * This fragment allows user to select a place type from list view and type the required place
 * name in search box.
 * Displays the result in Service Result Activity as a list view
 */
public class FindAServiceFragment extends Fragment {

    @BindView(R.id.text_search)
    EditText mTextSearch;
    @BindView(R.id.image_searchicon)
    ImageButton mImageSearchIcon;

    private View view;
    private ListView listview;
    private String mValueFromList = null;
    private StringBuilder strURL;
    public Double latitude;
    public Double longitude;
    private String CurrentRadius;
    private SharedPreferences mSharedPreferences;
    private String mSearchBoxValue;
    private LatLng latLng;
    private String myKey;
    private String mylatlng;


    private FindAServiceFragment newInstance() {
        Bundle args = new Bundle();
        FindAServiceFragment fragment = new FindAServiceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_find_a_service, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        getDataFromPreferences();
        updateLocation();
        listview = (ListView) view.findViewById(R.id.searchViewResult);
        listview.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout
                .simple_list_item_1, getContext().getResources().getStringArray(R.array.types_of_services)));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                mValueFromList = getContext().getResources().getStringArray(R.array
                        .servicetypes)[position];
                checkForValidSearch();

            }
        });

        mImageSearchIcon.setOnClickListener(Onclick);



    }

    View.OnClickListener Onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.image_searchicon:
                    checkForValidSearch();
                    break;

            }
        }
    };


    /**
     * To read the text entered in the edit text of search box
     */
    private void getDataFromSearchBox(){
            mSearchBoxValue = mTextSearch.getText().toString();
    }

    /**
     * Call API if search is valid else alert the user with alert dialogs.
     */
    private void checkForValidSearch() {
        getDataFromSearchBox();
        if(mValueFromList !=null && !(mSearchBoxValue.equals(""))) {
            getAPIKey();
            makeLatLng();
            Intent intent = new Intent(getActivity(), ServiceResultActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.fs_placetype), mValueFromList);
            bundle.putString(getString(R.string.fs_placename),mSearchBoxValue);
            bundle.putString(getString(R.string.fs_Radius), CurrentRadius);
            bundle.putString(getString(R.string.fs_latitude), latitude.toString());
            bundle.putString(getString(R.string.fs_longitude), longitude.toString());
            bundle.putString(getString(R.string.fs_LatLngString), mylatlng);
            bundle.putString(getString(R.string.fs_APIKey), myKey);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);


        }else if(mSearchBoxValue.equals("") && mValueFromList ==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getString(R.string.invalid_search))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener
                            () {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }else if(mValueFromList==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getString(R.string.item_not_selected))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }else if(mSearchBoxValue.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getString(R.string.searchvalue_notfound))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    /**
     * To get the name and email id of the user to be displayed on the header layout of nav drawer
     */
    private void getDataFromPreferences() {
        mSharedPreferences = getContext().getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        String email = mSharedPreferences.getString(PREF_LOGIN, getString(R.string.rerrew));
        String obj = mSharedPreferences.getString(email, "");
        Gson gson = new Gson();
        UserDetails newuser = gson.fromJson(obj, UserDetails.class);
        CurrentRadius = newuser.getRadius();
    }


    /**
     * Updates the current location of the user
     */
    void updateLocation() {
        DrawerActivity drawerActivity = (DrawerActivity) getActivity();
        latLng = drawerActivity.getCurrentLatLong();
        if (latLng != null) {
            latitude = latLng.latitude;
            longitude = latLng.longitude;
        }

    }

    /**
     * Create a string concatinating latitude and longitude
     */
    private void makeLatLng(){
        mylatlng = latitude.toString().concat(",").concat(longitude.toString());
    }

    /**
     * To get the API key
     */
    private void getAPIKey(){
       myKey= getString(R.string.APIkey);
    }

}
