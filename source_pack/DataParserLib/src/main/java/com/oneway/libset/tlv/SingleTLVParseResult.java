package com.oneway.libset.tlv;

import com.oneway.libset.tlv.entity.TLV;

public class SingleTLVParseResult {

	TLV tlv;
	boolean parseResult;
	String resultCode;

	// private static Logger logger =
	// LoggerFactory.getLogger(SingleTLVParseResult.class);

	public TLV getTlv() {
		return tlv;
	}

	public void setTlv(TLV tlv) {
		this.tlv = tlv;
	}

	public boolean getParseResult() {
		return parseResult;
	}

	public void setParseResult(boolean parseResult) {
		this.parseResult = parseResult;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

}
