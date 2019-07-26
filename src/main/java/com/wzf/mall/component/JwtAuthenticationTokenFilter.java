package com.wzf.mall.component;

import com.wzf.mall.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String getTokenHead;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader(this.tokenHeader);
        if (null!=authHeader&&authHeader.startsWith(this.getTokenHead)){
            String authToken = authHeader.substring(this.getTokenHead.length());
            String username = jwtTokenUtil.getUserNameFromToken(authToken);
            log.info("checking username : {}",username);
            if (null!=username&&SecurityContextHolder.getContext().getAuthentication()!=null){
                UserDetails details = this.userDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(authToken,details)){
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(details,null,details.getAuthorities());
                    authentication.setDetails(details);
                    log.info("authenticated user : {}",username);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request,response);
    }
}
