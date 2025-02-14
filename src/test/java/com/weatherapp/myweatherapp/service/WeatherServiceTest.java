package com.weatherapp.myweatherapp.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.weatherapp.myweatherapp.model.CityInfo;

public class WeatherServiceTest {

    private WeatherService weatherService;

    @BeforeEach
    public void setUp() {

        weatherService = new WeatherService();
    }

    @Test
    public void testForecastByCity() {
        
        CityInfo expectedCityInfo = new CityInfo();
        expectedCityInfo.setDaylightHours(10.5);
        expectedCityInfo.setTemperature(20.0);

        CityInfo result = weatherService.forecastByCity("London");

    
        assertNotNull(result, "CityInfo should not be null");
        assertEquals(expectedCityInfo.getDaylightHours(), result.getDaylightHours(), 0.01);
        assertEquals(expectedCityInfo.getTemperature(), result.getTemperature(), 0.01);
    }
}