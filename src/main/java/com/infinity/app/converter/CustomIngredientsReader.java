package com.infinity.app.converter;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Map.Entry;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinity.app.converter.IngredientsCordinates.Vertices;

public class CustomIngredientsReader {

	private List<IngredientsCordinates> ingredients = new ArrayList<>();
	
	private List<String> finalList = new ArrayList<>();
	

	public List<String> readIngredientsFromImage() throws JsonProcessingException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		JsonFactory factory = new JsonFactory();
		factory.setCodec(mapper);
		JsonParser parser = factory.createParser(
				Files.newInputStream(Paths.get("/home/ravi/Downloads/response4.txt"), StandardOpenOption.READ));
		TreeNode treeNode = parser.readValueAsTree();

		if (treeNode.isObject()) {
			try {
				iterateObject((JsonNode) treeNode);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < ingredients.size(); i++) {
			IngredientsCordinates inc = ingredients.get(i);
			String ingredient = inc.getIngredient();
			if(ingredient.contains(",")) {
				String ing[] = ingredient.split(",");
				Arrays.stream(ing).forEach(it -> finalList.add(it));
				
			}else {
				finalList.add(ingredient);
			}
		  }
		//System.out.println(ingredients); 
		finalList.forEach(it -> System.out.println("Item "+it));
		return finalList;
	}

	private void iterateObject(JsonNode node) {

		if (node.isArray()) {
			iterateArray(node);
		}

		processingNode(node);
	}

	private void iterateArray(JsonNode node) {

		processingNode(node);

	}

	private void processingNode(JsonNode node) {
		if (node.isObject()) {
			Iterator<Entry<String, JsonNode>> entry = node.fields();
			if (!node.path("boundingPoly").isMissingNode()) {

				String description = node.path("description").asText();
				if (description.trim().length() > 2 
						&& !description.matches("^[0-9.]*$")) {

					// System.out.println(description);

					IngredientsCordinates cor = new IngredientsCordinates();
					cor.setIngredient(description);
					ingredients.add(cor);

				}

			}
			// System.out.println("Start");
			Stream<Entry<String, JsonNode>> stream = StreamSupport
					.stream(Spliterators.spliteratorUnknownSize(entry, Spliterator.ORDERED), false);
			stream.forEach(it -> {

				String key = it.getKey();
				JsonNode value = it.getValue();
				if (value.isArray())
					iterateArray(value);
				if (value.isObject())
					iterateObject(value);
			});

		} else if (node.isArray()) {
			Iterator<JsonNode> entry = node.elements();
			Stream<JsonNode> stream = StreamSupport
					.stream(Spliterators.spliteratorUnknownSize(entry, Spliterator.ORDERED), false);
			stream.forEach(it -> {

				if (it.isArray())
					iterateArray(it);
				if (it.isObject())
					iterateObject(it);
			});
		}

	}

}
