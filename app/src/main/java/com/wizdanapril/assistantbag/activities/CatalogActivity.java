package com.wizdanapril.assistantbag.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.wizdanapril.assistantbag.R;
import com.wizdanapril.assistantbag.adapters.CatalogAdapter;
import com.wizdanapril.assistantbag.models.Catalog;
import com.wizdanapril.assistantbag.utils.Constant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class CatalogActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Catalog> catalogList;
    private CatalogAdapter catalogAdapter;

    private DatabaseReference catalogReference;
    private StorageReference tagImageReference;

    private TextView emptyText;

    private static final String PHOTO_KEY = "tag_image";
    private File photo;
    private Bitmap resizedPhotoBitmap;
    byte[] bitmapData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        Nammu.init(this);
        if (savedInstanceState != null) {
            photo = (File) savedInstanceState.getSerializable(PHOTO_KEY);
        }

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Nammu.askForPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    //Nothing, this sample saves to Public gallery so it needs permission
                }

                @Override
                public void permissionRefused() {
                    finish();
                }
            });
        }

        EasyImage.configuration(this)
                .setImagesFolderName("Assistant Bag")
                .setCopyTakenPhotosToPublicGalleryAppFolder(false)
                .setCopyPickedImagesToPublicGalleryAppFolder(false)
                .setAllowMultiplePickInGallery(false);


        SharedPreferences preferences = this.getSharedPreferences("LoggedAccount", MODE_PRIVATE);
        String userAccount = preferences.getString("userAccount", "error");
        String deviceId = preferences.getString("deviceId", "error");
        catalogReference = FirebaseDatabase.getInstance().getReference(Constant.DATA)
                .child(userAccount).child(deviceId).child(Constant.CATALOG);
        tagImageReference = FirebaseStorage.getInstance().getReference().child("images").child("tag");

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(R.string.catalog);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        emptyText = (TextView) findViewById(R.id.tv_no_data);

        catalogList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.rv_tag);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        catalogAdapter = new CatalogAdapter(catalogList, this);
        recyclerView.setAdapter(catalogAdapter);

        updateList();
    }

    private void updateList() {

        catalogReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Catalog catalog = dataSnapshot.getValue(Catalog.class);
                catalogList.add(catalog);
//                Log.d("LIST", String.valueOf(catalogList.size()));
                catalogAdapter.notifyDataSetChanged();
                checkIfEmpty();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Catalog catalog = dataSnapshot.getValue(Catalog.class);
                int index = getItemIndex(catalog);
                catalogList.set(index, catalog);
                catalogAdapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Catalog catalog = dataSnapshot.getValue(Catalog.class);
                int index = getItemIndex(catalog);
                catalogList.remove(index);
                catalogAdapter.notifyItemRemoved(index);
                checkIfEmpty();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private int getItemIndex(Catalog catalog) {

        int index = -1;

        for (int i = 0; i < catalogList.size(); i++) {
            if (catalogList.get(i).id.equals(catalog.id)) {
                index = i;
                break;
            }
        }

        return index;
    }

    public void removeTag(int position) {
        catalogReference.child(catalogList.get(position).id).removeValue();
        Toast.makeText(this, getResources().getString(R.string.tag_deleted), Toast.LENGTH_LONG).show();
    }

    public void changeTag(int position, Drawable tagImage) {
        final Catalog catalog = catalogList.get(position);
        showUpdateDialog(catalog, tagImage);
    }

    private void showUpdateDialog(final Catalog catalog, Drawable tagImage) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_update, null);

        dialogBuilder.setView(dialogView);

        final ImageView imageInput = (ImageView) dialogView.findViewById(R.id.iv_item);
        final EditText nameInput = (EditText) dialogView.findViewById(R.id.et_name);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.bt_update);

        dialogBuilder.setTitle(getResources().getString(R.string.update_tag));
        imageInput.setImageDrawable(tagImage);
        nameInput.setText(catalog.name);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        imageInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.openChooserWithGallery(CatalogActivity.this, getString(R.string.pick_source), 0);
//                String photoPath = photo.getPath();
//                Bitmap photoBitmap = BitmapFactory.decodeFile(photoPath)
            }

        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = nameInput.getText().toString().trim();

                if (TextUtils.isEmpty(newName)) {
                    nameInput.setError(getResources().getString(R.string.error_empty_field));
                    return;
                }

                updateTag(catalog, newName, bitmapData);
                alertDialog.dismiss();
            }
        });
    }

    private void updateTag(final Catalog catalog, String newName, byte[] data) {
        catalog.name = newName;
//        Map<String, Object> catalogModelValue = catalog.toMap();
//        Map<String, Object> newUser = new HashMap<>();
//
//        newUser.put(catalog.id, catalogModelValue);
//
//        CATALOG.updateChildren(newUser);

        catalogReference.child(catalog.id).child("name").setValue(newName);

        if (data != null) {
            UploadTask uploadTask = tagImageReference.child(catalog.id).putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri imageUri = taskSnapshot.getDownloadUrl();
                    catalogReference.child(catalog.id).child("imageUri").setValue(imageUri.toString());
                    Log.d("IMAGE_URI", imageUri.toString() );
                }
            });
        }

        Toast.makeText(CatalogActivity.this, getResources().getString(R.string.tag_updated),
                Toast.LENGTH_LONG).show();
    }

    private void checkIfEmpty() {
        if (catalogList.size() == 0) {
            recyclerView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, intent);
//                this.overridePendingTransition(R.anim.exit_current, R.anim.exit_new);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.exit_current, R.anim.exit_new);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PHOTO_KEY, photo);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {
                onPhotosReturned(imageFiles);
                String photoPath = photo.getPath();
                Bitmap photoBitmap = BitmapFactory.decodeFile(photoPath);
                resizedPhotoBitmap = Bitmap.createScaledBitmap(photoBitmap, 128, 128, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resizedPhotoBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                bitmapData = baos.toByteArray();

//                LayoutInflater inflater = getLayoutInflater();
//                final View dialogView = inflater.inflate(R.layout.dialog_update, null);
//                ImageView tagImage = (ImageView) dialogView.findViewById(R.id.iv_item);
//                tagImage.setImageBitmap(resizedPhotoBitmap);

            }
            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(CatalogActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    private void onPhotosReturned(List<File> returnedPhotos) {
        photo = returnedPhotos.get(0);
//        photo.addAll(returnedPhotos);
//        imagesAdapter.notifyDataSetChanged();
//        recyclerView.scrollToPosition(photo.size() - 1);

    }

    @Override
    protected void onDestroy() {
        // Clear any configuration that was done!
        EasyImage.clearConfiguration(this);
        super.onDestroy();
    }
}
