package com.example.application.data.generator;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.application.data.entity.Company;
import com.example.application.data.entity.User;
import com.example.application.data.entity.Status;
import com.example.application.data.repository.CompanyRepository;
import com.example.application.data.repository.UserRepository;
import com.example.application.data.repository.StatusRepository;
import com.vaadin.flow.spring.annotation.SpringComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.vaadin.artur.exampledata.DataType;
import org.vaadin.artur.exampledata.ExampleDataGenerator;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(UserRepository contactRepository, CompanyRepository companyRepository,
                                      StatusRepository statusRepository) {

        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (contactRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");
            ExampleDataGenerator<Company> companyGenerator = new ExampleDataGenerator<>(Company.class,
                    LocalDateTime.now());
            companyGenerator.setData(Company::setName, DataType.COMPANY_NAME);
            List<Company> companies = companyRepository.saveAll(companyGenerator.create(5, seed));

            List<Status> statuses = statusRepository
                    .saveAll(Stream.of("Imported lead", "Not contacted", "Contacted", "Customer", "Closed (lost)")
                            .map(Status::new).collect(Collectors.toList()));

            logger.info("... generating 50 User entities...");
            ExampleDataGenerator<User> contactGenerator = new ExampleDataGenerator<>(User.class,
                    LocalDateTime.now());
            contactGenerator.setData(User::setNickname, DataType.FIRST_NAME);
            contactGenerator.setData(User::setEmail, DataType.EMAIL);

            Random r = new Random(seed);
            List<User> users = contactGenerator.create(50, seed).stream().peek(contact -> {
                contact.setCompany(companies.get(r.nextInt(companies.size())));
            }).collect(Collectors.toList());

            contactRepository.saveAll(users);

            logger.info("Generated demo data");
        };
    }

}
