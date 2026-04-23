package com.smartcampus.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.logging.Logger;

// @Provider = JAX-RS auto-registers this filter for ALL requests
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(LoggingFilter.class.getName());

    // Called BEFORE the request reaches the resource method
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOG.info(String.format("[REQUEST]  %s %s",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri()
        ));
    }

    // Called AFTER the resource method returns
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        LOG.info(String.format("[RESPONSE] %s %s -> Status: %d",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri(),
                responseContext.getStatus()
        ));
    }
}