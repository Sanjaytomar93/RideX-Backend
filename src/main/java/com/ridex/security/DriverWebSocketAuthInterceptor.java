package com.ridex.security;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DriverWebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final CustomDriverDetailsService customDriverDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            authenticateDriver(accessor);
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            validateDriverSubscription(accessor);
        }

        return message;
    }

    private void authenticateDriver(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new AccessDeniedException("Driver token is required for WebSocket connection");
        }

        String token = authorization.substring(7);
        if (!JwtService.ACCOUNT_TYPE_DRIVER.equals(jwtService.extractAccountType(token))) {
            throw new AccessDeniedException("Driver token is required for WebSocket connection");
        }

        CustomDriverDetails driverDetails = (CustomDriverDetails) customDriverDetailsService
                .loadUserByUsername(jwtService.extractUsername(token));

        if (!jwtService.isTokenValid(token, driverDetails.getDriver())) {
            throw new AccessDeniedException("Invalid driver token");
        }

        accessor.setUser(new UsernamePasswordAuthenticationToken(
                driverDetails,
                null,
                driverDetails.getAuthorities()
        ));
    }

    private void validateDriverSubscription(StompHeaderAccessor accessor) {
        if (!(accessor.getUser() instanceof Authentication authentication)
                || !(authentication.getPrincipal() instanceof CustomDriverDetails driverDetails)) {
            throw new AccessDeniedException("Driver authentication is required");
        }

        String expectedDestination = "/topic/driver/" + driverDetails.getDriver().getId() + "/ride-requests";
        if (!expectedDestination.equals(accessor.getDestination())) {
            throw new AccessDeniedException("Driver cannot subscribe to another driver's ride requests");
        }
    }
}
