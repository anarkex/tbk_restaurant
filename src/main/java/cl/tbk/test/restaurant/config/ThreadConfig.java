package cl.tbk.test.restaurant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * ThreadPoolTaskExecutor because I need threads to remove items from different queues
 * @author manuelpinto
 */
@Configuration
public class ThreadConfig {

    @Bean(name = "databaseTaskExecutor")
    public TaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor executor=new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(3);
        executor.initialize();
        return executor;
    }
}
