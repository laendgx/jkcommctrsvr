package com.boco.commrabbitmq;

import com.boco.rabbitmqcommconfig.DevRabbitmqCommInfo;
import com.boco.rabbitmqcommconfig.DevRabbitmqCommInfoDataServiceImpl;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.util.*;


/**
 * @Author : dong
 * @CreateTime : 2020/04/16
 * @Description :rabbitmq配置
 **/
@Setter
@Getter
@Configuration
public class RabbitmqConfig {
    private static final Logger log = LoggerFactory.getLogger(RabbitmqConfig.class);

    @Resource(name = "devcommInfoDataServiceImpl")
    private DevRabbitmqCommInfoDataServiceImpl devcommInfoDataServiceImpl; //设备配置信息

    @Autowired
    private Environment env;

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    /**
     * 单一消费者
     *
     * @return
     */
    @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainer() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return factory;
    }

    /**
     * 多个消费者
     *
     * @return
     */
    @Bean(name = "multiListenerContainer")
    public SimpleRabbitListenerContainerFactory multiListenerContainer() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory, connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        factory.setConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.concurrency", int.class));
        factory.setMaxConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.max-concurrency", int.class));
        factory.setPrefetchCount(env.getProperty("spring.rabbitmq.listener.prefetch", int.class));
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", exchange, routingKey, replyCode, replyText, message);
            }
        });
        return rabbitTemplate;
    }

    //     * 创建数据交换器
    //     * @return
    //     */
    @Bean
    DirectExchange DirectExchange() {
        return new DirectExchange(env.getProperty("jkexchangeName"));
    }

    /**
     * 创建数据处理队列
     *
     * @return
     */
    @Bean
    public Queue DataProcessDirectQueue() {
        return new Queue(env.getProperty("dataProcessQueueName"), true);  //true 是否持久
    }
    //绑定  将队列路由key和交换机绑定, 并设置用于匹配键：dataProcessQueueKey
    @Bean
    Binding bindingDataProcessDirect() {
        return BindingBuilder.bind(DataProcessDirectQueue()).to(DirectExchange()).with(env.getProperty("dataProcessQueueKey"));
    }

    /**
     * 创建监控通讯服务接收队列
     *
     * @return
     */
    @Bean
    public Queue DirectQueue() {
        Map<String,Object> map = new HashMap<>(16);
        map.put("x-message-ttl",10000);
        return new Queue(env.getProperty("jkcollInfoQueueName"),true,false,false,map);
        //return new Queue(env.getProperty("jkcollInfoQueueName"), true);  //true 是否持久
    }


    /**
     * 创建设备采集服务接收队列信息 Njjx
     *
     * @return
     */
    @Bean
    public Queue devCollrevDirectQueueNjjx() {
        Map<String,Object> map = new HashMap<>(16);
        map.put("x-message-ttl",10000);
        return new Queue(env.getProperty("devCollrevNjjxQueueName"),true,false,false,map);
       //return new Queue(env.getProperty("devCollrevNjjxQueueName"), true);  //true 是否持久
    }

    //绑定  将队列路由key和交换机绑定, Njjx
    @Bean
    Binding bindingDirectNjjx() {
        return BindingBuilder.bind(devCollrevDirectQueueNjjx()).to(DirectExchange()).with(env.getProperty("devCollrevNjjxQueueKey"));
    }

    /**
     * 创建设备采集服务接收队列信息 Shss
     *
     * @return
     */
    @Bean
    public Queue devCollrevDirectQueueShss() {
        Map<String,Object> map = new HashMap<>(16);
        map.put("x-message-ttl",10000);
        return new Queue(env.getProperty("devCollrevShssQueueName"),true,false,false,map);
    }

    //绑定  将队列路由key和交换机绑定, Shss
    @Bean
    Binding bindingDirectShss() {
        return BindingBuilder.bind(devCollrevDirectQueueShss()).to(DirectExchange()).with(env.getProperty("devCollrevShssQueueKey"));
    }

    /**
     * 创建设备采集服务接收队列信息 Gzfh
     *
     * @return
     */
    @Bean
    public Queue devCollrevDirectQueueGzfh() {
        Map<String,Object> map = new HashMap<>(16);
        map.put("x-message-ttl",10000);
        return new Queue(env.getProperty("devCollrevGzfhQueueName"),true,false,false,map);
    }

    //绑定  将队列路由key和交换机绑定, Gzfh
    @Bean
    Binding bindingDirectGzfh() {
        return BindingBuilder.bind(devCollrevDirectQueueGzfh()).to(DirectExchange()).with(env.getProperty("devCollrevGzfhQueueKey"));
    }

    /**
     * 创建设备采集服务接收队列信息 Szxk
     *
     * @return
     */
    @Bean
    public Queue devCollrevDirectQueueSzxk() {
        Map<String,Object> map = new HashMap<>(16);
        map.put("x-message-ttl",10000);
        return new Queue(env.getProperty("devCollrevSzxkQueueName"),true,false,false,map);
    }

    //绑定  将队列路由key和交换机绑定, Szxk
    @Bean
    Binding bindingDirectSzxk() {
        return BindingBuilder.bind(devCollrevDirectQueueSzxk()).to(DirectExchange()).with(env.getProperty("devCollrevSzxkQueueKey"));
    }

    /**
     * 创建设备采集服务接收队列信息 Tzdm
     *
     * @return
     */
    @Bean
    public Queue devCollrevDirectQueueTzdm() {
        Map<String,Object> map = new HashMap<>(16);
        map.put("x-message-ttl",10000);
        return new Queue(env.getProperty("devCollrevTzdmQueueName"),true,false,false,map);
    }

    //绑定  将队列路由key和交换机绑定, Tzdm
    @Bean
    Binding bindingDirectTzdm() {
        return BindingBuilder.bind(devCollrevDirectQueueTzdm()).to(DirectExchange()).with(env.getProperty("devCollrevTzdmQueueKey"));
    }



    //------------------------------------------下面为动态创建devCommConfig.xml文件下的队列和交换器以及路由key值 不成功------------------------
