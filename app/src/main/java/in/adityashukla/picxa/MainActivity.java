package in.adityashukla.picxa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    ProgressBar progressBar;

    ImageView imageView;
    Button generateButton,saveButton;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editId);
        imageView = findViewById(R.id.imgId);
        progressBar = findViewById(R.id.progressId);
        generateButton = findViewById(R.id.genId);
        saveButton = findViewById(R.id.saveId);



        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String value = editText.getText().toString().trim();

                if(value.isEmpty()){
                    editText.setError("Please enter a valid text");
                    return;
                }

                callAPI(value);

            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              
            }
        });


    }

    private void callAPI(String value) {

        setProgressBar(true);

        JSONObject jsonObject = null;
        try {

            jsonObject = new JSONObject();
            jsonObject.put("prompt", value);
            jsonObject.put("size", "256x256");

        } catch (Exception e) {
            e.printStackTrace();

        }


        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/images/generations")
                .header("Authorization","Bearer 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")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(getApplicationContext(),"Failed to generate image",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {


                try {
                    JSONObject jsonObject1 = new JSONObject(response.body().string());
                    String imageUrl = jsonObject1.getJSONArray("data").getJSONObject(0).getString("url");

                    loadImg(imageUrl);

                    setProgressBar(false);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });



    }

    void setProgressBar(Boolean inProgress){
        runOnUiThread(()->
        {
            if(inProgress){
                progressBar.setVisibility(View.VISIBLE);
                generateButton.setVisibility(View.GONE);
                saveButton.setVisibility(View.VISIBLE);
            }else {
                progressBar.setVisibility(View.GONE);
                generateButton.setVisibility(View.VISIBLE);
            }
        });


    }

    private void loadImg(String url) {


        runOnUiThread(()->{
            Picasso.get().load(url).into(imageView);
        });






    }

}