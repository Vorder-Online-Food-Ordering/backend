package com.example.backend.event;

import com.example.backend.model.User;
import com.example.backend.response.AuthResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {
    private User user;
    private AuthResponse authResponse;
    private String applicationUrl;

    public RegistrationCompleteEvent( String applicationUrl, User user ,AuthResponse authResponse) {
        super(user);
        this.applicationUrl = applicationUrl;
        this.user = user;
        this.authResponse = authResponse;
    }
}
