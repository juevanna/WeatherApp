package com.weatherapp.myweatherapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);

    @Autowired
    private WeatherService weatherService;

    
    @GetMapping("/forecast/{city}")
    public ResponseEntity<CityInfo> forecastByCity(@PathVariable("city") String city) {
        try {
            CityInfo ci = weatherService.forecastByCity(city);
            if (ci == null) {
                logger.warn("CityInfo not found for city: {}", city);
                return ResponseEntity.badRequest().body(null);
            }
            return ResponseEntity.ok(ci);
        } catch (Exception e) {
            logger.error("Error fetching forecast for city: {}", city, e);
            return ResponseEntity.status(500).body(null);
        }
    }

    
    @GetMapping("/compareDaylight/{city1}/{city2}")
    public ResponseEntity<String> compareDaylightHours(
            @PathVariable("city1") String city1,
            @PathVariable("city2") String city2) {
        try {
            CityInfo cityInfo1 = weatherService.forecastByCity(city1);
            CityInfo cityInfo2 = weatherService.forecastByCity(city2);

            if (cityInfo1 == null || cityInfo2 == null) {
                logger.warn("One or both cities could not be found. City1: {}, City2: {}", city1, city2);
                return ResponseEntity.badRequest().body("One or both cities could not be found.");
            }

            double daylightHours1 = cityInfo1.getDaylightHours();
            double daylightHours2 = cityInfo2.getDaylightHours();

            String result;
            if (daylightHours1 > daylightHours2) {
                result = city1 + " has the longest daylight hours.";
            } else if (daylightHours1 < daylightHours2) {
                result = city2 + " has the longest daylight hours.";
            } else {
                result = "Both cities have the same daylight hours.";
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error comparing daylight hours for cities: {} and {}", city1, city2, e);
            return ResponseEntity.status(500).body("An unexpected error occurred.");
        }
    }

    
    @GetMapping("/rainCheck/{city1}/{city2}")
    public ResponseEntity<String> rainCheck(
            @PathVariable("city1") String city1,
            @PathVariable("city2") String city2) {
        try {
            CityInfo cityInfo1 = weatherService.forecastByCity(city1);
            CityInfo cityInfo2 = weatherService.forecastByCity(city2);

            if (cityInfo1 == null || cityInfo2 == null) {
                logger.warn("One or both cities could not be found. City1: {}, City2: {}", city1, city2);
                return ResponseEntity.badRequest().body("One or both cities could not be found.");
            }

            String condition1 = cityInfo1.getCurrentConditions().getConditions().toLowerCase();
            String condition2 = cityInfo2.getCurrentConditions().getConditions().toLowerCase();

            boolean isRaining1 = condition1.contains("rain") || condition1.contains("shower");
            boolean isRaining2 = condition2.contains("rain") || condition2.contains("shower");

            if (isRaining1 && isRaining2) {
                return ResponseEntity.ok("It is raining in both " + city1 + " and " + city2);
            } else if (isRaining1) {
                return ResponseEntity.ok("It is raining in " + city1);
            } else if (isRaining2) {
                return ResponseEntity.ok("It is raining in " + city2);
            } else {
                return ResponseEntity.ok("It is not raining in either city.");
            }
        } catch (Exception e) {
            logger.error("Error checking rain conditions for cities: {} and {}", city1, city2, e);
            return ResponseEntity.status(500).body("An unexpected error occurred.");
        }
    }
}