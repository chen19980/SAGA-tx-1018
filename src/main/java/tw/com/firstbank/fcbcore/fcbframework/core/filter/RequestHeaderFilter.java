package tw.com.firstbank.fcbcore.fcbframework.core.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * 此類別用來在 http request 中加入客製化的 http header 資訊.
 * @see MutableHttpServletRequest
 */
public class RequestHeaderFilter implements Filter {
    private final static Logger logger = LoggerFactory.getLogger(RequestHeaderFilter.class);

    public static final String CORE_GUID = "x-core-guid";

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try
        {

            if (request instanceof HttpServletRequest)
            {

                logger.debug("Request  {} : {}", ((HttpServletRequest) request).getMethod(), ((HttpServletRequest) request).getRequestURI());
                HttpServletRequest httpReq = (HttpServletRequest) request;
                MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(httpReq);

                if (!StringUtils.hasText(mutableRequest.getHeader(CORE_GUID))) {
                    // add guid
                    String uid = UUID.randomUUID().toString();
                    mutableRequest.putHeader(CORE_GUID, uid);
                }
                logger.debug("RequestHeaderFilter {}={}", CORE_GUID, mutableRequest.getHeader(CORE_GUID));

                chain.doFilter(mutableRequest, response);
            }
            else
            {
                chain.doFilter(request, response);
            }
        }
        catch(Exception e)
        {
            logger.error(e.getMessage(), e);
        }
        finally
        {

        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        if (logger.isDebugEnabled()) logger.info("RequestHeaderFilter");
    }

}
