package com.example.cardario_m2;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardario_m2.models.Dish;
import com.example.cardario_m2.models.Menu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
public class MainActivity extends AppCompatActivity {

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout container = findViewById(R.id.linearLayout);

        List<Dish> dishes = loadJSONFromAsset();

        if (dishes != null) {
            for (Dish dish : dishes) {
                View menuItemView = getLayoutInflater().inflate(R.layout.menu_item, container, false);

                // Encontrar os componentes do layout
                TextView nameTextView = menuItemView.findViewById(R.id.item_text);
                TextView priceTextView = menuItemView.findViewById(R.id.item_price);
                ImageView imageView = menuItemView.findViewById(R.id.item_image);

                // Definir o texto
                nameTextView.setText(dish.getName());
                priceTextView.setText(String.format("R$ %.2f", dish.getPrice()));

                // Carregar a imagem usando AsyncTask
                if (dish.getImage() != null && !dish.getImage().isEmpty()) {
                    new ThreadImageFile(imageView).execute(dish.getImage());
                } else {
                    imageView.setVisibility(View.GONE); // Esconder ImageView se não houver URL de imagem
                }

                // Adicionar o menuItemView ao layout
                container.addView(menuItemView);
            }
        }
    }

    private List<Dish> loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("cardapio.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {

            ex.printStackTrace();
            return null;
        }
        Gson gson = new Gson();
        Menu cardapio = gson.fromJson(json, Menu.class);
        if (cardapio != null) {
            return cardapio.getFoods();
        }
        return null;
    }

    private static class ThreadImageFile extends AsyncTask<String, Void, Bitmap> {
        private final ImageView imageView;

        public ThreadImageFile(ImageView imageView) {
            this.imageView = imageView;
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
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
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
