package com.example.application.data.generator;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import com.example.application.data.entity.User.User;
import com.example.application.data.entity.User.Interests;

import com.example.application.data.entity.WTA.Stage;
import com.example.application.data.repository.InterestsRepository;
import com.example.application.data.repository.UserRepository;
import com.example.application.data.repository.StageRepository;
import com.vaadin.flow.spring.annotation.SpringComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.vaadin.artur.exampledata.DataType;
import org.vaadin.artur.exampledata.ExampleDataGenerator;

@SpringComponent
public class UserDataGenerator {

    @Bean
    public CommandLineRunner loadUserData(UserRepository contactRepository, InterestsRepository interestsRepository) {

        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (contactRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            List<Interests> interests = interestsRepository.saveAll(Stream.of("ATP", "WTA", "ATP/WTA")
                    .map(Interests::new).collect(Collectors.toList()));

            logger.info("... generating 50 User entities...");
            ExampleDataGenerator<User> contactGenerator = new ExampleDataGenerator<>(User.class,
                    LocalDateTime.now());
            contactGenerator.setData(User::setNickname, DataType.FIRST_NAME);
            contactGenerator.setData(User::setEmail, DataType.EMAIL);

            Random r = new Random(seed);
            List<User> users = contactGenerator.create(1, seed).stream().peek(contact -> {
                contact.setInterest(interests.get(r.nextInt(interests.size())));
            }).collect(Collectors.toList());

            contactRepository.saveAll(users);

            logger.info("Generated demo data");
        };
    }

}