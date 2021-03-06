/*
 * IpmiCommandCoder.java 
 * Created on 2011-07-21
 *
 * Copyright (c) Verax Systems 2011.
 * All rights reserved.
 *
 * This software is furnished under a license. Use, duplication,
 * disclosure and all other uses are restricted to the rights
 * specified in the written license agreement.
 */
package com.veraxsystems.vxipmi.coding.commands;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import com.veraxsystems.vxipmi.coding.payload.IpmiPayload;
import com.veraxsystems.vxipmi.coding.payload.PlainMessage;
import com.veraxsystems.vxipmi.coding.payload.lan.IPMIException;
import com.veraxsystems.vxipmi.coding.payload.lan.IpmiLanResponse;
import com.veraxsystems.vxipmi.coding.payload.lan.NetworkFunction;
import com.veraxsystems.vxipmi.coding.protocol.AuthenticationType;
import com.veraxsystems.vxipmi.coding.protocol.IpmiMessage;
import com.veraxsystems.vxipmi.coding.protocol.Ipmiv15Message;
import com.veraxsystems.vxipmi.coding.protocol.Ipmiv20Message;
import com.veraxsystems.vxipmi.coding.protocol.PayloadType;
import com.veraxsystems.vxipmi.coding.protocol.encoder.Protocolv20Encoder;
import com.veraxsystems.vxipmi.coding.security.CipherSuite;
import com.veraxsystems.vxipmi.coding.security.SecurityConstants;

/**
 * A wrapper for IPMI command.
 * 
 * Parameterless constructors in classes derived from IpmiCommandCoder are meant
 * to be used for decoding. To avoid omitting setting an important parameter
 * when encoding message use parametered constructors rather than the
 * parameterless ones.
 * 从IpmiCommandCoder派生的类中的无参数构造函数用于解码。
 * 为了避免在编码消息时省略重要参数的设置，请使用带参数的构造函数，而不是无参数的构造函数。

 * 
 */
public abstract class IpmiCommandCoder {

	private IpmiVersion ipmiVersion;

	/**
	 * Authentication type used. Default == None;
	 */
	private AuthenticationType authenticationType;

	private CipherSuite cipherSuite;

	public void setIpmiVersion(IpmiVersion ipmiVersion) {
		this.ipmiVersion = ipmiVersion;
	}

	public IpmiVersion getIpmiVersion() {
		return ipmiVersion;
	}

	public void setAuthenticationType(AuthenticationType authenticationType) {
		this.authenticationType = authenticationType;
	}

	public AuthenticationType getAuthenticationType() {
		return authenticationType;
	}

	public void setCipherSuite(CipherSuite cipherSuite) {
		this.cipherSuite = cipherSuite;
	}

	public CipherSuite getCipherSuite() {
		return cipherSuite;
	}

	public IpmiCommandCoder() {
		setSessionParameters(IpmiVersion.V20, CipherSuite.getEmpty(),
				AuthenticationType.RMCPPlus);
	}

	public IpmiCommandCoder(IpmiVersion version, CipherSuite cipherSuite,
			AuthenticationType authenticationType) {
		setSessionParameters(version, cipherSuite, authenticationType);
	}

	/**
	 * Sets session parameters.
	 * 
	 * @param version
	 *            - IPMI version of the command.
	 * @param cipherSuite
	 *            - {@link CipherSuite} containing authentication,
	 *            confidentiality and integrity algorithms for this session.
	 *            包含此会话的身份验证、机密性和完整性算法。
	 * @param authenticationType
	 *            - Type of authentication used. Must be RMCPPlus for IPMI v2.0.
	 *            使用的身份验证类型。对于IPMI v2.0，必须是RMCPPlus。
	 */
	public void setSessionParameters(IpmiVersion version,
			CipherSuite cipherSuite, AuthenticationType authenticationType) {

		if (version == IpmiVersion.V20
				&& authenticationType != AuthenticationType.RMCPPlus) {
			throw new IllegalArgumentException(
					"Authentication Type must be RMCPPlus for IPMI v2.0 messages");
		}

		setIpmiVersion(version);
		setAuthenticationType(authenticationType);
		setCipherSuite(cipherSuite);
	}

