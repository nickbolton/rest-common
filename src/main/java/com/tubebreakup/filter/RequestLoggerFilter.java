package com.tubebreakup.filter;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Primary
public class RequestLoggerFilter extends CommonsRequestLoggingFilter {
    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        setIncludeQueryString(true);
        setIncludePayload(true);
        setMaxPayloadLength(10000);
        setIncludeHeaders(true);
        setIncludeClientInfo(true);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        super.doFilterInternal(request, response, filterChain);
    }

    protected void beforeRequest(HttpServletRequest request, String message) { }

    protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            RequestPayload requestPayload = new RequestPayload();
            requestPayload.setUri(request.getRequestURI());

            if (this.isIncludeClientInfo()) {
                requestPayload.setClient(request.getRemoteAddr());
                HttpSession session = request.getSession(false);
                if (session != null) {
                    requestPayload.setSession(session.getId());
                }
                requestPayload.setUser(request.getRemoteUser());
            }

            if (this.isIncludeHeaders()) {
                requestPayload.setHeaders(new ServletServerHttpRequest(request).getHeaders());
            }

            if (this.isIncludePayload()) {
                String payload = getMessagePayload(request);
                requestPayload.setRawPayload(payload);
                if (payload != null) {
                    if (request.getContentType() != null && request.getContentType().startsWith("application/json")) {
                        try {
                            Map<String, ?> json = mapper.readValue(payload, Map.class);
                            requestPayload.setJsonPayload(json);
                        } catch (Exception e) {
                        }
                    }
                }
            }

            try {
                StringWriter writer = new StringWriter();
                mapper.writerWithDefaultPrettyPrinter().writeValue(writer, requestPayload);
                return writer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        } catch (Exception e) {
            return super.createMessage(request, prefix, suffix);
        }
    }
}