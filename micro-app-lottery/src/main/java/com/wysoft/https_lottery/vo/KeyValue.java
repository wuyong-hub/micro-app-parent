package com.wysoft.https_lottery.vo;

public class KeyValue<keyType, valueType>{
	private keyType key;
	private valueType value;

	public keyType getKey() {
		return key;
	}

	public void setKey(keyType key) {
		this.key = key;
	}

	public valueType getValue() {
		return value;
	}

	public void setValue(valueType value) {
		this.value = value;
	}
}
