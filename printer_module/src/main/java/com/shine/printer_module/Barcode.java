package com.shine.printer_module;

import android.util.Log;

import java.io.UnsupportedEncodingException;

public class Barcode {
	private static final String TAG = "Barcode";
	private byte barcodeType;
	private int param1;
	private int param2;
	private int param3;
	private String content;
	private String charsetName = "gbk";

	public Barcode(byte barcodeType) {
		this.barcodeType = barcodeType;
	}

	public Barcode(byte barcodeType, int param1, int param2, int param3) {
		this.barcodeType = barcodeType;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
	}

	public Barcode(byte barcodeType, int param1, int param2, int param3,
				   String content) {
		this.barcodeType = barcodeType;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
		this.content = content;
	}

	// 条码类型type 为一维条码时，另外三个参数表示：
	// param1：条码横向宽度 2<=n<=6 ，默认为2 。
	// param2：条码高度 1<=n<=255，默认 162
	// param3：条码注释位置，0 不打印，1 上方，2 下方，3 上下方均有
	//
	// 条码类型type 为二维条码时，另外三个参数表示不同的意思：
	// 1. PDF4 17
	// param1：表示每行字符数，1<=n<=30 。
	// param2：表示纠错等级，0<=n<=8。
	// param3：表示纵向放大倍数。
	// 2. DATA MATRIX
	// param1：表示图形高，0<=n<= 144(0:自动选择) 。
	// param2：表示图形宽，8<=n<= 144(param1 为0 时,无效) 。
	// param3：表示纵向放大倍数。
	// 3. QR CODE
	// param1：表示图形版本号，1<=n<=30(0: 自动选择) 。
	// param2：表示纠错等级，n = 76,77,81,72(L:7%,M:15%,Q:25%,H:30%) 。
	public void setBarcodeParam(byte param1, byte param2, byte param3)
	{
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
	}

	public void setBarcodeContent(String content) {
		this.content = content;
	}

	public void setBarcodeContent(String content, String charsetName) {
		this.content = content;
		this.charsetName = charsetName;
	}

	public byte[] getBarcodeData() {
		byte realCommand[];
		switch (barcodeType) {

			case PrinterConstants.BarcodeType.PDF417:
			case PrinterConstants.BarcodeType.DATAMATRIX:
			case PrinterConstants.BarcodeType.QRCODE:
				realCommand = getBarcodeCommand2(content, barcodeType, param1,
						param2, param3);
				break;

			case PrinterConstants.BarcodeType.CODE128:
				// 打印code 128 需要在条码数据前选择字符集.这里选择code b
				// 形如: 29 107 73 11 123 66 78 111 46 1 2 3 4 5
				// 6(这里123456表示字符,十进制对应49,50,51...)
				// index从 123 66 后开始,表示条码数据开始位置. preIndex表示29 107 73 11共占4位.
				byte tempCommand[] = new byte[1024];
				int index = 0;
				int strLength = content.length();
				int tempLength = strLength;
				char charArray[] = content.toCharArray();
				boolean preHasCodeA = false;
				boolean preHasCodeB = false;
				boolean preHasCodeC = false;
				boolean needCodeC = false;
				for (int i = 0; i < strLength; i++) {
					byte a = (byte) charArray[i];
					if (a >= 0 && a <= 31) { // 控制字符,用CodeA(值为[0-31])
						// CODE A
						if (i == 0 || !preHasCodeA) {
							tempCommand[index++] = 123;
							tempCommand[index++] = 65;
							preHasCodeA = true;
							preHasCodeB = false;
							preHasCodeC = false;
							tempLength += 2;
						}
						tempCommand[index++] = a;
						continue; // 继续下次循环
					}
					if (a >= 48 && a <= 57) { // 数字用codeC,以便打印更长的数据
						if (!preHasCodeC) { // 如果前面不是codeC,证明这是第一个数字,需要判断是否需要转codeC
							// 先判断是否需要转codeC
							// 判断后续的8位是不是均为数字,超过8位才有必要转换成codeC,这里连续数字>=9位开始转.
							for (int m = 1; m < 9; m++) {
								// 后续的m位均为数字,m没有超出字符长度.
								if (i + m != strLength
										&& PrintUtil.isNum((byte) charArray[i + m])) {
									// m等于8时证明后续的8位均为数字,需要转codeC.
									if (m == 8) {
										needCodeC = true;
									}
									continue;
								}
								needCodeC = false;
								break; // 跳出判断是否需要转codeC
							}

						}
						// 需要codeC
						if (needCodeC) {
							if (!preHasCodeC) {
								tempCommand[index++] = 123;
								tempCommand[index++] = 67;
								preHasCodeA = false;
								preHasCodeB = false;
								preHasCodeC = true;
								tempLength += 2;
							}
							// 该数字不是最后一个字符
							if (i != strLength - 1) {
								byte b = (byte) charArray[i + 1];
								// 数字后面还是数字,俩个数字连接起来
								if (PrintUtil.isNum(b)) {
									tempCommand[index++] = (byte) ((a - 48) * 10 + (b - 48));
									tempLength--;
									i++;
									continue; // 继续下次循环
								}
								// else-->数字后面是非数字,此数字就为单个数字,需要用codeA或者codeB,这里选择codeB,然后跳出循环
							}
							// else-->该数字是最后一个字符.此数字就为单个数字,需要用codeA或者codeB,这里选择codeB
						}
						// else-->数字字符,但不需要转codeC,用codeB.
					}
					// else-->非数字字符用codeB.

					// CODE B
					if (!preHasCodeB) {
						tempCommand[index++] = 123;
						tempCommand[index++] = 66;
						preHasCodeA = false;
						preHasCodeB = true;
						preHasCodeC = false;
						tempLength += 2;
					}
					tempCommand[index++] = a;
				}

				// 经处理过后实际发送的条码数据.
				realCommand = getBarcodeCommand1(new String(tempCommand, 0, tempLength), barcodeType, (byte) tempLength);
				break;

			case PrinterConstants.BarcodeType.CODE93:
				// 形如: 29 107 70 6 1 2 3 4 5 6
				realCommand = getBarcodeCommand1(content, barcodeType, (byte)content.length());
				break;

			default:
				// 其他类型数据以0结尾.
				Log.e("jiangcy","barcodeType = "+barcodeType);
				Log.e("jiangcy","content = "+content);
				realCommand = getBarcodeCommand1(content, barcodeType);
				break;
		}
		StringBuffer sb = new StringBuffer();
		
		for (int j = 0; j < realCommand.length; j++) {
			String temp = Integer.toHexString(realCommand[j] & 255);
			if (temp.length() == 1) {
				temp = "0" + temp;
			}
			sb.append(temp + " ");
		}

		Log.d(TAG, "bar code command: " + sb.toString());
		
		return realCommand;
	}

