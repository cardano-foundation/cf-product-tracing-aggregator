package org.cardanofoundation.productaggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import com.bloxbean.cardano.yaci.core.config.YaciConfig;

@EntityScan({
        "org.cardanofoundation.productaggregator.model"
})
@EnableJpaRepositories({
        "org.cardanofoundation.productaggregator.model.repository"
})
@SpringBootApplication
public class ProductAggregatorApplication {

    public static void main(String[] args) {
        YaciConfig.INSTANCE.setReturnTxBodyCbor(true);
        SpringApplication.run(ProductAggregatorApplication.class, args);
    }

}
