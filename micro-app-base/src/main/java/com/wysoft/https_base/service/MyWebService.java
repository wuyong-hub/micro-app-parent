package com.wysoft.https_base.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService(name = "webService", targetNamespace = "http://service.wysoft.com")
@SOAPBinding(style=Style.RPC)
public interface MyWebService {
	@WebMethod
	public String service(@WebParam(name="body") String body);
}