	private byte[] getBarcodeCommand1(String content, byte... byteArray) {
		int index = 0;
		byte tmpByte[];
		try {
			if (charsetName != "") {
				tmpByte = content.getBytes(charsetName);
			}else{
				tmpByte = content.getBytes();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		byte command[] = new byte[tmpByte.length + 13];

		// 设置条码横向宽度 2<=n<=6,默认为2
		command[index++] = 29;
		command[index++] = 119;
		if (param1 >= 2 && param1 <= 6)
		{
			command[index++] = (byte) param1;
		}
		else
		{
			command[index++] = 2;
		}

		// 设置条码高度 1<=n<=255,默认162
		command[index++] = 29;
		command[index++] = 104;
		if (param2 >= 1 && param2 <= 255)
		{
			command[index++] = (byte)param2;
		}
		else
		{
			command[index++] = (byte) 162;
		}

		// 设置条码注释打印位置.0不打印,1上方,2下方,3上下方均有
		command[index++] = 29;
		command[index++] = 72;
		if (param3 >= 0 && param3 <= 3)
		{
			command[index++] = (byte) param3;
		}
		else
		{
			command[index++] = 0;
		}

		//条码类型与长度
		command[index++] = 0x1D;
		command[index++] = 0x6B;
		for (int i = 0; i < byteArray.length; i++)
		{
			command[index++] = byteArray[i];
		}

		//条码数据

		for (int j = 0; j < tmpByte.length; j++)
		{
			command[index++] = tmpByte[j];
		}

		return command;
	}

	//二维条码
	private byte[] getBarcodeCommand2(String content, byte barcodeType, int param1, int param2, int param3) {
		byte tmpByte[];
		try {
			if (charsetName != "") {
				tmpByte = content.getBytes(charsetName);
			}else{
				tmpByte = content.getBytes();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		byte command[] = new byte[tmpByte.length + 10];
		command[0] = 29;
		command[1] = 90;
		command[2] = (byte) (barcodeType - 100);
		command[3] = 27;
		command[4] = 90;
		command[5] = (byte) param1;
		command[6] = (byte) param2;
		command[7] = (byte) param3;
		command[8] = (byte) (tmpByte.length % 256);
		command[9] = (byte) (tmpByte.length / 256);
		System.arraycopy(tmpByte, 0, command, 10, tmpByte.length);

		return command;
	}
}
