package com.schytd.discount.enties;

public class Level {
	private String score;
	private String level;
	public Level(){}
	public Level(String score, String level) {
		this.score = score;
		this.level = level;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
}
