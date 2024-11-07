package junggoin.Back_End.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Value("${server_url}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        Server server = new Server()
                .url(serverUrl)
                .description("Server URL");
        Server localhost = new Server()
                .url("http://localhost:8080/")
                .description("Localhost URL");
        return new OpenAPI()
                .components(new Components())
                .addServersItem(server)
                .addServersItem(localhost)
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("JunggoWashing API Test") // API의 제목
                .description("") // API에 대한 설명
                .version("0.0.1"); // API의 버전
    }
}