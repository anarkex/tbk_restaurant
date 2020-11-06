package cl.tbk.test.restaurant.config;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clusterization compatibility
 * @author manuelpinto
 */
@Configuration
@EnableCaching
public class HazelcastConfig {
    /**
     * Configuration is defined on hazelcast.xml
     * @return 
     */
    @Bean
    public HazelcastInstance hazelcastInstance(){
        return Hazelcast.newHazelcastInstance();
    }
}
