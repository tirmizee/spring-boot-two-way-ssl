package com.tirmizee.controller;

import org.springframework.http.server.reactive.SslInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MutualTlsController {

    @GetMapping("/hello")
    public Mono<Map<String, String>> hello(ServerWebExchange exchange) {

        var response = new LinkedHashMap<String, String>();

        response.put("message", "Hello from Mutual TLS Server");

        response.put("clientCertificateSubject", getClientCertificateSubject(exchange));

        return Mono.just(response);
    }

    private String getClientCertificateSubject(ServerWebExchange exchange) {

        return Optional
                .ofNullable(exchange.getRequest().getSslInfo())
                .map(SslInfo::getPeerCertificates)
                .filter(certificates -> certificates.length > 0)
                .map(certificates -> certificates[0])
                .map(this::getSubject)
                .orElse("not-present");
    }

    private String getSubject(X509Certificate certificate) {
        return certificate
                .getSubjectX500Principal()
                .getName();
    }

}
