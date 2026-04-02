package com.toy.project.studio.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import com.toy.project.studio.auth.dto.response.AuthSessionResponse;
import com.toy.project.studio.auth.service.AuthService;
import com.toy.project.studio.auth.support.RefreshTokenCookieProvider;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private RefreshTokenCookieProvider refreshTokenCookieProvider;

    @InjectMocks
    private AuthController authController;

    @Test
    void sessionReturnsAuthenticatedTrueWithoutCookieMutation() {
        when(authService.hasValidSession("refresh-token")).thenReturn(true);

        ResponseEntity<AuthSessionResponse> response = authController.session("refresh-token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(AuthSessionResponse.of(true));
        assertThat(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE)).isFalse();
        verify(refreshTokenCookieProvider, never()).delete();
    }

    @Test
    void sessionReturnsAuthenticatedFalseWithoutCookieMutationWhenCookieIsMissing() {
        when(authService.hasValidSession(null)).thenReturn(false);

        ResponseEntity<AuthSessionResponse> response = authController.session(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(AuthSessionResponse.of(false));
        assertThat(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE)).isFalse();
        verify(refreshTokenCookieProvider, never()).delete();
    }

    @Test
    void sessionClearsCookieWhenRefreshTokenIsNotRecoverable() {
        ResponseCookie deleteCookie = ResponseCookie.from(RefreshTokenCookieProvider.COOKIE_NAME, "")
                .path("/auth")
                .maxAge(Duration.ZERO)
                .build();

        when(authService.hasValidSession("stale-refresh-token")).thenReturn(false);
        when(refreshTokenCookieProvider.delete()).thenReturn(deleteCookie);

        ResponseEntity<AuthSessionResponse> response = authController.session("stale-refresh-token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(AuthSessionResponse.of(false));
        assertThat(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE)).isEqualTo(deleteCookie.toString());
        verify(refreshTokenCookieProvider).delete();
    }
}
