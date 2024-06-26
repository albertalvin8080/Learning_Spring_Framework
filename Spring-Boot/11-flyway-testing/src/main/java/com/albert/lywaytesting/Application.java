package com.albert.lywaytesting;

import com.albert.lywaytesting.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class Application implements CommandLineRunner
{
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private final ProductRepository repository;

    @Override
    public void run(String... args) throws Exception {
        repository.findAll();
    }
}
