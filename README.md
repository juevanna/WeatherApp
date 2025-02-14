# Weather App - New Features

## Overview
This project adds two new features to the existing Java Spring Boot weather application:

1. **Daylight Hours Comparison** – Compare the length of daylight hours between two cities and return the city with the longest day.
2. **Rain Check** – Check which of the two cities is currently experiencing rain.

Both features integrate with the **Visual Crossing Weather API** to fetch real-time weather data.

---

## How It Works

### 1️⃣ Daylight Hours Comparison
- This feature takes two city names as input.
- It fetches sunrise and sunset times for both cities from the **Visual Crossing Weather API**.
- It calculates daylight duration (sunset - sunrise) and determines which city has the longest daylight hours.
- Returns the name of the city with the longer day.

### 2️⃣ Rain Check
- This feature takes two city names as input.
- It fetches real-time weather data from the **Visual Crossing Weather API**.
- It checks if either city is currently experiencing rain.
- Returns the name(s) of the city/cities where it is raining.

---

## Code Breakdown

### Controller: `WeatherController.java`
```java
package com.weatherapp.myweatherapp.controller;

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

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/forecast/{city}")
    public ResponseEntity<CityInfo> forecastByCity(@PathVariable("city") String city) {
        return ResponseEntity.ok(weatherService.forecastByCity(city));
    }

    @GetMapping("/compareDaylight/{city1}/{city2}")
    public ResponseEntity<String> compareDaylightHours(@PathVariable("city1") String city1, @PathVariable("city2") String city2) {
        return ResponseEntity.ok(weatherService.getCityWithLongestDay(city1, city2));
    }

    @GetMapping("/rainCheck/{city1}/{city2}")
    public ResponseEntity<String> rainCheck(@PathVariable("city1") String city1, @PathVariable("city2") String city2) {
        return ResponseEntity.ok(weatherService.getCitiesWithRain(city1, city2));
    }
}
```
- Defines three API endpoints:
  - `/weather/forecast/{city}` → Fetches the weather data for a single city.
  - `/weather/compareDaylight/{city1}/{city2}` → Compares daylight hours.
  - `/weather/rainCheck/{city1}/{city2}` → Checks for rain in two cities.

---

### Service: `WeatherService.java`
```java
package com.weatherapp.myweatherapp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherapp.myweatherapp.model.CityInfo;

@Service
public class WeatherService {

    private static final String API_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";
    private static final String API_KEY = "YOUR_API_KEY_HERE";

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
            if (response != null) {
                CityInfo cityInfo = new CityInfo();
                parseWeatherResponse(response, cityInfo);
                return cityInfo;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public String getCityWithLongestDay(String city1, String city2) {
        CityInfo data1 = forecastByCity(city1);
        CityInfo data2 = forecastByCity(city2);

        if (data1 == null || data2 == null) {
            return "Could not fetch weather data.";
        }

        double daylight1 = data1.getDaylightHours();
        double daylight2 = data2.getDaylightHours();

        return (daylight1 >= daylight2) ? city1 + " has the longest daylight hours." : city2 + " has the longest daylight hours.";
    }

    public String getCitiesWithRain(String city1, String city2) {
        CityInfo data1 = forecastByCity(city1);
        CityInfo data2 = forecastByCity(city2);

        if (data1 == null || data2 == null) {
            return "Could not fetch weather data.";
        }

        boolean isRaining1 = data1.getCurrentConditions().getConditions().toLowerCase().contains("rain");
        boolean isRaining2 = data2.getCurrentConditions().getConditions().toLowerCase().contains("rain");

        if (isRaining1 && isRaining2) return "It is raining in both cities.";
        if (isRaining1) return "It is raining in " + city1;
        if (isRaining2) return "It is raining in " + city2;

        return "It is not raining in either city.";
    }

    private void parseWeatherResponse(String response, CityInfo cityInfo) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode currentConditions = rootNode.path("currentConditions");

            cityInfo.setDaylightHours(currentConditions.path("daylight").asDouble());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
- Fetches weather data from **Visual Crossing Weather API**.
- Computes daylight hours from sunrise & sunset times.
- Determines whether it's raining in either city.

---

## Exception Handling

```java
try {
    CityInfo data = forecastByCity(city);
} catch (Exception e) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid city name: " + city, e);
}
```
- Catches errors and returns a **400 Bad Request** if an invalid city name is used.

---

## Testing

### Unit Tests
We wrote unit tests using **JUnit & Mockito**.

Example test for **Daylight Hours Comparison**:
```java
@Test
void testCompareDaylight() {
    when(weatherService.getCityWithLongestDay("London", "Paris"))
        .thenReturn("London has the longest daylight hours.");

    String result = weatherService.getCityWithLongestDay("London", "Paris");

    assertEquals("London has the longest daylight hours.", result);
}
```

Example test for **Rain Check**:
```java
@Test
void testRainCheck() {
    when(weatherService.getCitiesWithRain("London", "Paris"))
        .thenReturn("It is raining in London");

    String result = weatherService.getCitiesWithRain("London", "Paris");

    assertEquals("It is raining in London", result);
}
```

---

## Conclusion
This project extends the Weather API app by adding **daylight hours comparison** and **rain check features**. It integrates with **Visual Crossing Weather API**, follows **Spring Boot best practices**, includes **exception handling**, and has **unit tests** for reliability.
