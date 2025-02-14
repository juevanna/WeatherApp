package com.weatherapp.myweatherapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.weatherapp.myweatherapp.controller.WeatherController;
import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;

public class WeatherControllerTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCompareDaylightHours() {
        CityInfo city1 = new CityInfo();
        city1.setDaylightHours(10.5); 
        CityInfo city2 = new CityInfo();
        city2.setDaylightHours(8.0); 

        when(weatherService.forecastByCity("London")).thenReturn(city1);
        when(weatherService.forecastByCity("Paris")).thenReturn(city2);

        ResponseEntity<String> response = weatherController.compareDaylightHours("London", "Paris");
        assertEquals("London has the longest daylight hours.", response.getBody());
    }

    @Test
    public void testRainCheck() {
        CityInfo.CurrentConditions conditions1 = new CityInfo.CurrentConditions();
        conditions1.setConditions("rain");
        CityInfo city1 = new CityInfo();
        city1.setCurrentConditions(conditions1);

        CityInfo.CurrentConditions conditions2 = new CityInfo.CurrentConditions();
        conditions2.setConditions("clear");
        CityInfo city2 = new CityInfo();
        city2.setCurrentConditions(conditions2);

        when(weatherService.forecastByCity("London")).thenReturn(city1);
        when(weatherService.forecastByCity("Paris")).thenReturn(city2);

        ResponseEntity<String> response = weatherController.rainCheck("London", "Paris");
        assertEquals("It is raining in London", response.getBody());
    }
}