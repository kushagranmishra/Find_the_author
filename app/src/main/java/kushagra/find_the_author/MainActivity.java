package kushagra.find_the_author;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private Button btnSearch;
    private EditText editBookName;
    private TextView textBookName;
    private TextView textAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSearch = findViewById(R.id.btnSearch);
        editBookName = findViewById(R.id.editBookName);
        textBookName = findViewById(R.id.textBookName);
        textAuthor = findViewById(R.id.textAuthorName);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBooks();
            }
        });
    }

    public void searchBooks() {
        String queryString = editBookName.getText().toString();
        new FetchBook(textAuthor, textBookName).execute(queryString);
    }
    //    create a Asyntask class

    public class FetchBook extends AsyncTask<String, Void, String> {
        private WeakReference<TextView> textTitle;
        private WeakReference<TextView> textAuthor;

        // contructor
        public FetchBook(TextView texTitle, TextView textAuthor) {
            this.textTitle = new WeakReference<>(texTitle);
            this.textAuthor = new WeakReference<>(textAuthor);
        }

        @Override
        protected String doInBackground(String... query) {
            //    make a request to APi server

            return NetworkUtils.getBookInfo(query[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //    parse the JSON data
            try {
                // 1. raw string to JSONObject
                JSONObject jsonObject = new JSONObject(result);
                // 2. get the book data array
                JSONArray itemsArray = jsonObject.getJSONArray("items");

                // 3. loop variables
                int i = 0;
                String title = null;
                String authors = null;

                // 4. Look for results in array, exit when both title and author are found
                // or when all items are checked
                while (i < itemsArray.length() && (authors == null && title == null)) {
                    JSONObject book = itemsArray.getJSONObject(i);
                    JSONObject volume = book.getJSONObject("volumeInfo");
                    try {
                        title = volume.getString("title");
                        authors = volume.getString("authors");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i++;
                }
                if (title != null && authors != null) {
                    textTitle.get().setText(authors);
                    textAuthor.get().setText(title);
                } else {
                    textTitle.get().setText("Unknown Book Name");
                    textAuthor.get().setText("No results found");
                }

            } catch (Exception e) {
                e.printStackTrace();
                textTitle.get().setText("API error");
                textAuthor.get().setText("Please check Logcat");
            }
        }
//    create NetworkUtils log and create an URI
//    internet permision setup
    }
}