/*
 * IpmiListener.java 
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

import com.veraxsystems.vxipmi.api.async.messages.IpmiError;
import com.veraxsystems.vxipmi.api.async.messages.IpmiResponse;
import com.veraxsystems.vxipmi.api.async.messages.IpmiResponseData;

/**
 * Interface for listeners of {@link IpmiAsyncConnector}.
 */
public interface IpmiListener {

	/**
	 * Notifies listener of action that occurred.
	 * 将发生的操作通知侦听器。
	 * @param response
	 *            - {@link IpmiResponse} being notified <li>
	 *            {@link IpmiResponseData} if answer for request arrived, <li>
	 *            {@link IpmiError} if delivery failed.
	 *            如果交付失败了。
	 */
	void notify(IpmiResponse response);
}
