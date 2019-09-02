package com.oneway.libset.tlv;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.oneway.libset.tlv.entity.TLV;
import com.oneway.utils.CustomLogger;
import com.oneway.utils.SAXParserHandler;

public class TLVParseResult {

	List<TLV> tlvList;
	boolean parseResult;
	String resultCode;

	// private static Logger logger =
	// LoggerFactory.getLogger(TLVParseResult.class);
	private static CustomLogger logger = CustomLogger.getLogger(TLVParseResult.class);

	public List<TLV> getTlvList() {
		return tlvList;
	}

	public void setTlvList(List<TLV> tlvList) {
		this.tlvList = tlvList;
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

	public static class ResultCodeMatcher {
		private static SAXParserHandler handler = new SAXParserHandler();

		static {
			setResultDescriptionConfigLocation(
					ResultCodeMatcher.class.getClassLoader().getResourceAsStream("config/ResultModel.xml"));
		}

		public static boolean setResultDescriptionConfigLocation(Object configFileUri) {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			try {
				SAXParser parser = factory.newSAXParser();
				String uri = null;
				InputStream is = null;
				if (configFileUri != null) {
					if (configFileUri instanceof String) {
						uri = (String) configFileUri;
						logger.debug("config file location:" + uri);
						parser.parse(uri, handler);
					} else if (configFileUri instanceof InputStream) {
						is = (InputStream) configFileUri;
						logger.debug("config file input stream");
						parser.parse(is, handler);
					}
				}
				if (uri == null && is == null) {
					logger.debug("no config file found");
					return false;
				}
				logger.debug("工程配置中总共有" + handler.getResultCodeList().size() + "个结果码配置信息");
				// for (ResultInstruction tempResult :
				// handler.getResultCodeList()) {
				// if (tempResult.getCode().equalsIgnoreCase(resultCode)) {
				// logger.debug("结果码匹配成功:" + tempResult.getCode() + "_" +
				// tempResult.getComments());
				// return tempResult.getComments();
				// }
				// }
				return true;
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		public static String getDescription(String resultCode) {
			if (handler.getResultCodeList().size() == 0) {
				return null;
			}
			try {
				for (ResultInstruction tempResult : handler.getResultCodeList()) {
					if (tempResult.getCode().equalsIgnoreCase(resultCode)) {
						logger.debug("结果码匹配成功:" + tempResult.getCode() + "_" + tempResult.getComments());
						return tempResult.getComments();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

}
