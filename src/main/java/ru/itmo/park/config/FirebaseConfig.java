package ru.itmo.park.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("classpath:firebase-token.json")
    private Resource firebaseKey;

    /**
     * Настройка Firebase
     */
    @PostConstruct
    public void init() {
        try(InputStream is = firebaseKey.getInputStream()){
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(is))
                    .build();
            FirebaseApp.initializeApp(options);
        }catch (IOException ex){
            log.info(ex.getMessage());
        }
    }

}
