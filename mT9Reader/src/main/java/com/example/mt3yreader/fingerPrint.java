package com.example.mt3yreader;

public class fingerPrint {
	private int iWidth;
	private int iHeight;
	private byte[] imageBufferData = new byte[3073 * 11];
	private int[] imageBufferLength = {0};
	public int getWidth() {
		return iWidth;
	}
	public void setWidth(int iWidth) {
		this.iWidth = iWidth;
	}
	public int getHeight() {
		return iHeight;
	}
	public void setHeight(int iHeight) {
		this.iHeight = iHeight;
	}
	public byte[] getImageBufferData() {
		return imageBufferData;
	}
	public int[] getImageBufferLength() {
		return imageBufferLength;
	}
	
	

}
