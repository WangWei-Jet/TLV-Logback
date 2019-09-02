package com.oneway.libset;

import com.oneway.libset.tlv.CommonTlvParserImpl;
import com.oneway.libset.tlv.StandardTlvParserImpl;
import com.oneway.libset.tlv.TlvParser;

public class ParserFactory {

	private static TlvParser standardTlvParser;
	private static TlvParser commonTlvParser;

	public static TlvParser getStandardTlvParser() {
		if (standardTlvParser == null) {
			standardTlvParser = new StandardTlvParserImpl();
		}
		return standardTlvParser;
	}

	public static TlvParser getCommonTlvParser() {
		if (commonTlvParser == null) {
			commonTlvParser = new CommonTlvParserImpl();
		}
		return commonTlvParser;
	}
}
