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

package ru.exlmoto.digest.util.udp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.exlmoto.digest.util.Answer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

import static ru.exlmoto.digest.util.Answer.Ok;
import static ru.exlmoto.digest.util.Answer.Error;

@Component
public class UdpSender {
	private final int BUFFER_SIZE = 1024;

	@Value("${rest.timeout-sec}")
	private int timeoutSec;

	public Answer<String> sendCommandAndGetAnswer(String hostname, int port, String command) {
		try {
			InetAddress inetAddress = InetAddress.getByName(hostname);

			DatagramSocket datagramSocket = new DatagramSocket();
			datagramSocket.setSoTimeout((timeoutSec / 3) * 1000);

			// https://en.wikipedia.org/wiki/Out-of-band_data
			byte[] bufferOut = ("||||" + command).getBytes();
			for (int i = 0; i < 4; ++i) {
				bufferOut[i] = (byte) 0xFF;
			}
			byte[] bufferIn = new byte[BUFFER_SIZE];

			DatagramPacket outgoingPacket = new DatagramPacket(bufferOut, bufferOut.length, inetAddress, port);
			DatagramPacket incomingPacket = new DatagramPacket(bufferIn, bufferIn.length);

			datagramSocket.send(outgoingPacket);
			datagramSocket.receive(incomingPacket);
			return Ok(new String(incomingPacket.getData()));
		} catch (IOException ioe) {
			return Error(ioe.getLocalizedMessage());
		}
	}
}
