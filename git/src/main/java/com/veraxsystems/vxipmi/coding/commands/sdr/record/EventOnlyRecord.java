/*
 * EventOnlyRecord.java 
 * Created on 2011-08-04
 *
 * Copyright (c) Verax Systems 2011.
 * All rights reserved.
 *
 * This software is furnished under a license. Use, duplication,
 * disclosure and all other uses are restricted to the rights
 * specified in the written license agreement.
 */
package com.veraxsystems.vxipmi.coding.commands.sdr.record;

import com.veraxsystems.vxipmi.common.TypeConverter;

/**
 * This record provides a mechanism to associate FRU and Entity information with
 * a physical or logical sensor that generates events, but cannot otherwise be
 * accessed. This is typical of software-generated events, such as events
 * generated by BIOS.
 * 该记录提供了一种机制，可以将FRU和实体信息与生成事件的物理或逻辑传感器关联起来，
 * 但不能通过其他方式访问该传感器。
 * 这是典型的软件生成事件，例如BIOS生成的事件。
 */
public class EventOnlyRecord extends SensorRecord {

	@Override
	public String toString() {
		return "EventOnlyRecord [sensorOwnerId=" + sensorOwnerId + ", addressType=" + addressType + ", channelNumber="
				+ channelNumber + ", sensorOwnerLun=" + sensorOwnerLun + ", sensorNumber=" + sensorNumber
				+ ", entityId=" + entityId + ", entityPhysical=" + entityPhysical + ", entityInstanceNumber="
				+ entityInstanceNumber + ", sensorType=" + sensorType + ", eventReadingType=" + eventReadingType
				+ ", sensorDirection=" + sensorDirection + ", name=" + name + ", idInstanceModifierType="
				+ idInstanceModifierType + ", shareCount=" + shareCount + ", entityInstanceIncrements="
				+ entityInstanceIncrements + ", idInstanceModifierOffset=" + idInstanceModifierOffset + "]";
	}


	private byte sensorOwnerId;

	private AddressType addressType;

	private byte channelNumber;

	private byte sensorOwnerLun;

	private byte sensorNumber;

	private EntityId entityId;

	/**
	 * Entity is physical if true, logical otherwise.
	 */
	private boolean entityPhysical;

	private byte entityInstanceNumber;

	private SensorType sensorType;
	
	private int eventReadingType;
	
	private SensorDirection sensorDirection;

	private String name;

	/**
	 * The instance modifier is a character(s) that software can append to the
	 * end of the ID String. This field selects whether the appended
	 * character(s) will be numeric or alpha.
	 * 实例修饰符是软件可以附加到ID字符串末尾的字符。这个字段选择附加的字符是数字还是字母。
	 */
	private InstanceModifierType idInstanceModifierType;

	/**
	 * Sensor numbers sharing this record are sequential starting with the
	 * sensor number specified by the Sensor Number field for this record.
	 * 共享此记录的传感器编号是顺序的，从该记录的传感器编号字段指定的传感器编号开始。
	 */
	private int shareCount;

	private boolean entityInstanceIncrements;

