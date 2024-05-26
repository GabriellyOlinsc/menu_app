package com.example.cardario_m2;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardario_m2.models.Dish;
import com.example.cardario_m2.models.Menu;
import com.google.gson.Gson;

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

        List<Dish> dishes;
        boolean isOnline = false;

        //TODO arrumanr a lógica de online  e offline, acho que pode estar ao contrário aqui
        if(!isOnline){
            dishes = loadJSONFromAsset();
            if (dishes != null) {
                saveMenu(dishes);
            }
        }else{
            dishes = databaseHelper.getAllMenuItems();
            Toast.makeText(this, "Offline mode: Displaying local data", Toast.LENGTH_LONG).show();
        }

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
                    new ThreadImageFile(imageView, dish.getName()).execute(dish.getImage());
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

    public void saveMenu(List<Dish> dishes){
        for(Dish dish: dishes){
            databaseHelper.insertDish(dish);
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
                e.printStackTrace();
            }
            return null;
        }

        //Salvando no sistema de arquivos

        //TODO: entender como vamos saber que realmente está salvando em um sistema de arquivos
        private void saveImageToLocalFile(Bitmap bitmap, String imageName) {
            try {
                FileOutputStream fos = imageView.getContext().openFileOutput(imageName + ".jpg", MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
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
