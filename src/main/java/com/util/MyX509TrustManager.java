package com.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
* @author liuyg
* @version 创建时间：2018年12月11日 下午6:27:11
* @ClassName 类名称：
* @Description 类描述：
*/
/**
 * @author liuyg
 *
 */
public class MyX509TrustManager implements X509TrustManager {

	public void checkClientTrusted(X509Certificate[] chain, String authType)
		    throws CertificateException
		  {
		  }
 
		  public void checkServerTrusted(X509Certificate[] chain, String authType)
		    throws CertificateException
		  {
		  }
 
		  public X509Certificate[] getAcceptedIssuers()
		  {
		    return null;
		  }

}
