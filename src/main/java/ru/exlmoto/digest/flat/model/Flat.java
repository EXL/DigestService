package ru.exlmoto.digest.flat.model;

public class Flat {
	private String rooms;
	private String squares;
	private String floor;
	private String address;
	private String price;
	private String phone;
	private String link;

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

	public String getSquares() {
		return squares;
	}

	public String getFloor() {
		return floor;
	}

	public String getAddress() {
		return address;
	}

	public String getPrice() {
		return price;
	}

	public String getPhone() {
		return phone;
	}

	public String getLink() {
		return link;
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
