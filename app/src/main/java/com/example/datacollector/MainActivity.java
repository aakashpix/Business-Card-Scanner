package com.example.datacollector;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Okio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText companyNameEditText;
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText addressEditText;
    private Button submitButton;
    private Button downloadButton;

    private OkHttpClient client;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        companyNameEditText = findViewById(R.id.company_name);
        nameEditText = findViewById(R.id.name);
        phoneEditText = findViewById(R.id.phone);
        emailEditText = findViewById(R.id.email);
        addressEditText = findViewById(R.id.address);
        submitButton = findViewById(R.id.submit_button);
        downloadButton = findViewById(R.id.download_button);

        client = new OkHttpClient();
        gson = new GsonBuilder().create();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String companyName = companyNameEditText.getText().toString();  
                String name = nameEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String address = addressEditText.getText().toString();

                Company company = new Company(companyName, name, phone, email, address);

                RequestBody body = RequestBody.create(MediaType.get("application/json"), gson.toJson(company));
                Request request = new Request.Builder()
                        .url("http://192.168.18.224:8080/api/companies/createCompany")//192.168.18.22
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Company created successfully!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadDatabase();
            }
        });
    }

    private void downloadDatabase() {
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/companies/getAllCompanies")//10.0.2.2
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    writeToFile(responseBody);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void writeToFile(String responseBody) {
        try {
            File file = new File(getExternalCacheDir(), "database.xlsx");
            Okio.buffer(Okio.sink(file)).writeUtf8(responseBody).close();
            Toast.makeText(this, "Database downloaded successfully!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

class Company { private String companyName; private String name; private String phone; private String email; private String address;

    public Company(String companyName, String name, String phone, String email, String address) {
        this.companyName = companyName;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    // Getters
    public String getCompanyName() {
        return companyName;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    // Setters
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}