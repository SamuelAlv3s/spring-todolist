package com.smk.todoList.task.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.smk.todoList.task.TaskRepository;
import com.smk.todoList.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var servletPath = request.getServletPath();

        if (!servletPath.startsWith("/tasks/")) {
            filterChain.doFilter(request, response);
            return;
        }

        var authorization = request.getHeader("Authorization");

        if (authorization == null || authorization.isBlank()) {
            System.out.println("Authorization not found");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        var authEncoded = authorization.substring("Basic".length()).trim();

        byte[] authDecode = Base64.getDecoder().decode(authEncoded);

        var authString = new String(authDecode);

        String[] credentials = authString.split(":");

        String username = credentials[0];
        String password = credentials[1];

        var user = this.userRepository.findByUsername(username);

        System.out.println("User: " + user);

        if (user == null) {
            System.out.println("User not found");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {

            var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

            if (!passwordVerify.verified) {
                System.out.println("Password not match");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            request.setAttribute("idUser", user.getId());
            filterChain.doFilter(request, response);
        }
    }
}
