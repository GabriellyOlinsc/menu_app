package com.example.cardario_m2;


import android.os.Bundle;
import android.util.Log;
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
import java.util.List;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("gaby", "creating");

        LinearLayout container = findViewById(R.id.linearLayout);
        Log.i("gaby", "creating linear layout");

        List<Dish> dishes = loadJSONFromAsset();
        Log.i("gaby", "kaks" + dishes);

        if (dishes != null) {
            for (Dish dish : dishes) {
                TextView textView = new TextView(this);
                textView.setText(String.format("%s - R$ %.2f", dish.getName(), dish.getPrice()));
                textView.setTextSize(18);
                textView.setPadding(16, 16, 16, 16);

                // Definindo os parâmetros de layout para ocupar a largura total
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Largura igual ao contêiner
                        LinearLayout.LayoutParams.WRAP_CONTENT  // Altura ajustável ao conteúdo
                );
                params.setMargins(0, 0, 0, 16); // Margem inferior para espaçamento
                textView.setLayoutParams(params);

                // Estilizando o fundo do TextView como um retângulo
                textView.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
                ;

                container.addView(textView);
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
            List<Dish> dishes = cardapio.getFoods();
            // Log the result
            Log.d("gaby", "Loaded dishes: " + dishes.toString());
            return dishes;
        }
        Log.d("EMPTY", "Loaded dishes: " );
        return null;
    }
}
