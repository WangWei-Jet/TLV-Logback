package com.oneway.libset.tlv;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.oneway.libset.tlv.entity.TLV;
import com.oneway.utils.CustomLogger;
import com.oneway.utils.Utils;

public abstract class TlvParser {
	public String[] tags;
	public final CustomLogger logger = CustomLogger.getLogger(this.getClass());
	public final String TAG = this.getClass().getSimpleName();

	/**
	 * 设置tag，告诉解析器数据中所有的tag
	 * 
	 * @param tags
	 */
	public void setPreTags(String[] tags) {
		this.tags = tags;
	}

	/**
	 * 设置Len的进制，默认十六进制
	 * 
	 * @param radix
	 */
	public abstract boolean setLenRadix(int radix);

	/**
	 * 解析数据
	 * 
	 * @param data
	 * @return
	 */
	public TLVParseResult parse(String data) {
		if (data == null || data.trim().length() == 0) {
			logger.warn("data to parse is null");
			return null;
		}
		if (data.length() % 2 != 0) {
			logger.warn("data length error,length must be a number of even");
			return null;
		}
		byte[] sourceBytes = Utils.hexString2Bytes(data);
		return parse(sourceBytes);
	}

	/**
	 * 解析数据
	 * 
	 * @param data
	 * @return
	 */
	public TLVParseResult parse(byte[] data) {
		TLVParseResult tlvParseResult = new TLVParseResult();
		if (data == null || data.length == 0) {
			logger.warn("data to parse is null");
			return tlvParseResult;
		}
		List<TLV> resultList = new ArrayList<TLV>();
		// parse tlv
		ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
		byteBuffer.put(data);

		logger.debug(TAG + ":start to parse data");

		printByteBufferInfo(byteBuffer, false);
		byteBuffer.flip();
		printByteBufferInfo(byteBuffer, false);
		// boolean parseResult = true;
		SingleTLVParseResult singleTlvResult = null;
		while (byteBuffer.hasRemaining()) {
			singleTlvResult = parseSingleTLV(byteBuffer);
			if (!singleTlvResult.getParseResult()) {
				break;
			}
			resultList.add(singleTlvResult.getTlv());
		}
		tlvParseResult.setResultCode(singleTlvResult.getResultCode());
		tlvParseResult.setParseResult(singleTlvResult.getParseResult());
		// while (byteBuffer.hasRemaining()) {
		// TLV tlv = new TLV();
		// // tag parse
		// boolean tagOverFlag = false;
		// boolean firstTagByte = true;
		// ByteBuffer tagBuffer = ByteBuffer.allocate(10);
		// while (!tagOverFlag) {
		// if (!byteBuffer.hasRemaining()) {
		// logger.debug("no more tag data found");
		// // 无效tag
		// tlvParseResult.setResultCode("01");
		// parseResult = false;
		// break;
		// }
		// byte currentPositionByte = byteBuffer.get();
		// int temp = 0;
		// if (firstTagByte) {
		// if ((currentPositionByte & 0x0f) == 0x0f
		// && (temp = (((currentPositionByte - 0x0f) & 0xff) >> 4)) > 0 && temp
		// % 2 == 1) {
		// // 1F,3F,5F,7F,9F,BF,DF,FF
		// // do nothing
		// } else {
		// tagOverFlag = true;
		// }
		// firstTagByte = false;
		// } else {
		// if ((currentPositionByte & 0x80) != 0x80) {
		// tagOverFlag = true;
		// }
		// }
		// // System.out.println("temp:" + temp);
		// tagBuffer.put(currentPositionByte);
		// }
		// if (!tagOverFlag) {
		// break;
		// }
		// tagBuffer.flip();
		// printByteBufferInfo(tagBuffer, false);
		// byte[] tagBytes = new byte[tagBuffer.remaining()];
		// tagBuffer.get(tagBytes);
		// tlv.setTagStr(Utils.bytesToHexString(tagBytes, tagBytes.length));
		// logger.debug("\n");
		// logger.debug("***********section************");
		// logger.debug("TAG:" + tlv.getTagStr());
		//
		// // length parse
		// ByteBuffer lengthBuffer = ByteBuffer.allocate(10);
		// if (!byteBuffer.hasRemaining()) {
		// logger.debug("no length data found");
		// parseResult = false;
		// // 未发现Len
		// tlvParseResult.setResultCode("02");
		// break;
		// }
		// byte currentPositionByte = byteBuffer.get();
		// int temp = 0;
		// boolean multiLen = false;
		// if ((currentPositionByte & 0x80) == 0x80 && (temp =
		// (currentPositionByte & 0xff - 0x80 & 0xff)) > 0) {
		// // length first byte > 0x80
		// // System.out.println("temp:" + temp);
		// lengthBuffer.put(currentPositionByte);
		// byte[] realLengthBytes = new byte[temp];
		// if (byteBuffer.remaining() < realLengthBytes.length) {
		// logger.debug("not enough length data found");
		// // Len错误
		// tlvParseResult.setResultCode("03");
		// parseResult = false;
		// break;
		// }
		// byteBuffer.get(realLengthBytes);
		// lengthBuffer.put(realLengthBytes);
		// multiLen = true;
		// } else {
		// lengthBuffer.put(currentPositionByte);
		// }
		// lengthBuffer.flip();
		// printByteBufferInfo(lengthBuffer, false);
		// byte[] lengthBytes = new byte[lengthBuffer.remaining()];
		// lengthBuffer.get(lengthBytes);
		// tlv.setLengthStr(Utils.bytesToHexString(lengthBytes,
		// lengthBytes.length));
		// logger.debug("LEN:" + tlv.getLengthStr());
		//
		// // value parse
		// String finalLenStr = tlv.getLengthStr();
		// if (multiLen) {
		// finalLenStr = finalLenStr.substring(2);
		// }
		// int lenInt = Integer.parseInt(finalLenStr, 16);
		// if (byteBuffer.remaining() < lenInt) {
		// logger.warn("value length not consistent,quit parse");
		// // value与len长度不一致
		// tlvParseResult.setResultCode("04");
		// parseResult = false;
		// break;
		// }
		// byte[] valueBytes = new byte[lenInt];
		// byteBuffer.get(valueBytes);
		// tlv.setValueStr(Utils.bytesToHexString(valueBytes,
		// valueBytes.length));
		// logger.debug("VAL:" + tlv.getValueStr());
		// logger.debug("***********section************\n");
		// resultList.add(tlv);
		// }
		tlvParseResult.setTlvList(resultList);
		// tlvParseResult.setParseResult(parseResult);
		// if (parseResult) {
		// // 成功
		// tlvParseResult.setResultCode("00");
		// }
		logger.debug(TAG + ":data parse over");
		return tlvParseResult;
	}

	public void printByteBufferInfo(ByteBuffer byteBuffer, boolean allowPrint) {
		if (allowPrint) {
			logger.debug("position:" + byteBuffer.position() + "\tlimit:" + byteBuffer.limit() + "\tcapacity:"
					+ byteBuffer.capacity() + "\tremaining:" + byteBuffer.remaining());
		}
	}

	/**
	 * 解析单个TLV数据，可通过多次调用解析完成所有的TLV数据
	 * 
	 * @param byteBuffer
	 * @return
	 */
	public abstract SingleTLVParseResult parseSingleTLV(ByteBuffer byteBuffer);

}
