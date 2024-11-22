package dev.dharam.mykafka.services;

import dev.dharam.kafkacore.ProductCreatedEvent;
import dev.dharam.mykafka.models.CreateProductResModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public String createProduct(CreateProductResModel product) throws Exception{
        String productId = UUID.randomUUID().toString();

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
            productId,
                product.getTitle(),
                product.getPrice(),
                product.getQuantity()
        );

        //Async Operation
       CompletableFuture<SendResult<String, ProductCreatedEvent>> future=
               kafkaTemplate.send("product-created-topic-event",productId,productCreatedEvent);

       future.whenComplete((result, exception)->{
           if(exception != null){
                logger.error(" ******** Failed to send message!"+exception.getMessage());
           }else{
                logger.info(" ******** Message sent successfully"+result.getRecordMetadata());
           }
       });

        //Sync Operation
//        SendResult<String, ProductCreatedEvent> result=
//                kafkaTemplate.send("product-created-topic-event",productId,productCreatedEvent).get();
//
//        logger.info(String.valueOf(result.getRecordMetadata().partition()));
//        logger.info(result.getRecordMetadata().topic());

       //future.join();// this will block thread up to getting response, so it will work as synchronous task

        logger.info(" **** Returning product ID ****");
        return productId;
    }
}
