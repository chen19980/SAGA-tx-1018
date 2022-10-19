package tw.com.firstbank.fcbcore.fcbframework.spring.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tw.com.firstbank.fcbcore.fcbframework.core.filter.BusinessServiceCORSFilter;
import tw.com.firstbank.fcbcore.fcbframework.core.filter.MDCFilter;
import tw.com.firstbank.fcbcore.fcbframework.core.filter.RequestHeaderFilter;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RequestHeaderFilter> requestHeaderFilter() {
        FilterRegistrationBean<RequestHeaderFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestHeaderFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<MDCFilter> mdcFilter() {
        FilterRegistrationBean<MDCFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new MDCFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    //@Order(4)
    @Bean
    public FilterRegistrationBean<BusinessServiceCORSFilter> businessServiceCORSFilter() {
        FilterRegistrationBean<BusinessServiceCORSFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new BusinessServiceCORSFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(4);
        return registrationBean;
    }
}
