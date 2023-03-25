package com.infinity.app;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.infinity.app.converter.CustomIngredientsReader;
import com.infinity.app.converter.CustomJsonReader;

public class DemoApplication {

	public static void main(String[] args) throws JsonProcessingException, IOException {
		
		//Upload file to Google cloud
		
		/*
		 * RestTemplate template = new RestTemplate();
		 * 
		 * 
		 * MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		 * body.add("file", Paths.get("home/ravi/Downloads/1.jpg"));
		 * 
		 * 
		 * HttpEntity entity = new HttpEntity(body);
		 * entity.getHeaders().add("Content-type", "image/jpg");
		 * entity.getHeaders().add("Authorization",
		 * "Bearer ya29.a0Ael9sCOl0e6Bz0lHx8siR_6nslYBQiGyqKnCc4zDJc8VynYXLcvGkM0FCli6Uy6Seb15y6VDbJ25sEmyCmUkgtAcYXEVIqMGrEIqzQYFfI2OLTIk_PiHpAalSbbJyZPQJlwWUDkeDLpB84ma0ed-AkEzcrYYaCgYKAaUSARMSFQF4udJhyEpOgYPscJWHdN69KFAphA0163"
		 * );
		 * 
		 * ResponseEntity<String> response = template.exchange(
		 * "https://storage.googleapis.com/upload/storage/v1/b/infinity-images/o?uploadType=media&name=1.jpg",
		 * HttpMethod.POST, entity, String.class);
		 * 
		 * System.out.println("file uploaded successfully");
		 */
		new CustomIngredientsReader().readIngredientsFromImage();

	}

}
