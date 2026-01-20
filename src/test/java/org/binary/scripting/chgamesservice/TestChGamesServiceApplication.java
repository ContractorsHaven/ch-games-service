package org.binary.scripting.chgamesservice;

import org.springframework.boot.SpringApplication;

public class TestChGamesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(ChGamesServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