	/**
	 * Suppose sensor ID is 'Temp' for 'Temperature Sensor', share count = 3, ID
	 * string instance modifier = numeric, instance modifier offset = 5 - then
	 * the sensors could be identified as: Temp 5, Temp 6, Temp 7 <br>
	 * If the modifier = alpha, offset=0 corresponds to 'A', offset=25
	 * corresponds to 'Z', and offset = 26 corresponds to 'AA', thus, for
	 * offset=26 the sensors could be identified as: Temp AA, Temp AB, Temp AC
	 * 假设传感器ID是“临时”“温度传感器”,分享数= 3,修饰符=数字ID字符串实例,实例修改器抵消= 5,
	 * 那么传感器可以确定为:临时5,临时6日临时7 < br >如果修饰符=α,抵消= 0对应于“A”,
	 * 抵消= 25对应于“Z”,和抵消= 26对应于“AA”,因此,抵消= 26传感器可以确定为
	 * :临时AA,临时AB,临时交流
	 */
	private int idInstanceModifierOffset;

	
	@Override
	protected void populateTypeSpecficValues(byte[] recordData,
			SensorRecord record) {
		
		setSensorOwnerId(TypeConverter.intToByte((TypeConverter
				.byteToInt(recordData[5]) & 0xfe) >> 1));

		setAddressType(AddressType.parseInt(TypeConverter
				.byteToInt(recordData[5]) & 0x01));

		setChannelNumber(TypeConverter.intToByte((TypeConverter
				.byteToInt(recordData[6]) & 0xf0) >> 4));

		setSensorOwnerLun(TypeConverter.intToByte(TypeConverter
				.byteToInt(recordData[6]) & 0x3));

		setSensorNumber(recordData[7]);

		setEntityId(EntityId.parseInt(TypeConverter.byteToInt(recordData[8])));

		setEntityPhysical((TypeConverter.byteToInt(recordData[9]) & 0x80) == 0);

		setEntityInstanceNumber(TypeConverter.intToByte(TypeConverter
				.byteToInt(recordData[9]) & 0x7f));

		setSensorType(SensorType.parseInt(TypeConverter
				.byteToInt(recordData[10])));
		
		setEventReadingType(TypeConverter.byteToInt(recordData[11]));

		setSensorDirection(SensorDirection.parseInt((TypeConverter
				.byteToInt(recordData[12]) & 0xc0) >> 6));

		setIdInstanceModifierType(InstanceModifierType.parseInt((TypeConverter
				.byteToInt(recordData[12]) & 0x30) >> 4));
		
		setShareCount(TypeConverter.byteToInt(recordData[12]) & 0xf);
		
		setEntityInstanceIncrements((TypeConverter.byteToInt(recordData[13]) & 0x80) != 0);
		
		setIdInstanceModifierOffset(TypeConverter.byteToInt(recordData[13]) & 0x7f);
		
		byte[] name = new byte[recordData.length - 17];
		
		System.arraycopy(recordData, 17, name, 0, name.length);
		
		setName(decodeName(recordData[16], name));

	}


	public byte getSensorOwnerId() {
		return sensorOwnerId;
	}


	public void setSensorOwnerId(byte sensorOwnerId) {
		this.sensorOwnerId = sensorOwnerId;
	}


	public AddressType getAddressType() {
		return addressType;
	}


	public void setAddressType(AddressType addressType) {
		this.addressType = addressType;
	}


	public byte getChannelNumber() {
		return channelNumber;
	}


	public void setChannelNumber(byte channelNumber) {
		this.channelNumber = channelNumber;
	}


	public byte getSensorOwnerLun() {
		return sensorOwnerLun;
	}


	public void setSensorOwnerLun(byte sensorOwnerLun) {
		this.sensorOwnerLun = sensorOwnerLun;
	}


	public byte getSensorNumber() {
		return sensorNumber;
	}


	public void setSensorNumber(byte sensorNumber) {
		this.sensorNumber = sensorNumber;
	}


	public EntityId getEntityId() {
		return entityId;
	}


	public void setEntityId(EntityId entityId) {
		this.entityId = entityId;
	}


	public boolean isEntityPhysical() {
		return entityPhysical;
	}


	public void setEntityPhysical(boolean entityPhysical) {
		this.entityPhysical = entityPhysical;
	}


	public byte getEntityInstanceNumber() {
		return entityInstanceNumber;
	}


	public void setEntityInstanceNumber(byte entityInstanceNumber) {
		this.entityInstanceNumber = entityInstanceNumber;
	}


	public SensorType getSensorType() {
		return sensorType;
	}


	public void setSensorType(SensorType sensorType) {
		this.sensorType = sensorType;
	}


	public int getEventReadingType() {
		return eventReadingType;
	}


	public void setEventReadingType(int eventReadingType) {
		this.eventReadingType = eventReadingType;
	}


	public SensorDirection getSensorDirection() {
		return sensorDirection;
	}


	public void setSensorDirection(SensorDirection sensorDirection) {
		this.sensorDirection = sensorDirection;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public InstanceModifierType getIdInstanceModifierType() {
		return idInstanceModifierType;
	}


	public void setIdInstanceModifierType(
			InstanceModifierType idInstanceModifierType) {
		this.idInstanceModifierType = idInstanceModifierType;
	}


	public int getShareCount() {
		return shareCount;
	}


	public void setShareCount(int shareCount) {
		this.shareCount = shareCount;
	}


	public boolean isEntityInstanceIncrements() {
		return entityInstanceIncrements;
	}


	public void setEntityInstanceIncrements(boolean entityInstanceIncrements) {
		this.entityInstanceIncrements = entityInstanceIncrements;
	}


	public int getIdInstanceModifierOffset() {
		return idInstanceModifierOffset;
	}


	public void setIdInstanceModifierOffset(int idInstanceModifierOffset) {
		this.idInstanceModifierOffset = idInstanceModifierOffset;
	}

}
