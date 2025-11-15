package com.abhedyam.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {
    
    @Value("${app.firebase.credentials-file:abhedyam-6abbc-firebase-adminsdk-fbsvc-8bce8e2c24.json}")
    private String firebaseCredentialsFile;
    
    @Value("${app.firebase.credentials:}")
    private String firebaseCredentials;
    
    @Bean
    public FirebaseApp firebaseApp() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options;
                InputStream serviceAccount;
                
                if (firebaseCredentials != null && !firebaseCredentials.isEmpty()) {
                    serviceAccount = new ByteArrayInputStream(firebaseCredentials.getBytes());
                    log.info("Loading Firebase credentials from environment variable");
                } else {
                    ClassPathResource resource = new ClassPathResource(firebaseCredentialsFile);
                    serviceAccount = resource.getInputStream();
                    log.info("Loading Firebase credentials from file: {}", firebaseCredentialsFile);
                }
                
                GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .build();
                
                FirebaseApp app = FirebaseApp.initializeApp(options);
                log.info("Firebase initialized successfully");
                return app;
            } else {
                return FirebaseApp.getInstance();
            }
        } catch (IOException e) {
            log.error("Failed to initialize Firebase", e);
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }
}

