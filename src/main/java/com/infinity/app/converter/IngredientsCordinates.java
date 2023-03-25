package com.infinity.app.converter;

import java.math.BigDecimal;
import java.util.List;

public class IngredientsCordinates {
	
	private String ingredient;
	private List<Vertices> vertices;
	private BigDecimal unit;
	
	static class Vertices {
		private int x;
		private int y;
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		@Override
		public String toString() {
			return "Vertices [x=" + x + ", y=" + y + "]";
		}
		
	}
	
	
	public BigDecimal getUnit() {
		return unit;
	}
	public void setUnit(BigDecimal unit) {
		this.unit = unit;
	}
	public String getIngredient() {
		return ingredient;
	}
	public void setIngredient(String ingredient) {
		this.ingredient = ingredient;
	}
	
	
	public List<Vertices> getVertices() {
		return vertices;
	}
	public void setVertices(List<Vertices> vertices) {
		this.vertices = vertices;
	}
	@Override
	public String toString() {
		return "IngredientsCordinates [ingredient=" + ingredient + ", vertices=" + vertices + ", unit=" + unit + "]\n";
	}
	
	

}
