/*
 * UdpMessage.java 
 * Created on 2011-08-17
 *
 * Copyright (c) Verax Systems 2011.
 * All rights reserved.
 *
 * This software is furnished under a license. Use, duplication,
 * disclosure and all other uses are restricted to the rights
 * specified in the written license agreement.
 */
package com.veraxsystems.vxipmi.transport;

import java.net.InetAddress;

/**
 * Container for UDP message.
 */
public class UdpMessage {
	/**
	 * Target port when sending message. Sender port when receiving
	 * message.
	 * 发送消息时的目标端口。接收消息时发送端端口。
	 */
	private int port;
	
	/**
	 * Target address when sending message. Sender address when receiving
	 * message.
	 * 发送消息时的目标地址。接收邮件时的发件人地址。
	 */
	private InetAddress address;

	private byte[] message;

	/**
	 * Target port when sending message. Sender port when receiving
	 * message.
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Target port when sending message. Sender port when receiving
	 * message.
	 * 发送消息时的目标端口。接收消息时发送端端口。
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Target address when sending message. Sender address when receiving
	 * message.
	 */
	public InetAddress getAddress() {
		return address;
	}

	/**
	 * Target address when sending message. Sender address when receiving
	 * message.
	 */
	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public byte[] getMessage() {
		return message;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}
}
