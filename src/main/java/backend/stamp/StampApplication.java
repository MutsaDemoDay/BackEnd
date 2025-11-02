package backend.stamp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class StampApplication {

	public static void main(String[] args) {
		SpringApplication.run(StampApplication.class, args);
	}

}
