package com.careauth.compass;

import com.careauth.compass.application.scenario.ScenarioVerifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CareAuthCompassApplication {
    public static void main(String[] args) {
        if (args.length >= 2 && "verify".equals(args[0])) {
            int exitCode = new ScenarioVerifier().verify(args[1], true);
            System.exit(exitCode);
        }
        SpringApplication.run(CareAuthCompassApplication.class, args);
    }
}
