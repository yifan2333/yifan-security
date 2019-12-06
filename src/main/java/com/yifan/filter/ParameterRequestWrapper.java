package com.yifan.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ParameterRequestWrapper extends HttpServletRequestWrapper {


    public ParameterRequestWrapper(HttpServletRequest request ) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
       return (String) super.getAttribute(name);
    }
}
