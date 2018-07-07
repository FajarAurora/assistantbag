package com.wizdanapril.assistantbag.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wizdanapril.assistantbag.R;
import com.wizdanapril.assistantbag.models.Catalog;
import com.wizdanapril.assistantbag.utils.Constant;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechRecognitionActivity extends AppCompatActivity {

	private TextToSpeech t1;
	private TextView txvResult;

//    private List<String> nameList;
    private StringBuilder stringBuilder;
    private String toBeSpoken;
    private String dayName;
    private DatabaseReference catalogReference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speech_recognition);
		txvResult = (TextView) findViewById(R.id.txvResult);

		t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR) {
					t1.setLanguage(new Locale("id","ID"));
				}
			}
		});

//		nameList = new ArrayList<>();
        stringBuilder = new StringBuilder();

        SharedPreferences preferences = this.getSharedPreferences("LoggedAccount", MODE_PRIVATE);
        String userAccount = preferences.getString("userAccount", "error");
        String deviceId = preferences.getString("deviceId", "error");
        catalogReference = FirebaseDatabase.getInstance().getReference(Constant.DATA)
                .child(userAccount).child(deviceId).child(Constant.CATALOG);

	}

	public void getSpeechInput(View view) {

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent, 10);
		} else {
			Toast.makeText(this, "Maaf, perangkat Anda tidak mendukung speech recognition", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case 10:
				if (resultCode == RESULT_OK && data != null) {
					final ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (result.get(0).equals("Senin")) {
                        dayName = "monday";
                        catalogReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot children : dataSnapshot.getChildren()) {
                                    Catalog catalog = children.getValue(Catalog.class);
                                    if (catalog != null && catalog.schedule.containsKey(dayName)) {
//                                        nameList.add(catalog.name);
                                        stringBuilder.append(catalog.name);
                                        stringBuilder.append(". ");

                                    }
                                }
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });

//
                        txvResult.setText(stringBuilder.toString());
                        toBeSpoken = stringBuilder.toString();
//                        startActivity(new Intent(SpeechRecognitionActivity.this,
//                                ScheduleActivity.class));
                        t1.speak(toBeSpoken, TextToSpeech.QUEUE_FLUSH, null);
                        stringBuilder.setLength(0);
                    } else if (result.get(0).equals("Selasa")) {
                        dayName = "tuesday";
                        catalogReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot children : dataSnapshot.getChildren()) {
                                    Catalog catalog = children.getValue(Catalog.class);
                                    if (catalog != null && catalog.schedule.containsKey(dayName)) {
//                                        nameList.add(catalog.name);
                                        stringBuilder.append(catalog.name);
                                        stringBuilder.append(". ");

                                    }
                                }
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });

//
                        txvResult.setText(stringBuilder.toString());
                        toBeSpoken = stringBuilder.toString();
//                        startActivity(new Intent(SpeechRecognitionActivity.this,
//                                ScheduleActivity.class));
                        t1.speak(toBeSpoken, TextToSpeech.QUEUE_FLUSH, null);
                        stringBuilder.setLength(0);
                    } else if (result.get(0).equals("Rabu")) {
                        dayName = "wednesday";
                        catalogReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot children : dataSnapshot.getChildren()) {
                                    Catalog catalog = children.getValue(Catalog.class);
                                    if (catalog != null && catalog.schedule.containsKey(dayName)) {
//                                        nameList.add(catalog.name);
                                        stringBuilder.append(catalog.name);
                                        stringBuilder.append(". ");

                                    }
                                }
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });

//
                        txvResult.setText(stringBuilder.toString());
                        toBeSpoken = stringBuilder.toString();
//                        startActivity(new Intent(SpeechRecognitionActivity.this,
//                                ScheduleActivity.class));
                        t1.speak(toBeSpoken, TextToSpeech.QUEUE_FLUSH, null);
                        stringBuilder.setLength(0);
                    } else if (result.get(0).equals("Kamis")) {
                        dayName = "thursday";
                        catalogReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot children : dataSnapshot.getChildren()) {
                                    Catalog catalog = children.getValue(Catalog.class);
                                    if (catalog != null && catalog.schedule.containsKey(dayName)) {
//                                        nameList.add(catalog.name);
                                        stringBuilder.append(catalog.name);
                                        stringBuilder.append(". ");

                                    }
                                }
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });

//
                        txvResult.setText(stringBuilder.toString());
                        toBeSpoken = stringBuilder.toString();
//                        startActivity(new Intent(SpeechRecognitionActivity.this,
//                                ScheduleActivity.class));
                        t1.speak(toBeSpoken, TextToSpeech.QUEUE_FLUSH, null);
                        stringBuilder.setLength(0);
                    } else if (result.get(0).equals("Jumat")) {
                        dayName = "friday";
                        catalogReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot children : dataSnapshot.getChildren()) {
                                    Catalog catalog = children.getValue(Catalog.class);
                                    if (catalog != null && catalog.schedule.containsKey(dayName)) {
//                                        nameList.add(catalog.name);
                                        stringBuilder.append(catalog.name);
                                        stringBuilder.append(". ");

                                    }
                                }
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });

//
                        txvResult.setText(stringBuilder.toString());
                        toBeSpoken = stringBuilder.toString();
//                        startActivity(new Intent(SpeechRecognitionActivity.this,
//                                ScheduleActivity.class));
                        t1.speak(toBeSpoken, TextToSpeech.QUEUE_FLUSH, null);
                        stringBuilder.setLength(0);
                    } else if (result.get(0).equals("Sabtu")) {
                        dayName = "saturday";
                        catalogReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot children : dataSnapshot.getChildren()) {
                                    Catalog catalog = children.getValue(Catalog.class);
                                    if (catalog != null && catalog.schedule.containsKey(dayName)) {
//                                        nameList.add(catalog.name);
                                        stringBuilder.append(catalog.name);
                                        stringBuilder.append(". ");

                                    }
                                }
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });

//
                        txvResult.setText(stringBuilder.toString());
                        toBeSpoken = stringBuilder.toString();
//                        startActivity(new Intent(SpeechRecognitionActivity.this,
//                                ScheduleActivity.class));
                        t1.speak(toBeSpoken, TextToSpeech.QUEUE_FLUSH, null);
                        stringBuilder.setLength(0);
                    } else if (result.get(0).equals("Minggu")) {
                        dayName = "sunday";
                        catalogReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot children : dataSnapshot.getChildren()) {
                                    Catalog catalog = children.getValue(Catalog.class);
                                    if (catalog != null && catalog.schedule.containsKey(dayName)) {
//                                        nameList.add(catalog.name);
                                        stringBuilder.append(catalog.name);
                                        stringBuilder.append(". ");

                                    }
                                }
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });

//
                        txvResult.setText(stringBuilder.toString());
                        toBeSpoken = stringBuilder.toString();
//                        startActivity(new Intent(SpeechRecognitionActivity.this,
//                                ScheduleActivity.class));
                        t1.speak(toBeSpoken, TextToSpeech.QUEUE_FLUSH, null);
                        stringBuilder.setLength(0);
                    } else {
                        t1.speak("Maaf, bukan nama hari", TextToSpeech.QUEUE_FLUSH, null);
                        Toast.makeText(this, "Not a day", Toast.LENGTH_SHORT).show();
                    }
                }
				break;
		}

	}

//	public void onPause(){
//		if(t1 !=null){
//			t1.stop();
//			t1.shutdown();
//		}
//		super.onPause();
//	}
}