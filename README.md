```xml
zuul:
  routes:
    # 将cloud-provider-product服务的接口映射为product-proxy
    cloud-provider-product: /product-proxy/**
  # 屏蔽到原始的访问方式
  ignored-services:
    "*"
    
# 第二种方式
zuul:
  routes:
    product1:
        path: /product-proxy/**
        serviceId: cloud-provider-product
    product2:
        path: /product2-proxy/**
        url: http://localhost:8090/
  ignored-services:
    "*"
```