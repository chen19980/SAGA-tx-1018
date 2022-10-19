package tw.com.firstbank.fcbcore.fcbframework.core.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 此類別用來統一設定 http header 中的 access control 資訊.
 *
 */
public class BusinessServiceCORSFilter implements Filter {

    @SuppressWarnings("unused")
    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST");
        //httpResponse.setHeader("Access-Control-Allow-Headers", "content-type, authorization");

        HttpServletRequest http_request = (HttpServletRequest) request;
        if(!http_request.getMethod().equals("OPTIONS")){
            chain.doFilter(request, response);
        }else{
            httpResponse.setStatus(HttpServletResponse.SC_OK);
        }
    }
}