	/**
	 * Prepares an IPMI request message containing class-specific command
	 * 准备包含特定于类的命令的IPMI请求消息
	 * 
	 * @param sequenceNumber
	 *            - A generated sequence number used for matching request and
	 *            response. If IPMI message is sent in a session, it is used as
	 *            a Session Sequence Number. For all IPMI messages,
	 *            sequenceNumber % 256 is used as a IPMI LAN Message sequence
	 *            number and as an IPMI payload message tag.
	 *            一个生成的序列号，用于匹配请求和响应。如果IPMI消息在会话中发送，
	 *            则将其用作会话序列号。对于所有IPMI消息，sequenceNumber % 256用
	 *            作IPMI LAN消息序列号和IPMI有效负载消息标记。

	 * @param sessionId
	 *            - ID of the managed system's session message is being sent in.
	 *            For sessionless commands should b set to 0.
	 *            正在发送托管系统的会话消息的ID。对于无会话命令，b应该设置为0。
	 * @return IPMI message
	 * @throws NoSuchAlgorithmException
	 *             - when authentication, confidentiality or integrity algorithm
	 *             fails.
	 * @throws InvalidKeyException
	 *             - when creating of the algorithm key fails
	 */
	public IpmiMessage encodeCommand(int sequenceNumber, int sessionId)
			throws NoSuchAlgorithmException, InvalidKeyException {
		if (getIpmiVersion() == IpmiVersion.V15) {
			Ipmiv15Message message = new Ipmiv15Message();

			message.setAuthenticationType(getAuthenticationType());

			message.setSessionID(sessionId);

			message.setSessionSequenceNumber(sequenceNumber);

			message.setPayload(preparePayload(sequenceNumber));

			return message;
		} else /* IPMI version 2.0 */{
			Ipmiv20Message message = new Ipmiv20Message(getCipherSuite()
					.getConfidentialityAlgorithm());

			message.setAuthenticationType(getAuthenticationType());

			message.setSessionID(sessionId);

			message.setSessionSequenceNumber(sequenceNumber);

			message.setPayloadType(PayloadType.Ipmi);

			message.setPayloadAuthenticated(getCipherSuite()
					.getIntegrityAlgorithm().getCode() != SecurityConstants.IA_NONE);

			message.setPayloadEncrypted(getCipherSuite()
					.getConfidentialityAlgorithm().getCode() != SecurityConstants.CA_NONE);

			message.setPayload(preparePayload(sequenceNumber));

			message.setAuthCode(getCipherSuite()
					.getIntegrityAlgorithm()
					.generateAuthCode(
							message.getIntegrityAlgorithmBase(new Protocolv20Encoder())));

			return message;
		}
	}

	/**
	 * Checks if given message contains response command specific for this
	 * class.
	 * 检查给定的消息是否包含特定于该类的响应命令。
	 * @param message
	 * @return True if message contains response command specific for this
	 *         class, false otherwise.
	 *         如果message包含特定于该类的响应命令，则为True，否则为false。
	 */
	public boolean isCommandResponse(IpmiMessage message) {
		if (message.getPayload() instanceof IpmiPayload) {
			if (message.getPayload() instanceof IpmiLanResponse) {
				return ((IpmiLanResponse) message.getPayload()).getCommand() == getCommandCode();
			} else  {
				return message.getPayload() instanceof PlainMessage;
			}
		} else {
			return false;
		}
	}

	/**
	 * Retrieves command code specific for command represented by this class
	 * 检索特定于该类所表示的命令的命令代码
	 * @return command code
	 */
	public abstract byte getCommandCode();

	/**
	 * Retrieves network function specific for command represented by this
	 * class.
	 * 检索特定于该类所表示的命令的网络函数。
	 * @return network function
	 * @see NetworkFunction
	 */
	public abstract NetworkFunction getNetworkFunction();

	/**
	 * Prepares {@link IpmiPayload} to be encoded. Called from
	 * {@link #encodeCommand(int, int)}
	 * 
	 * @param sequenceNumber
	 *            - sequenceNumber % 256 is used as an IPMI payload message tag
	 * @return IPMI payload
	 * @throws NoSuchAlgorithmException
	 *             - when authentication, confidentiality or integrity algorithm
	 *             fails.
	 * @throws InvalidKeyException
	 *             - when creating of the algorithm key fails
	 */
	protected abstract IpmiPayload preparePayload(int sequenceNumber)
			throws NoSuchAlgorithmException, InvalidKeyException;

	/**
	 * Retrieves command-specific response data from IPMI message
	 * 从IPMI消息检索特定于命令的响应数据
	 * @param message
	 *            - IPMI message
	 * @return response data
	 * @throws IllegalArgumentException
	 *             when message is not a response for class-specific command or
	 *             response has invalid length.
	 * @throws IPMIException
	 *             when response completion code isn't OK.
	 * @throws NoSuchAlgorithmException
	 *             when authentication, confidentiality or integrity algorithm
	 *             fails.
	 * @throws InvalidKeyException
	 *             when creating of the authentication algorithm key fails
	 */
	public abstract ResponseData getResponseData(IpmiMessage message)
			throws IllegalArgumentException, IPMIException,
			NoSuchAlgorithmException, InvalidKeyException;

	/**
	 * Used in several derived classes - converts {@link PrivilegeLevel} to
	 * byte.
	 * 在几个派生类中使用-将{@link特权}转换为字节。
	 * @param privilegeLevel
	 * @return privilegeLevel encoded as a byte due to {@link CommandsConstants}
	 */
	protected byte encodePrivilegeLevel(PrivilegeLevel privilegeLevel) {
		switch (privilegeLevel) {
		case MaximumAvailable:
			return CommandsConstants.AL_HIGHEST_AVAILABLE;
		case Callback:
			return CommandsConstants.AL_CALLBACK;
		case User:
			return CommandsConstants.AL_USER;
		case Operator:
			return CommandsConstants.AL_OPERATOR;
		case Administrator:
			return CommandsConstants.AL_ADMINISTRATOR;
		default:
			throw new IllegalArgumentException("Invalid privilege level");
		}
	}
}
