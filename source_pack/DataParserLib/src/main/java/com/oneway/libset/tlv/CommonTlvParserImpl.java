package com.oneway.libset.tlv;

import java.nio.ByteBuffer;
import java.util.Locale;

import com.oneway.libset.tlv.entity.TLV;
import com.oneway.utils.Utils;

public class CommonTlvParserImpl extends TlvParser {

	// private String[] tags;
	private int lenRadix = 16;
	// public final CustomLogger logger =
	// CustomLogger.getLogger(CommonTlvParserImpl.class);

	// @Override
	// public void setPreTags(String[] tags) {
	// // TODO Auto-generated method stub
	// this.tags = tags;
	// }

	// @Override
	// public TLVParseResult parse(String data) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public TLVParseResult parse(byte[] data) {
	// // TODO Auto-generated method stub
	// return null;
	// }

	@Override
	public SingleTLVParseResult parseSingleTLV(ByteBuffer byteBuffer) {
		if (!byteBuffer.hasRemaining()) {
			logger.debug("parseSingleTLV:no buffer data found");
		}
		SingleTLVParseResult tlvResult = new SingleTLVParseResult();
		// tag parse
		boolean parseResult = false;
		while (!parseResult) {
			TLV tlv = new TLV();
			ByteBuffer tagBuffer = ByteBuffer.allocate(10);
			// tag parse
			byteBuffer.mark();
			byte[] allData = new byte[byteBuffer.remaining()];
			byteBuffer.get(allData);
			byteBuffer.reset();
			String allDataStr = Utils.bytesToHexString(allData, allData.length);
			logger.debug("byte buffer:" + allDataStr);
			boolean tagFound = false;
			if (tags == null || tags.length == 0) {
				tlvResult.setResultCode("101");
				break;
			}
			for (String tag : tags) {
				if (allDataStr.toUpperCase(Locale.getDefault()).startsWith(tag.toUpperCase(Locale.getDefault()))) {
					tagFound = true;
					byteBuffer.position(byteBuffer.position() + tag.length() / 2);
					tagBuffer.put(Utils.hexString2Bytes(tag));
					break;
				}
			}
			if (!tagFound) {
				tlvResult.setResultCode("102");
				break;
			}
			tagBuffer.flip();
			printByteBufferInfo(tagBuffer, false);
			byte[] tagBytes = new byte[tagBuffer.remaining()];
			tagBuffer.get(tagBytes);
			tlv.setTagStr(Utils.bytesToHexString(tagBytes, tagBytes.length));
			logger.debug("\n");
			logger.debug("***********section************");
			logger.debug("TAG:" + tlv.getTagStr());

			// length parse
			ByteBuffer lengthBuffer = ByteBuffer.allocate(10);
			if (!byteBuffer.hasRemaining()) {
				logger.debug("no length data found");
				parseResult = false;
				// 未发现Len
				tlvResult.setResultCode("02");
				break;
			}
			byte currentPositionByte = byteBuffer.get();
			int temp = 0;
			boolean multiLen = false;
			if ((currentPositionByte & 0x80) == 0x80) {
				if ((temp = (currentPositionByte & 0xff - 0x80 & 0xff)) > 0) {
					// length first byte > 0x80
					// System.out.println("temp:" + temp);
					lengthBuffer.put(currentPositionByte);
					byte[] realLengthBytes = new byte[temp];
					if (byteBuffer.remaining() < realLengthBytes.length) {
						logger.debug("not enough length data found");
						// Len错误
						tlvResult.setResultCode("03");
						parseResult = false;
						break;
					}
					byteBuffer.get(realLengthBytes);
					lengthBuffer.put(realLengthBytes);
					multiLen = true;
				} else {
					logger.debug("length can not head with 0x80");
					// Len错误
					tlvResult.setResultCode("05");
					parseResult = false;
					break;
				}
			} else {
				lengthBuffer.put(currentPositionByte);
			}
			lengthBuffer.flip();
			printByteBufferInfo(lengthBuffer, false);
			byte[] lengthBytes = new byte[lengthBuffer.remaining()];
			lengthBuffer.get(lengthBytes);
			tlv.setLengthStr(Utils.bytesToHexString(lengthBytes, lengthBytes.length));
			logger.debug("LEN:" + tlv.getLengthStr());

			// value parse
			String finalLenStr = tlv.getLengthStr();
			if (multiLen) {
				finalLenStr = finalLenStr.substring(2);
			}
			int lenInt = Integer.parseInt(finalLenStr, 16);
			if (byteBuffer.remaining() < lenInt) {
				logger.warn("value length not consistent,quit parse");
				// value与len长度不一致
				tlvResult.setResultCode("04");
				parseResult = false;
				break;
			}
			byte[] valueBytes = new byte[lenInt];
			byteBuffer.get(valueBytes);
			tlv.setValueStr(Utils.bytesToHexString(valueBytes, valueBytes.length));
			logger.debug("VAL:" + tlv.getValueStr());
			logger.debug("***********section************\n");
			parseResult = true;
			tlvResult.setResultCode("00");
			tlvResult.setTlv(tlv);
		}
		tlvResult.setParseResult(parseResult);
		return tlvResult;
	}

	@Override
	public boolean setLenRadix(int radix) {
		switch (radix) {
		case 10:
		case 16:
			// 当前未实现进制长度逻辑
			lenRadix = radix;
			logger.debug("set radix:" + lenRadix);
			break;

		default:
			return false;
		}
		return true;
	}

}
