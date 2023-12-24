package tn.example.homesecuritydevice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);

        String raspberryPiIpAddress = "raspberry_pi_ip";
        int port = 5555;

        new ImageDownloadTask().execute(raspberryPiIpAddress, Integer.toString(port));
    }

    private class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            String ipAddress = params[0];
            int port = Integer.parseInt(params[1]);
            Bitmap bitmap = null;

            try (Socket socket = new Socket(ipAddress, port)) {
                // Read image size
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                int imageSize = dataInputStream.readInt();

                // Read image data
                byte[] imageData = new byte[imageSize];
                dataInputStream.readFully(imageData);

                // Convert byte array to Bitmap
                bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageSize);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }
}
