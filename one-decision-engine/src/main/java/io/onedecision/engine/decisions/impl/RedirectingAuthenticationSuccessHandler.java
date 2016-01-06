package io.onedecision.engine.decisions.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

/**
 * Extends logic of {@link AbstractAuthenticationTargetUrlRequestHandler} giving
 * top priority to a URL requested prior to the form login interception.
 *
 * @author Tim Stephenson
 */
public class RedirectingAuthenticationSuccessHandler extends
        SimpleUrlAuthenticationSuccessHandler {

    public RedirectingAuthenticationSuccessHandler(String path) {
        super(path);
    }

    /**
     * First check for a <code>SavedRequest</code> and if none exists continue
     * as per {@link AbstractAuthenticationTargetUrlRequestHandler}.
     */
    protected void handle(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(
                request, response);
        String targetUrl = savedRequest.getRedirectUrl();
        System.out.println("requested url: " + targetUrl);

        if (targetUrl == null) {
            targetUrl = determineTargetUrl(request, response);
        }

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to "
                    + targetUrl);
            return;
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
