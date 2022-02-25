package mobiledev.unb.ca.recyclerviewlab.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import mobiledev.unb.ca.recyclerviewlab.model.Course;

public class JsonUtils {
    private static final String CS_JSON_FILE = "CS.json";

    private static final String KEY_COURSES = "courses";
    private static final String KEY_COURSE_ID = "courseID";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";

    private ArrayList<Course> coursesArray;

    // Initializer to read our data source (JSON file) into an array of course objects
    public JsonUtils(Context context) {
        processJSON(context);
    }

    private void processJSON(Context context) {
        coursesArray = new ArrayList<>();

        try {
            // Create a JSON Object from file contents String
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(loadJSONFromAssets(context)));

            // Create a JSON Array from the JSON Object
            // This array is the "courses" array mentioned in the lab write-up
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_COURSES);

            for (int i = 0; i < jsonArray.length(); i++) {
                // TODO 1:
                //  Using the JSON array update coursesArray

                // Create a JSON Object from individual JSON Array element
                JSONObject individual = jsonArray.getJSONObject(i);

                // Get data from individual JSON Object
                String id = individual.getString(KEY_COURSE_ID);
                String name = individual.getString(KEY_NAME);
                String description = individual.getString(KEY_DESCRIPTION);


                //Builder B = new Course.Builder(id,name,description);
                // Add new Course to courses ArrayList;
                coursesArray.add((new Course.Builder(id,name,description).build()));


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadJSONFromAssets(Context context)  {
        // TODO 2:
        //  1. Obtain an instance of the AssetManager class from the referenced context
        //    (https://developer.android.com/reference/android/content/Context#getAssets())

        AssetManager manager = context.getAssets();

        //  2. Open the CS_JSON_FILE from the assets folder
        //     (https://developer.android.com/reference/android/content/res/AssetManager)


        // 3. Process the file using an input stream
        InputStream file = null;
        try {
            file = manager.open(CS_JSON_FILE);

            byte[] bytes = new byte[file.available()];

            file.read(bytes);
            return new String(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }


        return "";

    }

    // Getter method for courses ArrayList
    public ArrayList<Course> getCourses() {
        return coursesArray;
    }
}
