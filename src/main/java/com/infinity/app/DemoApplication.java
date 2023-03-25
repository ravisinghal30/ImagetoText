package com.infinity.app;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.infinity.app.converter.CustomJsonReader;

public class DemoApplication {

	public static void main(String[] args) throws JsonProcessingException, IOException {
		
		System.out.println(new CustomJsonReader().readIngredientsFromImage());

	}

}
