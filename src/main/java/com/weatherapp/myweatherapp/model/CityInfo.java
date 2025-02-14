package com.weatherapp.myweatherapp.model;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CityInfo {

    @JsonProperty("address")
    private String address;

    @JsonProperty("description")
    private String description;

    @JsonProperty("currentConditions")
    private CurrentConditions currentConditions;

    @JsonProperty("days")
    private List<Days> days;

    private double daylightHours;
    private double temperature;
    private double feelsLike;
    private double humidity;
    private String weatherDescription;

   
    public CurrentConditions getCurrentConditions() {
        return currentConditions;
    }

    public void setCurrentConditions(CurrentConditions currentConditions) {
        this.currentConditions = currentConditions;
    }

    
    public double getDaylightHours() {
        if (currentConditions == null || currentConditions.getSunrise() == null || currentConditions.getSunset() == null) {
            return 0;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            LocalTime sunriseTime = LocalTime.parse(currentConditions.getSunrise(), formatter);
            LocalTime sunsetTime = LocalTime.parse(currentConditions.getSunset(), formatter);

            long durationInMinutes = Duration.between(sunriseTime, sunsetTime).toMinutes();
            daylightHours = durationInMinutes / 60.0;
        } catch (DateTimeParseException e) {
            daylightHours = 0;
        }

        return daylightHours;
    }

    public void setDaylightHours(double daylightHours) {
        this.daylightHours = daylightHours;
    }

    
    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public List<Days> getDays() {
        return days;
    }

    public static class CurrentConditions {
        @JsonProperty("temp")
        private String currentTemperature;

        @JsonProperty("sunrise")
        private String sunrise;

        @JsonProperty("sunset")
        private String sunset;

        @JsonProperty("feelslike")
        private String feelslike;

        @JsonProperty("humidity")
        private String humidity;

        @JsonProperty("conditions")
        private String conditions;

        public double getCurrentTemperature() {
            return parseTemperature(currentTemperature);
        }

        public double getFeelslike() {
            return parseTemperature(feelslike);
        }

        private double parseTemperature(String temperature) {
            try {
                return temperature != null ? Double.parseDouble(temperature) : 0.0;
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }

        public String getSunrise() {
            return sunrise;
        }

        public String getSunset() {
            return sunset;
        }

        public String getHumidity() {
            return humidity;
        }

        public String getConditions() {
            return conditions;
        }

        public void setConditions(String conditions) {
            this.conditions = conditions;
        }
    }

    public static class Days {
        @JsonProperty("datetime")
        private String date;

        @JsonProperty("temp")
        private String currentTemperature;

        @JsonProperty("tempmax")
        private String maxTemperature;

        @JsonProperty("tempmin")
        private String minTemperature;

        @JsonProperty("conditions")
        private String conditions;

        public double getCurrentTemperature() {
            return parseTemperature(currentTemperature);
        }

        public double getMaxTemperature() {
            return parseTemperature(maxTemperature);
        }

        public double getMinTemperature() {
            return parseTemperature(minTemperature);
        }

        private double parseTemperature(String temperature) {
            try {
                return temperature != null ? Double.parseDouble(temperature) : 0.0;
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }

        public String getDate() {
            return date;
        }

        public String getConditions() {
            return conditions;
        }
    }
}