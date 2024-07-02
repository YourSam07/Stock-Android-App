package com.example.webtech4;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class NewsModel {
    private String source;
    private Long timestamp;
    private String title;
    private String description;
    private String image;
    private String url;

    public NewsModel(String source, Long timestamp, String title, String description, String image, String url) {
        this.source = source;
        this.timestamp = timestamp;
        this.title = title;
        this.description = description;
        this.image = image;
        this.url = url;
    }

    public String getSource() {
        return source;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }

    public String getHours() {
        long unixTimestamp = this.timestamp;

        // Convert Unix timestamp to milliseconds
        long timestampMillis = unixTimestamp * 1000;

        // Get the current time in milliseconds
        long currentTimeMillis = System.currentTimeMillis();

        // Calculate the difference in milliseconds
        long diffMillis = currentTimeMillis - timestampMillis;

        // Calculate the difference in hours
        long diffHours = TimeUnit.MILLISECONDS.toHours(diffMillis);

        // Format the difference as "x hours ago"
        if (diffHours < 24){
            return diffHours + " hours ago";
        } else if (diffHours < 48){
            return "Yesterday";
        } else if (diffHours < 168 ) {
            return "This Week";
        } else if (diffHours < 720) {
            return "This Month";
        } else {
            return "This Year";
        }
    }

    public String formatDate() {
        // Convert Unix timestamp to milliseconds
        long timestampMillis = this.timestamp * 1000;

        // Create a Date object from the timestamp
        Date date = new Date(timestampMillis);

        // Define the desired date format
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

        // Set the time zone if needed (optional)
        sdf.setTimeZone(TimeZone.getDefault());

        // Format the date and return as a string
        return sdf.format(date);
    }
}
