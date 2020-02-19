package ru.exlmoto.digest.site.model;

public class PagerModel {
	private int current;
	private int all;
	private int startAux;
	private int endAux;

	public PagerModel(int current, int all, int startAux, int endAux) {
		this.current = current;
		this.all = all;
		this.startAux = startAux;
		this.endAux = endAux;
	}

	public PagerModel() {

	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public int getAll() {
		return all;
	}

	public void setAll(int all) {
		this.all = all;
	}

	public int getStartAux() {
		return startAux;
	}

	public void setStartAux(int startAux) {
		this.startAux = startAux;
	}

	public int getEndAux() {
		return endAux;
	}

	public void setEndAux(int endAux) {
		this.endAux = endAux;
	}
}
