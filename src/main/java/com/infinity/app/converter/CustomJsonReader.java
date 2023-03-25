package com.infinity.app.converter;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinity.app.converter.IngredientsCordinates.Vertices;

public class CustomJsonReader {
	
	private List<IngredientsCordinates> ingredients = new ArrayList<>();
	private Map<Integer, BigDecimal> ingrediantValues = new HashMap<>();
	private int minServeIndex = 0;
	private int maxServeIndex = 0;
	
	
	public List<IngredientsCordinates>  readIngredientsFromImage() throws JsonProcessingException, IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		//JsonNode node = mapper.reader().readTree(Files.newInputStream(Paths.get("/home/ravi/Downloads/response.txt"), StandardOpenOption.READ));
		
		JsonFactory factory = new JsonFactory();
		factory.setCodec(mapper);
		JsonParser parser= factory.createParser(Files.newInputStream(Paths.get("/home/ravi/Downloads/response4.txt"), StandardOpenOption.READ));
		TreeNode treeNode = parser.readValueAsTree();
		
		if(treeNode.isObject()) {
			try {
				iterateObject((JsonNode) treeNode);
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
		  
		
		// prepare Nutrition table
		for(IngredientsCordinates in : ingredients) {
			List<Vertices> v = in.getVertices();
			BigDecimal quant = ingrediantValues.get(findY(ingrediantValues, v));
			if(quant !=  null) {
				in.setUnit(quant);
				//System.out.println(in.getIngredient()+ " = "+ in);
			}
		}
		
		for(IngredientsCordinates inc : ingredients) {
			List<Vertices> v = inc.getVertices();
			StringBuilder builder = new StringBuilder();
			builder.append(inc.getIngredient());
			Iterator<IngredientsCordinates> c = ingredients.iterator();
			while(c.hasNext() ) {
				IngredientsCordinates ing = c.next();
				if(v.get(1).getY() == ing.getVertices().get(0).getY()) {
					builder.append(" "+ing.getIngredient());
					ing = null;
				}
			}
			inc.setIngredient(builder.toString());
			
		}
		//System.out.println(ingredients); System.out.println(ingrediantValues);
		return ingredients;
	}
	
	private  int findY(Map<Integer, BigDecimal> ingrediantValues, List<Vertices> v ) {
		Optional<Integer> y = v.stream().filter(it -> ingrediantValues.get(it.getY()) !=null ).map(it -> it.getY()).findFirst();
		if(!y.isPresent()) {
			for(int i =0 ; i< 4 ;i++) {
				for(Vertices it  : v) {
					if(ingrediantValues.get(it.getY() + i) != null) {
						return it.getY()+i;
					}
				}
			}
		}
		return y.orElse(v.get(0).getY());
	}
	
	private  void iterateObject(JsonNode node) {
		
		if(node.isArray()) {
			iterateArray(node);
		}
		
		processingNode(node);
	}

	private  void iterateArray(JsonNode node) {
		
		processingNode(node);
		
	}
	
	private  void processingNode(JsonNode node) {
		if(node.isObject()) {
			Iterator<Entry<String, JsonNode>> entry = node.fields();
			if( !node.path("boundingPoly").isMissingNode() ) {
				
				
				//System.out.println("Node "+node);
				JsonNode vertices = node.path("boundingPoly").path("vertices");
				String description = node.path("description").asText();
				if(description.trim().length() > 0 && description.trim().length() < 15) {
					
					if(description.matches("^[0-9.]*$")) {
						Double number = node.path("description").asDouble();
						Iterator<JsonNode> verticesItr =  vertices.elements();
						while(verticesItr.hasNext()) {
							JsonNode vertice = verticesItr.next();
							if(minServeIndex > 0 && maxServeIndex > 0) {
								if(vertice.path("x").asInt() >= minServeIndex && vertice.path("x").asInt() <= maxServeIndex )
									ingrediantValues.putIfAbsent(vertice.path("y").asInt(), BigDecimal.valueOf(number));
							}else {
								//find for Per 100g
								int index = 0;
								for (IngredientsCordinates ing : ingredients) {
									if(ing.getIngredient().equals("Per")) {
										IngredientsCordinates in =  ingredients.get(index+1);
										if(in.getIngredient().equalsIgnoreCase("100g")) {
											Optional<Vertices> min = ing.getVertices().stream().min(Comparator.comparing(Vertices:: getX));
											Optional<Vertices> max = ing.getVertices().stream().max(Comparator.comparing(Vertices:: getX));
											if(min.isPresent()) {
												minServeIndex = min.get().getX() - 20;
											}
											if(max.isPresent()) {
												maxServeIndex = max.get().getX();
											}
											
											//System.out.println("Found per 100g "+minServeIndex +" : "+maxServeIndex);
											break;
										}
										index++;
									}
									
								}
							}
							
						}
					}else {
						//System.out.println(description);
						
						IngredientsCordinates cor = new IngredientsCordinates();
						cor.setIngredient(description);
						
						Iterator<JsonNode> verticesItr =  vertices.elements();
						List<Vertices> list = new ArrayList<>();
						while(verticesItr.hasNext()) {
							JsonNode vertice = verticesItr.next();
							Vertices v = new Vertices();
							v.setX(vertice.path("x").asInt());
							v.setY(vertice.path("y").asInt());
							list.add(v);
						}
						cor.setVertices(list);
						ingredients.add(cor);
						if(description.equals("Serve")) {
							Optional<Vertices> min = cor.getVertices().stream().min(Comparator.comparing(Vertices:: getX));
							Optional<Vertices> max = cor.getVertices().stream().max(Comparator.comparing(Vertices:: getX));
							if(min.isPresent()) {
								minServeIndex = min.get().getX() - 20;
							}
							if(max.isPresent()) {
								maxServeIndex = max.get().getX();
							}
							//System.out.println("Found "+minServeIndex +" : "+maxServeIndex);
						}
					}
					
					
				}
				
			}
				//System.out.println("Start");
				Stream<Entry<String, JsonNode>> stream =StreamSupport.stream(Spliterators.spliteratorUnknownSize(entry, Spliterator.ORDERED), false);
				stream.forEach(it -> {
					
					String key = it.getKey();
					JsonNode value = it.getValue(); 
					//System.out.println("Key "+key +" : "+value.asText());
					if(value.isArray()) iterateArray(value);
					if(value.isObject()) iterateObject(value);
				});
			
			
			
				
				
			
		}else if(node.isArray()) {
			Iterator<JsonNode>entry = node.elements();
			Stream<JsonNode> stream =StreamSupport.stream(Spliterators.spliteratorUnknownSize(entry, Spliterator.ORDERED), false);
			stream.forEach(it -> {
				
				if(it.isArray()) iterateArray(it);
				if(it.isObject()) iterateObject(it);
			});
		}
		
	}

}
