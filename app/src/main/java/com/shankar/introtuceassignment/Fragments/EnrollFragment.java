package com.shankar.introtuceassignment.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.shankar.customtoast.Keyboard;
import com.shankar.customtoast.Snacky;
import com.shankar.customtoast.Toasty;
import com.shankar.introtuceassignment.R;
import com.shankar.introtuceassignment.model.UserModel;

import java.util.Calendar;
import java.util.List;

public class EnrollFragment extends Fragment {


    public EnrollFragment() {

    }

    View mView;
    TextInputEditText firstName, lastName, dateOfBirth, age, gender, country, state, homeTown, phoneNumber, telephoneNumber;
    DatabaseReference mUsers;
    NestedScrollView nestedScroll;
    ImageView addProfilePicture;
    public final int PICK_IMAGE = 1;
    final int REQUEST_EXTERNAL_STORAGE = 100;
    public String imagePath = "";
    private int mYear, mMonth, mDay;

    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_enroll, container, false);


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);


        initComponents();
        datOfBirth();
        return mView;
    }


    private void initComponents() {

        firstName = mView.findViewById(R.id.firstName);
        lastName = mView.findViewById(R.id.lastName);
        dateOfBirth = mView.findViewById(R.id.dateOfBirth);
        age = mView.findViewById(R.id.age);
        gender = mView.findViewById(R.id.gender);
        country = mView.findViewById(R.id.country);
        state = mView.findViewById(R.id.state);
        homeTown = mView.findViewById(R.id.homeTown);
        phoneNumber = mView.findViewById(R.id.phoneNumber);
        telephoneNumber = mView.findViewById(R.id.telephoneNumber);
        addProfilePicture = mView.findViewById(R.id.addProfilePicture);

        nestedScroll = mView.findViewById(R.id.nestedScroll);

        mView.findViewById(R.id.addUserButton).setOnClickListener( v -> saveDataToServer());
        addProfilePicture.setOnClickListener(v -> onImageClick());
    }

    private void saveDataToServer() {


        Keyboard.hideKeyboard(getActivity());
        progressDialog.show();
        String firstNameST = firstName.getText().toString();
        String lastNameST = lastName.getText().toString();
        String dateOfBirthST = dateOfBirth.getText().toString();
        String ageST = age.getText().toString();
        String genderST = gender.getText().toString();
        String countryST = country.getText().toString();
        String stateST = state.getText().toString();
        String homeTownST = homeTown.getText().toString();
        String phoneNumberST = phoneNumber.getText().toString();
        String telephoneNumberST = telephoneNumber.getText().toString();


        if (firstNameST.isEmpty()) {

            setError(firstName);

        } else if (lastNameST.isEmpty()) {

            setError(lastName);

        } else if (dateOfBirthST.isEmpty()) {

            setError(dateOfBirth);

        } else if(ageST.isEmpty()) {

            setError(age);

        } else if (genderST.isEmpty()) {

            setError(gender);

        } else if (countryST.isEmpty()) {

            setError(country);

        } else if (stateST.isEmpty()) {

            setError(state);

        } else if (homeTownST.isEmpty()) {

            setError(homeTown);

        } else if (phoneNumberST.isEmpty()) {

            setError(phoneNumber);

        } else if (telephoneNumberST.isEmpty()) {

            setError(telephoneNumber);

        }else  if(imagePath.isEmpty())
        {
            Toasty.infoToast(getActivity(),"Select a Image");
            progressDialog.dismiss();
            nestedScroll.scrollTo(0,0);
        }
        else
        {
            //Token is generated by Firebase and unique to device
            String token= FirebaseInstanceId.getInstance().getToken();
            mUsers = FirebaseDatabase.getInstance().getReference("Users").child(token);
            mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.child(phoneNumberST).exists())
                    {
                        phoneNumber.requestFocus();
                        progressDialog.dismiss();
                        Toasty.errorToast(getActivity(),"PhoneNumber Exists !");
                    }

                    else
                    {

                        UserModel userModel = new UserModel(firstNameST, lastNameST, dateOfBirthST, ageST, genderST, countryST, stateST, homeTownST, phoneNumberST, telephoneNumberST, imagePath, getPosition(snapshot.getChildrenCount()));
                        mUsers.child(phoneNumberST).setValue(userModel).addOnCompleteListener(aVoid -> {
                            progressDialog.dismiss();
                            new Handler().postDelayed(() -> Toasty.successToast(getActivity(),"Successful"), 500);
                            clearTexts();

                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private void setError(TextInputEditText editText)
    {
        editText.requestFocus();
        progressDialog.dismiss();
        Snacky.snackButton(getActivity(),editText.getHint().toString()+ " is Empty!","Okay", R.color.light_blue_500);
    }

    private void clearTexts() {
        firstName.setText("");
        lastName.setText("");
        dateOfBirth.setText("");
        age.setText("");
        gender.setText("");
        country.setText("");
        state.setText("");
        homeTown.setText("");
        phoneNumber.setText("");
        telephoneNumber.setText("");
        imagePath = "";
        addProfilePicture.setImageResource(R.drawable.ic_baseline_insert_photo_24);

        new Handler().postDelayed(() -> nestedScroll.scrollTo(0,0), 500);
    }



    private void datOfBirth() {

        //Date and Time Picker
        dateOfBirth.setOnClickListener(v -> {

            try {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                @SuppressLint("DefaultLocale") DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) (view, year, month, dayOfMonth) ->
                {
                    dateOfBirth.setText(String.format("%02d-%02d-%4d", dayOfMonth, month + 1, year));
                    //date.setText(String.format("%4d-%02d-%02d",year, month + 1, dayOfMonth));
                }, mYear, mMonth, mDay);

                datePickerDialog.show();

            } catch (Exception e) {
                Toast.makeText(getActivity(), "Something Went Error", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void onImageClick() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            Toasty.infoToast(getActivity(),"Please give Storage Permissions to open Images");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);

        } else {

            ImagePicker.create(this)
                    .limit(1)
                    .includeVideo(false)
                    .includeAnimation(false)
                    .start();

        }
    }

    public void ImagePickerWithoutDependency()
    {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);

        } else {

            if(!isPackageInstalled())
            {
                //Opens google photos
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                //intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                intent.setPackage("com.google.android.apps.photos");
                startActivityForResult(Intent.createChooser(intent, "Choose a Picture: "), PICK_IMAGE);
            }
            else
            {
//                Opens file manager
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Choose a Picture: "), PICK_IMAGE);


            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == 1) {
//            if (resultCode == Activity.RESULT_OK) {
//                if (data.getClipData() != null) {
//                    Log.i("TAGCNT","Image path : " +data.getClipData().getItemAt(0).getUri());
//                    imagePath = String.valueOf(data.getClipData().getItemAt(0).getUri());
//                    Glide.with(getActivity())
//                            .load(imagePath)
//                            .into(addProfilePicture);
//                }
//            }
//        }

        try {
            if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
                List<Image> images = ImagePicker.getImages(data);

                imagePath = String.valueOf(images.get(0).getUri());

                Glide.with(addProfilePicture)
                        .load(imagePath)
                        .into(addProfilePicture);
            }
        }

        catch (Exception e)
        {
            Log.i("TAG02","Exception : "+e.getLocalizedMessage());
        }




    }

    private boolean isPackageInstalled() {
        
        //Just Checking google photos app exists or not. 
        try {
            PackageManager packageManager = getActivity().getPackageManager();
            packageManager.getPackageInfo("com.google.android.apps.photos", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private String getPosition(long fCount)
    {

        String a = "0";
        String b = "00";
        String c = "000";
        String count = String.valueOf(fCount);
        if(count.length() == 1)
        {
            return c+count;
        }
        else if(count.length() == 2)
        {
            return b+count;

        }
        else if(count.length() == 3)
        {
            return a+count;

        }
        else
        {
            return count;
        }
    }

}