//    /**
//     * 创建队列
//     * @return
//     */
//    public Queue createDirectQueue(String queueName) {
//        return new Queue(queueName, true);  //true 是否持久
//    }
//
//    /**
//     * 创建消息队列
//     * @return
//     */
//    @Bean
//    public LinkedList<Queue> builderDevCollQueue() {
//        LinkedList<Queue> queueLinkeds = new LinkedList<Queue>();
//        try {
//            List<DevRabbitmqCommInfo> DevcommInfos = devcommInfoDataServiceImpl.getDevcommInfoList();
//            for (int k = 0; k < DevcommInfos.size(); k++) {
//                queueLinkeds.add(createDirectQueue(DevcommInfos.get(k).getQueueName()));
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            System.out.println("builderDevCollQueue异常" + ex.toString());
//        }
//        return queueLinkeds;
//    }
//
//    /**
//     * 查找队列
//     * @param queueLinkeds
//     * @param queueName
//     * @return
//     */
//    public Queue getQueue(LinkedList<Queue> queueLinkeds,String queueName){
//        Queue queue=null;
//        for (int i = 0; i < queueLinkeds.size(); i++) {
//            if (queueName.equals(queueLinkeds.get(i).getName()))
//                queue = queueLinkeds.get(i);
//        }
//        return queue;
//    }
//
//    /**
//     * 创建队列
//     * @return
//     */
//    public DirectExchange createExchange(String exchangeName) {
//        return new DirectExchange(exchangeName);
//    }
//
//    /**
//     * 创建消息队列
//     * @return
//     */
//    @Bean
//    public LinkedList<DirectExchange> builderExchange() {
//        LinkedList<DirectExchange> directExchanges = new LinkedList<DirectExchange>();
//        try {
//            List<DevRabbitmqCommInfo> DevcommInfos = devcommInfoDataServiceImpl.getDevcommInfoList();
//            for (int k = 0; k < DevcommInfos.size(); k++) {
//                directExchanges.add(createExchange(DevcommInfos.get(k).getExchangeName()));
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            System.out.println("builderExchange" + ex.toString());
//        }
//        return directExchanges;
//    }
//
//    /**
//     * 根据交换机名称查找交换器
//     * @param directExchanges
//     * @param directExchangeName
//     * @return
//     */
//    public DirectExchange getDirectExchange(LinkedList<DirectExchange> directExchanges,String directExchangeName){
//        DirectExchange directExchange=null;
//        for (int i = 0; i < directExchanges.size(); i++) {
//            if(directExchangeName.equals(directExchanges.get(i).getName()))
//                directExchange = directExchanges.get(i);
//        }
//        return directExchange;
//    }
//
//
//    //绑定  将队列路由key和交换机绑定, 并设置用于匹配键：devsvrQueueKey_1
//    @Bean
//    public List<Binding> bindingDirectExchangeDevCollrevQueueKeyList(
//            LinkedList<DirectExchange> directExchanges,LinkedList<Queue> queueLinkeds) {
//        List<Binding> bingings = new ArrayList<Binding>();
//        try {
//            List<DevRabbitmqCommInfo> DevcommInfos = devcommInfoDataServiceImpl.getDevcommInfoList();
//            Binding binding =null;
//            for (int k = 0; k < DevcommInfos.size(); k++) {
//                String queueName= DevcommInfos.get(k).getQueueName();
//                String queueRoutingKey=DevcommInfos.get(k).getQueueRoutingKey();
//                String directExchange=DevcommInfos.get(k).getExchangeName();
//                binding =BindingBuilder.bind(getQueue(queueLinkeds,queueName)).to(
//                        getDirectExchange(directExchanges,directExchange)).with(queueRoutingKey);
//                bingings.add(binding);
//                log.info("正在绑定队列"+DevcommInfos.get(k).getQueueName()+"键值"+queueRoutingKey+"到交换器"+env.getProperty("exchangeName"));
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            System.out.println("bindingDirectExchangeDevCollrevQueueKeyList异常"+ex.toString());
//        }
//        return bingings;
//    }


}