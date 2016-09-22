package com.example.bhavya.places.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v4.util.LruCache;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bhavya.places.R;
import com.example.bhavya.places.pojoclass.placesdetailspojoclass.Location;
import com.example.bhavya.places.pojoclass.placesdetailspojoclass.Opening_hours;
import com.example.bhavya.places.pojoclass.placesdetailspojoclass.Photos;
import com.example.bhavya.places.pojoclass.placesdetailspojoclass.PlacesDetailsPojoClass;
import com.example.bhavya.places.pojoclass.placesdetailspojoclass.Reviews;
import com.example.bhavya.places.pojoclass.userdetailspojoclass.UserDetails;
import com.example.bhavya.places.ui.adapter.PlaceDetailsAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.bhavya.places.ui.activity.SignUpActivity.PREF_FILE_NAME;
import static com.example.bhavya.places.ui.activity.SignUpActivity.PREF_LOGIN;


/**
 * Parses information from Google Places Details API to show more information about the clicked
 * item in Nearby Places listview
 */

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.imageview_place)
    ImageView mImageViewPlace;
    @BindView(R.id.button_fav)
    CheckBox mBtnFav;
    @BindView(R.id.btn_next_to_dialler)
    Button mDialler;
    @BindView(R.id.btn_next_to_website)
    Button mWebsite;

    @BindView(R.id.text_placename)
    TextView mTextViewPlaceName;
    @BindView(R.id.textWorkingHours)
    TextView mTextViewWorkingHours;

    @BindView(R.id.text_placeaddress)
    TextView mTextViewAddress;
    @BindView(R.id.text_contact)
    TextView mTextContact;
    @BindView(R.id.text_distance)
    TextView mTextDistance;
    @BindView(R.id.text_link)
    TextView mTextLink;
   /* @BindView(R.id.text_reviews)
    TextView mTextViewPersonReviews;*/

    @BindView(R.id.ratingbar_placeRating)
    RatingBar mPlaceRating;

    @BindView(R.id.listviewplacereviews)
    ListView mListviewReviews;

    private ProgressDialog mDialog;
    private StringBuilder json = new StringBuilder();
    private StringBuilder strURL;
    private String mPlaceid;
    private StringBuilder hours = new StringBuilder();
    private LruCache<String, Bitmap> mMemoryCache;
    private Bitmap bitmap;
    private PlacesDetailsPojoClass detailsPojoClassObject;
    private Gson gson = new Gson();
    private StringBuilder photoURL;
    private Double placeLatitude;
    private Double placeLongitude;
    private Double userLatitude;
    private Double userLongtiude;
    private String mDistance;
    private String contactNum;
    private String link;
    private String useremail;
    private int mItemNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }

        };
        setCustomActionBar();
        getDataFromPreferences();


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.getString(getString(R.string.place_id)) != null) {
                mPlaceid = bundle.getString(getString(R.string.place_id));
            }
            if (bundle.getString(getString(R.string.latitude)) != null) {
                String temp = bundle.getString(getString(R.string.latitude));
                userLatitude = Double.valueOf(temp);
            }
            if (bundle.getString(getString(R.string.longitude)) != null) {
                String temp = bundle.getString(getString(R.string.longitude));
                userLongtiude = Double.valueOf(temp);
            }
        }

        createUrl();
        new GetDetailsAPI().execute(strURL.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    View.OnClickListener Onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_next_to_dialler:
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(getString(R.string.tel) + contactNum));
                    startActivity(intent);
                    break;
                case R.id.btn_next_to_website:
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(link));
                    startActivity(i);
                    break;

            }
        }
    };


    /**
     * Customizing toolbar with no title
     */
    private void setCustomActionBar() {
        // to refer the toolbar
        setSupportActionBar(mToolbar);
        //to eliminate the app name from the toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Initilazes onclick properties
     */
    private void setOnClickProperties() {
        mDialler.setOnClickListener(Onclick);
        mWebsite.setOnClickListener(Onclick);
        //setting favourites
        boolean isFavourite = readState();
        mBtnFav.setChecked(isFavourite);
        mBtnFav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    saveDetailsIntoSD();
                } else {
                    removeDetailsFromSD();
                }
            }
        });
    }

    /**
     * to check if the place id exists in the hashmap
     * @return
     */
    private boolean readState(){
        try {
            File mainFolder = new File(Environment.getExternalStorageDirectory(), getString(R
                    .string.setrootpath));
            if (!mainFolder.exists()) {
                mainFolder.mkdir();
            }
            try {
                File file = new File(mainFolder, useremail + getResources().getString(R.string.textfile_extension));
                if (file.exists()) {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    LinkedHashMap hMap = (LinkedHashMap) objectInputStream.readObject();
                    objectInputStream.close();
                    boolean keyExists = hMap.containsKey(detailsPojoClassObject.getResult().getPlace_id());
                    if(keyExists){
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * To get the email id of the user for creating the file directory path
     */
    private void getDataFromPreferences() {
        SharedPreferences mSharedPreferences = this.getSharedPreferences(PREF_FILE_NAME,
                MODE_PRIVATE);
        String email = mSharedPreferences.getString(PREF_LOGIN, getString(R.string.rerrew));

        String obj = mSharedPreferences.getString(email, "");
        Gson gson = new Gson();
        UserDetails newuser = gson.fromJson(obj, UserDetails.class);
        useremail = newuser.getEmail();
    }


    /**
     * Saves the details of the place into SD card on favorating
     */
    private void saveDetailsIntoSD() {
        try {
            File mainFolder = new File(Environment.getExternalStorageDirectory(), getString(R
                    .string.setrootpath));
            if (!mainFolder.exists()) {
                mainFolder.mkdir();
            }
            try {
                File file = new File(mainFolder, useremail + getResources().getString(R.string
                        .textfile_extension));

                if (!file.exists()) {
                    file.createNewFile();
                    writeToFileFirstTime(file);
                }else{
                    readFromSDCard(file);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a hashmap that stores place id as key and placedetails object as its value.
     * Write hashmap into the file using ObjectOutputStream.
     * @param file
     */
    private void writeToFileFirstTime(File file) {
        LinkedHashMap<String, PlacesDetailsPojoClass> mapper = new LinkedHashMap<String, PlacesDetailsPojoClass>();
        mapper.put(detailsPojoClassObject.getResult().getPlace_id(), detailsPojoClassObject);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsolutePath());
            ObjectOutputStream objectOutputStream= new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(mapper);
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (StreamCorruptedException ex){
            ex.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the hash map already stored in the file and call writetofile method
     */
    private void readFromSDCard(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            LinkedHashMap myNewlyReadInMap = (LinkedHashMap) objectInputStream.readObject();
            writeToFile(file, myNewlyReadInMap);
            objectInputStream.close();
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
    }

    /**
     * Appends the new hashmap to already existing hash map
     * @param file
     * @param mynewmap
     */
    private void writeToFile(File file, Map mynewmap){
        mynewmap.put(detailsPojoClassObject.getResult().getPlace_id(), detailsPojoClassObject);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsolutePath());
            ObjectOutputStream objectOutputStream= new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(mynewmap);
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes the placeid key from the hashmap on unfavouriting
     */
    private void removeDetailsFromSD() {
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
                    LinkedHashMap hMap = (LinkedHashMap) objectInputStream.readObject();
                    boolean keyExists = hMap.containsKey(detailsPojoClassObject.getResult().getPlace_id());
                    if (keyExists) {
                        hMap.remove(detailsPojoClassObject.getResult().getPlace_id());
                    }
                    objectInputStream.close();
                    FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsolutePath());
                    ObjectOutputStream objectOutputStream= new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(hMap);
                    objectOutputStream.close();

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Create URL to make API call in nearbyplaceswithoutviewpager Fragment
     */
    private void createUrl() {
        strURL = new StringBuilder(getString(R.string.initialString));
        strURL.append(getString(R.string.setplaceid)).append(mPlaceid).append(getString(R.string
                .and)).append(getString(R.string.key)).append(getString(R.string.APIkey));
        Log.i(getString(R.string.log_api), strURL.toString());
    }


    /**
     * Calculates the distance between 2 points getting its latitude and longitude
     *
     * @param lat1 User location latitude
     * @param lon1 User location longitude
     * @param lat2 Destination latitude
     * @param lon2 Destination longitude
     * @return distance rounded to 2 decimal positions as a string
     */
    private String distance(double lat1, double lon1, double lat2, double lon2) {

        double distance = 0.0;
        android.location.Location currentlocation = new android.location.Location("Home");
        currentlocation.setLatitude(lat1);
        currentlocation.setLongitude(lon1);

        android.location.Location newlocation = new android.location.Location("Destination");
        newlocation.setLatitude(lat2);
        newlocation.setLongitude(lon2);

        distance = currentlocation.distanceTo(newlocation) / 1000;
        String result = String.format("%.2f", distance);
        return result;
    }

    /**
     * Adds image url and image into cache
     *
     * @param key
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * Gets the stored btimap from cache
     *
     * @param key
     * @return Bitmap image
     */
    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * Loads the bitmap from cache.
     * if bitmap is not cached, it displays a default image- image_profile else it displays
     * restaurant photo parsed from details api
     *
     * @param url
     */
    private void loadBitmap(String url) {
        final String imageKey = String.valueOf(url);
        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            mImageViewPlace.setImageBitmap(bitmap);
        } else {
            mImageViewPlace.setImageResource(R.drawable.image_profile);
            new GetPlacePhoto().execute(photoURL.toString());
        }
    }

    /**
     * Creates URL taken from photoreference parsed from Details API, makes URL valid and passes
     * it to AsyncTask
     */
    private void createPhotoURL() {
        Photos photo[];
        photo = detailsPojoClassObject.getResult().getPhotos();
        photoURL = new StringBuilder(getString(R.string.initalPhotoRef));
        photoURL.append(getString(R.string.maxwidth)).append(photo[0].getWidth());
        photoURL.append(getString(R.string.and)).append(getString(R.string.photoref)).append
                (photo[0].getPhoto_reference());
        photoURL.append(getString(R.string.and)).append(getString(R.string.key))
                .append(getString(R.string.APIkey));

        StringBuilder s = photoURL;
        Log.i(getString(R.string.log_api), photoURL.toString());
    }


    /**
     * To parse the Google Places Details API
     */
    class GetDetailsAPI extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(DetailsActivity.this);
            mDialog.setMessage(getResources().getString(R.string.loading_place_details));
            mDialog.show();
        }

        protected String doInBackground(String[] urls) {

            try {
                String s = urls[0];
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        json.append(line).append("\n");
                    }
                    bufferedReader.close();
                    String s1 = json.toString();
                    return json.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e(getString(R.string.error), e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                response = getString(R.string.error);
                Log.i(getString(R.string.log_info), response);
                return;
            }

            try {

                detailsPojoClassObject = gson.fromJson(json.toString(),
                        PlacesDetailsPojoClass.class);
                String placename = detailsPojoClassObject.getResult().getName();
                mTextViewPlaceName.setText(placename);
                String formattedAdd = detailsPojoClassObject.getResult().getFormatted_address();
                mTextViewAddress.setText(formattedAdd);

                Location location;
                location = detailsPojoClassObject.getResult().getGeometry().getLocation();

                mPlaceRating.setRating(Float.parseFloat(detailsPojoClassObject.getResult().getRating()));

                contactNum = detailsPojoClassObject.getResult().getFormatted_phone_number();
                mTextContact.setText(contactNum);
                link = detailsPojoClassObject.getResult().getWebsite();
                mTextLink.setText(link);

                Opening_hours openingHours;
                openingHours = detailsPojoClassObject.getResult().getOpening_hours();
                String workingHours[] = openingHours.getWeekday_text();
                for (int i = 0; i < workingHours.length; i++) {
                    hours.append(workingHours[i]);
                    hours.append("\n");
                }
                mTextViewWorkingHours.setText(hours);

                Reviews[] reviews;
                reviews = detailsPojoClassObject.getResult().getReviews();

                PlaceDetailsAdapter myAdapter = new PlaceDetailsAdapter(DetailsActivity.this,
                        R.layout.custom_place_reviews, reviews);
                mListviewReviews.setAdapter(myAdapter);

                try {
                    createPhotoURL();
                    loadBitmap(photoURL.toString());
                } catch (Exception e) {
                    Log.i(getString(R.string.urlerror), getString(R.string.error));
                }

                placeLatitude = location.getLat();
                placeLongitude = location.getLng();
                mDistance = distance(userLatitude, userLongtiude, placeLatitude, placeLongitude);
                mTextDistance.setText(getString(R.string.Distance).concat(String.valueOf
                        (mDistance)).concat(getString(R.string.km)));

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            mDialog.dismiss();
            setOnClickProperties();
        }
    }


    /**
     * To parse the photo of the restaurant from Details API
     */
    private class GetPlacePhoto extends AsyncTask<String, String, Bitmap> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
                addBitmapToMemoryCache(String.valueOf(args[0]), bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            if (image != null) {
                mImageViewPlace.setImageBitmap(image);
            }
        }

    }


}
