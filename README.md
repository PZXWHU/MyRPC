## EasyRPC
EasyRPC是不使用注册中心的RPC框架，即只支持client-server的调用模式。

![EasyRPC](images/EasyRpc.png)


## Server使用
- 自动扫描和发布服务(默认情况)
    1. 在服务实现类上标注注解@Service
    2. 在启动类上标注注解@ServiceScan
```java
@ServiceScan
public class TestNettyServer {

    public static void main(String[] args) {
        //SocketServer socketServer = new SocketServer();
        NettyServer server = new NettyServer();
        server.start(9999);
    }

}
```

- 手动发布服务
    1. 创建服务实现类实例
    2. 调用RpcServer的publishService方法，发布服务。
```java
public class TestNettyServer {

    public static void main(String[] args) {
        NettyServer server = new NettyServer(false);//autoScan = false，创建RpcServer不会自动扫描发布服务
        HelloService helloService = new HelloServiceImpl();
        server.publishService(helloService, HelloService.class.getCanonicalName());//服务名称必须使用全类名，否则客户端无法识别
        server.start(9999);
    }

}
```

当然也可以开启自动扫描后，再进行手动发布服务。


## client使用

   1. 创建RpcClient，输入Server的ip地址和端口
   2. 利用服务代理类，获得指定服务接口的代理对象
   3. 调用代理对象的方法，获得Rpc调用结果
```java
public class TestNettyClient {

    public static void main(String[] args) {
      
        RpcClient rpcClient = new NettyClient("127.0.0.1",9999);
        ServiceProxy serviceProxy = new ServiceProxy(rpcClient);
        HelloService helloService = serviceProxy.getProxy(HelloService.class);

        String res = helloService.hello(new HelloObject(12, "This is a message"));
        System.out.println(res);
    }
}
```