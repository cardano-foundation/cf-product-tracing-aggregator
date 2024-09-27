package org.cardanofoundation.bolnisiaggregator;

import com.bloxbean.cardano.yaci.core.config.YaciConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan({
        "org.cardanofoundation.bolnisiaggregator.model"
})
@EnableJpaRepositories({
        "org.cardanofoundation.bolnisiaggregator.model.repository"
})
@SpringBootApplication
public class BolnisiAggregatorApplication {

    public static void main(String[] args) {
        YaciConfig.INSTANCE.setReturnTxBodyCbor(true);
        SpringApplication.run(BolnisiAggregatorApplication.class, args);
    }

}
