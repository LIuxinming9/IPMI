/*
 * ConnectionHandle.java 
 * Created on 2011-09-07
 *
 * Copyright (c) Verax Systems 2011.
 * All rights reserved.
 *
 * This software is furnished under a license. Use, duplication,
 * disclosure and all other uses are restricted to the rights
 * specified in the written license agreement.
 */
package com.veraxsystems.vxipmi.api.async;

import com.veraxsystems.vxipmi.coding.commands.PrivilegeLevel;
import com.veraxsystems.vxipmi.coding.security.CipherSuite;
import com.veraxsystems.vxipmi.connection.Connection;

/**
 * Handle to the {@link Connection}
 * {@link连接}句柄
 */
public class ConnectionHandle {
	@Override
	public String toString() {
		return "ConnectionHandle [handle=" + handle + ", cipherSuite=" + cipherSuite + ", privilegeLevel="
				+ privilegeLevel + "]";
	}

	private int handle;//处理
	private CipherSuite cipherSuite;//密码续
	private PrivilegeLevel privilegeLevel;//特权水平
	
	public CipherSuite getCipherSuite() {
		return cipherSuite;
	}

	public void setCipherSuite(CipherSuite cipherSuite) {
		this.cipherSuite = cipherSuite;
	}

	public PrivilegeLevel getPrivilegeLevel() {
		return privilegeLevel;
	}

	public void setPrivilegeLevel(PrivilegeLevel privilegeLevel) {
		this.privilegeLevel = privilegeLevel;
	}

	public int getHandle() {
		return handle;
	}

	public ConnectionHandle(int handle) {
		this.handle = handle;
	}
}
