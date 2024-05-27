package com.example.cardario_m2;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardario_m2.models.Dish;
import com.example.cardario_m2.models.Menu;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    private DBConnetion databaseHelper;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DBConnetion(this);
        databaseHelper.clearMenuItems();

        LinearLayout container = findViewById(R.id.linearLayout);

        // Verificação inicial de conectividade e carregamento de dados
       //   loadDataAndDisplay(container);

        // Registrar o BroadcastReceiver para mudanças de conectividade
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadDataAndDisplay(container);
            }
        }, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void loadDataAndDisplay(LinearLayout container) {
        List<Dish> dishes;
        if (isOnline()) {
            dishes = loadJSONFromAsset();
            if (dishes != null && !dishes.isEmpty()) {
                saveMenu(dishes);
                Toast.makeText(this, "Conectado à internet", Toast.LENGTH_SHORT).show();
            }
        } else {
            dishes = databaseHelper.getAllMenuItems();
            Toast.makeText(this, "Sem conexão com a internet", Toast.LENGTH_SHORT).show();
        }

        displayDishes(container, dishes);
    }

    @SuppressLint("DefaultLocale")
    private void displayDishes(LinearLayout container, List<Dish> dishes) {
        if (dishes != null) {
            container.removeAllViews(); // Limpar os dados existentes na interface
            for (Dish dish : dishes) {
                View menuItemView = getLayoutInflater().inflate(R.layout.menu_item, container, false);

                TextView nameTextView = menuItemView.findViewById(R.id.item_text);
                TextView priceTextView = menuItemView.findViewById(R.id.item_price);
                ImageView imageView = menuItemView.findViewById(R.id.item_image);

                nameTextView.setText(dish.getName());
                priceTextView.setText(isOnline() ? String.format("R$ %.2f", dish.getPrice()) : "A consultar");

                if (dish.getImage() != null && !dish.getImage().isEmpty()) {
                    if (isOnline()) {
                        new ThreadImageFile(imageView, dish.getName()).execute(dish.getImage());
                    } else {
                        loadImageFromLocal(imageView, dish.getName());
                    }
                } else {
                    imageView.setVisibility(View.GONE);
                }
                container.addView(menuItemView);
            }
        }
    }
    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private List<Dish> loadJSONFromAsset() {
        try {
            InputStream is = getAssets().open("cardapio.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            Menu menu = gson.fromJson(json, Menu.class);
            return menu != null ? menu.getFoods() : null;
        } catch (IOException ex) {
            Log.e("MainActivity", "Error reading JSON file", ex);
            return null;
        }
    }

    private void loadImageFromLocal(ImageView imageView, String imageName) {
        try {
            File filePath = getFileStreamPath(imageName + ".jpg");
            if (filePath.exists()) {
                FileInputStream fis = new FileInputStream(filePath);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                imageView.setImageBitmap(bitmap);
                fis.close();
            } else {
                imageView.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            Log.e("MainActivity", "Error loading image from local storage", e);
            imageView.setVisibility(View.GONE);
        }
    }

    private void saveMenu(List<Dish> dishes) {
        List<Dish> existingDishes = databaseHelper.getAllMenuItems();

        for (Dish dish : dishes) {
            boolean dishExists = false;
            for (Dish existingDish : existingDishes) {
                if (existingDish.getName().equals(dish.getName())) {
                    dishExists = true;
                    break;
                }
            }
            if (!dishExists) {
                databaseHelper.insertDish(dish);
                Log.d("MainActivity", "Novo item salvo: " + dish.getName());
            }
        }
    }

    private static class ThreadImageFile extends AsyncTask<String, Void, Bitmap> {
        private final ImageView imageView;
        private final String imageUrl;

        public ThreadImageFile(ImageView imageView, String imageUrl) {
            this.imageView = imageView;
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                String imageURL = urls[0];
                URL url = new URL(imageURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                InputStream is = con.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                is.close();

                saveImageToLocalFile(bitmap, imageUrl);

                return bitmap;
            } catch (Exception e) {
                Log.e("MainActivity", "Error downloading image", e);
            }
            return null;
        }

       private void saveImageToLocalFile(Bitmap bitmap, String imageName) {
            try {
                FileOutputStream fos = imageView.getContext().openFileOutput(imageName + ".jpg", MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            } catch (IOException e) {
                Log.e("MainActivity", "Error downloading image", e);
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setVisibility(View.GONE);
            }
        }
    }

}