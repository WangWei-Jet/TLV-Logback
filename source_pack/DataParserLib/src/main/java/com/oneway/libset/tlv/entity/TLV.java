package com.oneway.libset.tlv.entity;

public class TLV {

	// 标签域（Tag）包括一个或多个连续字节
	String tagStr;
	// 长度域（Len）包括一个或多个连续字节
	String lengthStr;
	// 值域（Value）定义数据对象的值。如果 L＝00，则值域不存在
	String valueStr;

	public String getTagStr() {
		return tagStr;
	}

	public void setTagStr(String tagStr) {
		this.tagStr = tagStr;
	}

	public String getLengthStr() {
		return lengthStr;
	}

	public void setLengthStr(String lengthStr) {
		this.lengthStr = lengthStr;
	}

	public String getValueStr() {
		return valueStr;
	}

	public void setValueStr(String valueStr) {
		this.valueStr = valueStr;
	}

}
