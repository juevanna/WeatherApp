package com.weatherapp.myweatherapp.service;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherapp.myweatherapp.model.CityInfo;

@Service
public class WeatherService {

    private static final String API_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";
    private static final String API_KEY = "QEZT4WN4759QYR45XE7A429BH";
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WeatherService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    
    public CityInfo forecastByCity(String city) {
        String url = String.format("%s%s/today?key=%s", API_URL, city, API_KEY);

        try {
            String response = restTemplate.getForObject(url, String.class);
            logger.debug("Raw API Response: {}", response); 

            if (response != null) {
                CityInfo cityInfo = new CityInfo();
                parseWeatherResponse(response, cityInfo);
                return cityInfo;
            } else {
                logger.error("Empty response received for city: {}", city);
                return null;
            }
        } catch (RestClientException e) {
            logger.error("Error fetching weather data for city: {}", city, e);
            return null;
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON response for city: {}", city, e);
            return null;
        }
    }

    
    private void parseWeatherResponse(String response, CityInfo cityInfo) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(response);
        JsonNode currentConditions = rootNode.path("currentConditions");

        
        cityInfo.setTemperature(getDoubleValue(currentConditions, "temp"));
        cityInfo.setFeelsLike(getDoubleValue(currentConditions, "feelslike"));
        cityInfo.setHumidity(getIntValue(currentConditions, "humidity"));
        cityInfo.setWeatherDescription(getStringValue(currentConditions, "conditions"));

        String sunrise = getStringValue(currentConditions, "sunrise");
        String sunset = getStringValue(currentConditions, "sunset");

        if (sunrise != null && sunset != null) {
            cityInfo.setDaylightHours(calculateDaylightHours(sunrise, sunset));
        }
    }

    
    private double getDoubleValue(JsonNode node, String fieldName) {
        return node.path(fieldName).isMissingNode() ? 0.0 : node.path(fieldName).asDouble();
    }


    private int getIntValue(JsonNode node, String fieldName) {
        return node.path(fieldName).isMissingNode() ? 0 : node.path(fieldName).asInt();
    }

    
    private String getStringValue(JsonNode node, String fieldName) {
        return node.path(fieldName).isMissingNode() ? null : node.path(fieldName).asText();
    }


    private double calculateDaylightHours(String sunrise, String sunset) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            LocalTime sunriseTime = LocalTime.parse(sunrise, formatter);
            LocalTime sunsetTime = LocalTime.parse(sunset, formatter);

            long durationInMinutes = Duration.between(sunriseTime, sunsetTime).toMinutes();
            return durationInMinutes / 60.0;
        } catch (DateTimeParseException e) {
            logger.error("Error parsing sunrise or sunset time", e);
            return 0.0;
        }
    }
}