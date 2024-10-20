package com.restclient.restclient.factory;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Component
public class HttpInterfaceFactory {

    public <S> S createClient(Class<S> serviceClass, RestClient restclient) {
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restclient))
                                      .build()
                                      .createClient(serviceClass);
    }

}
