package com.travel_agent.resolver;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.travel_agent.repositories.UserRepository;
import com.travel_agent.repositories.CompanyRepository;
import com.travel_agent.annotation.CurrentUserId;
import com.travel_agent.models.entity.UserEntity;
import com.travel_agent.models.entity.CompanyEntity;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;

@Component
@RequiredArgsConstructor
public class CurrentUserIdResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUserId.class) != null
                && parameter.getParameterType().equals(Integer.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication);

        String username = authentication.getName();
        System.out.println(username);

        String role = authentication.getAuthorities().iterator().next().getAuthority();
        System.out.println("Role: " + role);

        // Handle different roles
        switch (role) {
            case "ROLE_USER":
                UserEntity user = userRepository.findByUsernameOrEmail(username).orElse(null);
                System.out.println("UserInfo: " + user);
                return user != null ? user.getUserId() : null;

            case "ROLE_COMPANY":
                CompanyEntity company = companyRepository.findByUsernameOrEmail(username).orElse(null);
                System.out.println("CompanyInfo: " + company);
                return company != null ? company.getCompanyId() : null;

            case "ROLE_GUEST":
                return null;

            default:
                return null;
        }
    }
}