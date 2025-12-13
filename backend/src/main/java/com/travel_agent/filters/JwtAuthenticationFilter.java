package com.travel_agent.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.travel_agent.utils.JwtUtils;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
    
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response); // Vẫn phải cho request tiếp tục
            return;
        }
        
    
        String token = request.getHeader("Authorization");
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                String username = jwtUtils.extractUsername(token);
        
                // nếu chưa có auth hoặc là guest → override
                boolean shouldSetAuth = currentAuth == null ||
                    currentAuth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_GUEST"));
        
                if (username != null && shouldSetAuth) {
                    var authentication = jwtUtils.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
        
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            // chỉ set guest nếu chưa có auth
            if (currentAuth == null) {

                var defaultAuth = new UsernamePasswordAuthenticationToken(
                        "guest",
                        "guest",
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_GUEST"))
                );
                SecurityContextHolder.getContext().setAuthentication(defaultAuth);
            }
        }
        filterChain.doFilter(request, response);
    
    }
}