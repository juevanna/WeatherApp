package com.weatherapp.myweatherapp.repository;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.weatherapp.myweatherapp.model.CityInfo;

@Repository
public class VisualcrossingRepository {

    private static final Logger logger = LoggerFactory.getLogger(VisualcrossingRepository.class);

    @Value("${weather.visualcrossing.url}")
    private String url;

    @Value("${weather.visualcrossing.key}")
    private String key;

    public CityInfo getByCity(String city) {
        String uri = url + "timeline/" + city + "?key=" + key;
        RestTemplate restTemplate = new RestTemplate();

        try {
            CityInfo cityInfo = restTemplate.getForObject(uri, CityInfo.class);

            if (cityInfo != null && cityInfo.getCurrentConditions() != null) {
                calculateDaylightHours(cityInfo);
                return cityInfo;
            } else {
                logger.warn("No current conditions found for city: {}", city);
                return null;
            }
        } catch (RestClientException e) {
            logger.error("Error fetching data from VisualCrossing for city: {}", city, e);
            return null;
        }
    }

    private void calculateDaylightHours(CityInfo cityInfo) {
        try {
            String sunrise = cityInfo.getCurrentConditions().getSunrise();
            String sunset = cityInfo.getCurrentConditions().getSunset();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime sunriseTime = LocalTime.parse(sunrise, formatter);
            LocalTime sunsetTime = LocalTime.parse(sunset, formatter);

            long daylightMinutes = Duration.between(sunriseTime, sunsetTime).toMinutes();
            double daylightHours = daylightMinutes / 60.0;

            cityInfo.setDaylightHours(daylightHours);

        } catch (DateTimeParseException e) {
            logger.error("Error parsing sunrise or sunset times for city: {}", cityInfo.getAddress(), e);
        } catch (NullPointerException e) {
            logger.error("Sunrise or sunset times are missing for city: {}", cityInfo.getAddress(), e);
        }
    }
}