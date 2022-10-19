package tw.com.firstbank.fcbcore.fcbframework.core.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 此類別用來設定 MDC 資訊.
 */
public class MDCFilter implements Filter {
    private final static Logger logger = LoggerFactory.getLogger(MDCFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        if (logger.isDebugEnabled()) logger.debug("MDCFilter");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            MDC.put(RequestHeaderFilter.CORE_GUID, req.getHeader(RequestHeaderFilter.CORE_GUID));
        } catch (Exception e) {
            //ignored
        }

        try {
            chain.doFilter(request,response);
        } finally {
            //must be cleared, threadLocal !!
            MDC.clear();
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
