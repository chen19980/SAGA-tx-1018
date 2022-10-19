package tw.com.firstbank.fcbcore.fcbframework.core.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 此類別用來生成可以修改的 Http Request Map 結構.
 * <pre>
 *     原生的 HttpServletRequest 是 immutable，不可修改的
 *     若有需要額外加入請求的資料，則必需轉換成 mutable
 * </pre>
 */
final class MutableHttpServletRequest extends HttpServletRequestWrapper {
    // holds custom header and value mapping
    private final Map<String, String> customHeaders;
    private final Map<String, String[]> customParams;

    public MutableHttpServletRequest(HttpServletRequest request){
        super(request);
        this.customHeaders = new HashMap<String, String>();
        this.customParams = new HashMap<String, String[]>();
    }

    public void putHeader(String name, String value){
        this.customHeaders.put(name, value);
    }

    public Boolean headerExist(String name) {
        return customHeaders.containsKey(name);
    }

    @Override
    public String getHeader(String name) {
        // check the custom headers first
        String headerValue = customHeaders.get(name);

        if (headerValue != null){
            return headerValue;
        }
        // else return from into the original wrapped object
        return ((HttpServletRequest) getRequest()).getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        // create a set of the custom header names
        Set<String> set = new HashSet<String>(customHeaders.keySet());

        // now add the headers from the wrapped request object
        Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
        while (e.hasMoreElements()) {
            // add the names of the request headers into the list
            String n = e.nextElement();
            set.add(n);
        }

        // create an enumeration from the set and return
        return Collections.enumeration(set);
    }

    public void putParameter(String name, String [] value){
        this.customParams.put(name, value);
    }

    public void putParameter(String name, String value){
        String [] values = customParams.get(name);
        List<String> list = null;
        if (values != null) {
            list = Arrays.asList(values);
        } else {
            list = new ArrayList<String>();
        }
        list.add(value);
        String[] arr = new String[list.size()];
        arr = list.toArray(arr);
        customParams.put(name, arr);
    }

    @Override
    public String getParameter(String name) {
        // check the custom parameter first
        String [] value = customParams.get(name);

        if (value != null){
            return value[0];
        }

        // else return from into the original wrapped object
        return ((HttpServletRequest) getRequest()).getParameter(name);
    }

    @Override
    public String [] getParameterValues(String name) {
        // check the custom parameter first
        String [] value = customParams.get(name);

        if (value != null){
            return value;
        }

        // else return from into the original wrapped object
        return ((HttpServletRequest) getRequest()).getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new HashMap<String, String[]>();
        Map<String, String[]> oriMap = ((HttpServletRequest) getRequest()).getParameterMap();
        map.putAll(oriMap);

        if (customParams != null) {
            map.putAll(customParams);
        }

        return map;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        // create a set of the custom parameter names
        Set<String> set = new HashSet<String>(customParams.keySet());

        // now add the parameters from the wrapped request object
        Enumeration<String> e = ((HttpServletRequest) getRequest()).getParameterNames();
        while (e.hasMoreElements()) {
            // add the names of the request parameters into the list
            String n = e.nextElement();
            set.add(n);
        }

        // create an enumeration from the set and return
        return Collections.enumeration(set);
    }
}

