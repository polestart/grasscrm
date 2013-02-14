package com.gcrm.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class TotalFilter extends ActionSupport {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String url;

    public String getUrl() {
        return url;
    }

    @Override
    public String execute() throws Exception {

        HttpServletRequest request = ServletActionContext.getRequest();
        String result = (String) request.getAttribute("jsp");
        url = result;
        if (request.getQueryString() != null) {
            url = url + "?" + request.getQueryString();
        }
        return "url";
    }
}