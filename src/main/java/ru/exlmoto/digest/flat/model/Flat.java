/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2020 EXL <exlmotodev@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
