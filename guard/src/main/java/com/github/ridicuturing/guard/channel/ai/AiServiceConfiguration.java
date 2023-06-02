package com.github.ridicuturing.guard.channel.ai;

import org.springframework.stereotype.Component;

@Component
public class AiServiceConfiguration {


    /**
     * put ai model’s bean name and bean class in a enum will be easy to manage
     *
     * @param defaultListableBeanFactory defaultListableBeanFactory
     */
    //@PostConstruct
    /*public void initAiService(DefaultListableBeanFactory defaultListableBeanFactory) {
        for (Model model : Model.values()) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(model.getBeanClass());
            AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
            defaultListableBeanFactory.registerBeanDefinition(model.getServiceName(), beanDefinition);
        }
    }*/
}
