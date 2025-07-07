package br.com.blavikode.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static br.com.blavikode.library.ApplicationConstants.*;

@EnableJpaRepositories(basePackages = BR_COM_BLAVIKODE_LIBRARY_REPOSITORIES)
@ComponentScan(BR_COM_BLAVIKODE_LIBRARY_REPOSITORIES)
@PropertySource(CLASSPATH_DEFAULT_PROPERTIES)
@EnableAutoConfiguration
@SpringBootApplication
public class LibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
	}

}
