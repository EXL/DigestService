package ru.exlmoto.digest.flat.model;

public class Flat {
	private String rooms;
	private String squares;
	private String floor;
	private String address;
	private String price;
	private String phone;
	private String link;

	public Flat() {
		// TODO: Drop this and setters????
	}

	public Flat(String rooms, String squares, String floor, String address, String price, String phone, String link) {
		this.rooms = rooms;
		this.squares = squares;
		this.floor = floor;
		this.address = address;
		this.price = price;
		this.phone = phone;
		this.link = link;
	}

	public String getRooms() {
		return rooms;
	}

	public void setRooms(String rooms) {
		this.rooms = rooms;
	}

	public String getSquares() {
		return squares;
	}

	public void setSquares(String squares) {
		this.squares = squares;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		return
			"Flat{" +
				"rooms=" + rooms +
				", squares=" + squares +
				", floor=" + floor +
				", address=" + address +
				", price=" + price +
				", phone=" + phone +
				", link=" + link +
				"}";
	}
}
