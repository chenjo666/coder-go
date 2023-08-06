package com.cj.codergobackend;

import com.cj.codergobackend.constants.ArticleType;
import com.cj.codergobackend.pojo.Article;
import com.cj.codergobackend.service.ArticleCommentService;
import com.cj.codergobackend.service.ArticleService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootTest(classes = {CoderGoBackendApplication.class})
public class ArticleServiceTests {
    @Resource
    ArticleService articleService;

    @Test
    public void testSearchArticlesByMySQL() {
        System.out.println();
    }

    public Date randomDate() {
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime randomTime = now.minusDays(random.nextInt(365)).minusHours(random.nextInt(24))
                .minusMinutes(random.nextInt(60)).minusSeconds(random.nextInt(60));
        return Date.from(randomTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private static final Long[] userIds = new Long[]{
            1677640320653533186L, 1676909673454575647L, 1676909673454575618L
    };
    @Test
    public void saveBatch() {
        String[] titles = new String[]{"CAP理论", "Netty核心组件",
                "vue基础功能-基础语法（1）", "vue基础功能-计算属性（2）", "vue基础功能-监听属性（3）", "vue基础功能-属性绑定（4）",
                "vue基础功能-表单绑定（5）", "vue基础功能-结构渲染（6）", "vue基础功能-时间处理（7）",
                "NIO编程", "单例模式"
        };


        String[] content = new String[]{"### CAP 理论\n" +
                "\n" +
                "起源：CAP 理论是加州理工大学伯克利分校的 Eric Brewer 教授在 2000 年 7 月的 ACM PODC 会议上首次提出的，它是 Eric Brewer 在 Inktomi 期间研发搜索引擎、分布式 Web 缓存时得出的关于数据一致性（ C：Consistency ）、服务可用性（ A：Availability ）、分区容错性（ P：Partition-tolerance ）的一个著名猜想：\n" +
                "\n" +
                "> It is impossible for a web service to provide the three following guarantees :Consistency, Availability and Partition-tolerance.\n" +
                "> \n" +
                "\n" +
                "也就是说\n" +
                "\n" +
                "概念：\n" +
                "\n" +
                "- 一致性（Consistency）：指\"*all nodes see the same data at the same time*\"，即所有节点在同一时间的数据完全一致。\n" +
                "- 可用性（Availablility）：可用性指\"*reads and writes always succeed*\"，即要求系统内的节点们接收到了无论是写请求还是读请求，都要能处理并给回响应结果。\n" +
                "- 分区容错性（Partition tolerance）：分区容错性指\"*the system continues to operate despite arbitrary message loss or failure of part of the system*\"，即分布式系统在遇到某节点或网络分区故障的时候，仍然能够对外提供满足一致性或可用性的服务。\n" +
                "\n" +
                "在分布式系统中，P 原则是一定需要满足的，如果不不满足 P，这就意味着不能提供一致性和可用性服务，同时 CAP 全不满足。\n" +
                "\n" +
                "简而言之，P 原则必须满足，在此基础还可以满足 C 原则或 A 原则，俗称 CP 原则和 AP 原则。\n" +
                "\n" +
                "C 原则强调的是数据一致性，确保各节点之间数据一定相同。由于网络传输数据过程中存在传播时间（哪怕是万分之一秒），这就会导致数据同步过程中必然会出现数据不一致的情况，（按理来说 C 原则是一定满足不了的）。如果我们必须要满足 C 原则，则必须暂停 A 原则，等到数据一致时\n" +
                "\n" +
                "A 原则强调的是服务可用性，确保读请求和写请求一定成功。由于节点搭建在计算机硬件上，可能会出现硬件设备或网络故障导致服务不可用的情况，如果我们必须要满足 A 原则，那么此时必须舍弃 C 原则，依然向外提供服务，但是允许数据不一致的情况出现。\n" +
                "\n" +
                "按照上述所示，如果读请求到来，似乎三个原则都能满足，如果写请求到来，C 原则不可能满足，A 原则才可能满足。", "## 核心组件\n" +
                "\n" +
                "### Channel - 数据通道\n" +
                "\n" +
                "Netty 中的 Channel 和 Nio 中的 Channel 类似，用于表示一个网络连接后的数据通道，并且最常用的为 NioServerSokcetChannel 和 NioSocketChannel，用于 TCP 连接。\n" +
                "\n" +
                "Netty 提供了一个 EmbeddedChannel 类用于单元测试。\n" +
                "\n" +
                "- 【示例 1】使用 NioServerSocketChannel 和 NioSocketChannel 构建 TCP 连接的数据通道，连接后，客户端向服务器发送 “bye bye”，服务器接收并输出显示。\n" +
                "    - TCPServer.java\n" +
                "        \n" +
                "        ```java\n" +
                "        import io.netty.bootstrap.ServerBootstrap;\n" +
                "        import io.netty.channel.ChannelHandlerContext;\n" +
                "        import io.netty.channel.ChannelInboundHandlerAdapter;\n" +
                "        import io.netty.channel.ChannelInitializer;\n" +
                "        import io.netty.channel.nio.NioEventLoopGroup;\n" +
                "        import io.netty.channel.socket.nio.NioServerSocketChannel;\n" +
                "        import io.netty.channel.socket.nio.NioSocketChannel;\n" +
                "        import io.netty.handler.codec.string.StringDecoder;\n" +
                "        \n" +
                "        public class TCPServer {\n" +
                "            public static void main(String[] args) {\n" +
                "                new ServerBootstrap()\n" +
                "                        .group(new NioEventLoopGroup())\n" +
                "                        .channel(NioServerSocketChannel.class)\n" +
                "                        .childHandler(\n" +
                "                                new ChannelInitializer<NioSocketChannel>() {\n" +
                "                                    // 6. 初始化 channel\n" +
                "                                    @Override\n" +
                "                                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {\n" +
                "                                        // 解编码器\n" +
                "                                        nioSocketChannel.pipeline().addLast(new StringDecoder());\n" +
                "                                        // 自定义通道处理器\n" +
                "                                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {\n" +
                "                                            @Override\n" +
                "                                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {\n" +
                "                                                System.out.println(\"Message from client: \" + msg);\n" +
                "                                            }\n" +
                "                                        });\n" +
                "                                    }\n" +
                "                                })\n" +
                "                        .bind(9000);\n" +
                "            }\n" +
                "        }\n" +
                "        ```\n" +
                "        \n" +
                "    - TCPClient.java\n" +
                "        \n" +
                "        ```java\n" +
                "        import io.netty.bootstrap.Bootstrap;\n" +
                "        import io.netty.channel.ChannelInitializer;\n" +
                "        import io.netty.channel.nio.NioEventLoopGroup;\n" +
                "        import io.netty.channel.socket.nio.NioSocketChannel;\n" +
                "        import io.netty.handler.codec.string.StringEncoder;\n" +
                "        \n" +
                "        import java.net.InetSocketAddress;\n" +
                "        \n" +
                "        public class TCPClient {\n" +
                "            public static void main(String[] args) throws InterruptedException {\n" +
                "                new Bootstrap()\n" +
                "                        .group(new NioEventLoopGroup())\n" +
                "                        .channel(NioSocketChannel.class)\n" +
                "                        .handler(new ChannelInitializer<NioSocketChannel>() {\n" +
                "                            @Override\n" +
                "                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {\n" +
                "                                nioSocketChannel.pipeline().addLast(new StringEncoder());\n" +
                "                            }\n" +
                "                        })\n" +
                "                        .connect(new InetSocketAddress(\"localhost\", 9000))\n" +
                "                        .sync()\n" +
                "                        .channel()\n" +
                "                        .writeAndFlush(\"bye bye\");\n" +
                "            }\n" +
                "        }\n" +
                "        ```\n" +
                "        \n" +
                "- 【示例 2】构建 UDP 通信…\n" +
                "    \n" +
                "    ```java\n" +
                "    \n" +
                "    ```\n" +
                "    \n" +
                "- 【示例 3】使用 EmbeddedChannel 测试 InBoundHandler 的使用。\n" +
                "    \n" +
                "    ```java\n" +
                "    import io.netty.channel.ChannelHandlerContext;\n" +
                "    import io.netty.channel.ChannelInboundHandlerAdapter;\n" +
                "    import io.netty.channel.embedded.EmbeddedChannel;\n" +
                "    \n" +
                "    public class TestEmbeddedChannel {\n" +
                "        public static void main(String[] args) {\n" +
                "            ChannelInboundHandlerAdapter h1 = new ChannelInboundHandlerAdapter() {\n" +
                "                @Override\n" +
                "                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {\n" +
                "                    System.out.println(msg);\n" +
                "                    super.channelRead(ctx, msg);\n" +
                "                }\n" +
                "            };\n" +
                "            ChannelInboundHandlerAdapter h2 = new ChannelInboundHandlerAdapter() {\n" +
                "                @Override\n" +
                "                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {\n" +
                "                    System.out.println(msg);\n" +
                "                    super.channelRead(ctx, msg);\n" +
                "                }\n" +
                "            };\n" +
                "            ChannelInboundHandlerAdapter h3 = new ChannelInboundHandlerAdapter() {\n" +
                "                @Override\n" +
                "                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {\n" +
                "                    System.out.println(msg);\n" +
                "                    super.channelRead(ctx, msg);\n" +
                "                }\n" +
                "            };\n" +
                "    \n" +
                "            EmbeddedChannel embeddedChannel = new EmbeddedChannel(h1, h2, h3);\n" +
                "            embeddedChannel.writeInbound(\"hello\");\n" +
                "        }\n" +
                "    }\n" +
                "    ```\n" +
                "    \n" +
                "\n" +
                "【注意】Channel 提供了 write() 方法向数据通道中写入数据，但是并不会真正写入进数据通道，而是在缓冲区中，使用 flush() 方法才能真正写入数据通道，另外，可以使用 writeAndFlush() 方法一口气写入数据通道。\n" +
                "\n" +
                "### EventLoop - 事件循环\n" +
                "\n" +
                "`EventLoop` 是一个事件循环处理机，负责处理各种事件，包括连接事件、读写请求事件等。\n" +
                "\n" +
                "具体来说，EventLoop 是一个接口，它具有以下几种实现类：\n" +
                "\n" +
                "- `DefaultEventLoop`：继承至 SingleThreadEventLoop 类，以 BIO （默认）模型进行事件循环处理。\n" +
                "- `NioEventLoop`：继承至 SingleThreadEventLoop 类，以 NIO 模型进行事件循环处理。\n" +
                "- `EmbeddedEventLoop`：主要用于进行单元测试。\n" +
                "- `EpollEventLoop`：基于 linux 操作系统的实现。\n" +
                "- `KQueueEventLoop`：基于 BSD 操作系统的实现。\n" +
                "\n" +
                "这些实现类本质上是一个单线程，其中的 run() 方法用来不停的处理 Channel 上的的事件。\n" +
                "\n" +
                "`EventLoopGroup` 用于管理一组 EventLoop 对象，当 Channel 上发生事件后，它会调用 EventLoopGroup 的 register() 方法将这个事件与其中一个 EventLoop 对象进行绑定，以后发生该事件时由该 EventLoop 对象进行处理，这样做的好处是保证了线程安全。\n" +
                "\n" +
                "EventLoop 接口还实现了 `ScheduledExecutorService` 接口  ，因此可以实现普通任务和定时任务。而 NioEventLoop 由于拓展了 EventLoop（内置 Selector），因此它还可以处理  IO 事件。\n" +
                "\n" +
                "需要注意的是：每一个 EventLoop 都本质上是一个线程，如果需要关闭运行的客户端，则需要关闭这些 EventLoop，EventLoopGroup 提供了一个 `shutdownGracefully()` 统一停止掉所有 EventLoop。\n" +
                "\n" +
                "- 【示例 1】使用 DefaultEventLoop 实现一个普通任务，输出 ”hello, world”。\n" +
                "    \n" +
                "    ```java\n" +
                "    import io.netty.channel.DefaultEventLoop;\n" +
                "    \n" +
                "    public class TestEventLoop {\n" +
                "        public static void main(String[] args) {\n" +
                "            DefaultEventLoop eventLoop = new DefaultEventLoop();\n" +
                "            eventLoop.submit(() -> {\n" +
                "                System.out.println(Thread.currentThread().getName() + \": hello, world\");\n" +
                "            });\n" +
                "            System.out.println(Thread.currentThread().getName() + \": completed\");\n" +
                "        }\n" +
                "    }\n" +
                "    ```\n" +
                "    \n" +
                "- 【示例 2】使用 DefaultEventLoop 实现一个定时任务，每 1 秒输出一个 “hello”。\n" +
                "    \n" +
                "    ```java\n" +
                "    import io.netty.channel.DefaultEventLoop;\n" +
                "    import java.util.concurrent.TimeUnit;\n" +
                "    \n" +
                "    public class TestEventLoopTimerTask {\n" +
                "        public static void main(String[] args) {\n" +
                "            DefaultEventLoop eventLoop = new DefaultEventLoop();\n" +
                "            eventLoop.scheduleWithFixedDelay(() -> {\n" +
                "                System.out.println(\"hello\");\n" +
                "            }, 1, 1, TimeUnit.SECONDS);\n" +
                "        }\n" +
                "    }\n" +
                "    ```\n" +
                "    \n" +
                "- 【示例 3】使用 NioEventLoop 处理客户端请求，客户端不断输入，服务器不断接收数据并输出显示，客户端通过输入 “bye” 停止输出给服务器，并停止客户端的运行。\n" +
                "    - Server.java\n" +
                "        \n" +
                "        ```java\n" +
                "        public class Server {\n" +
                "            public static void main(String[] args) {\n" +
                "                new ServerBootstrap()\n" +
                "                        .group(new NioEventLoopGroup())\n" +
                "                        .channel(NioServerSocketChannel.class)\n" +
                "                        .childHandler(new ChannelInitializer<NioSocketChannel>() {\n" +
                "                            @Override\n" +
                "                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {\n" +
                "                                nioSocketChannel.pipeline().addLast(new StringDecoder());\n" +
                "                                nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {\n" +
                "                                    @Override\n" +
                "                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {\n" +
                "                                        System.out.println(\"msg: \" + msg);\n" +
                "                                    }\n" +
                "                                });\n" +
                "                            }\n" +
                "                        })\n" +
                "                        .bind(9000);\n" +
                "            }\n" +
                "        }\n" +
                "        ```\n" +
                "        \n" +
                "    - Clinet.java\n" +
                "        \n" +
                "        ```java\n" +
                "        import io.netty.bootstrap.Bootstrap;\n" +
                "        import io.netty.channel.Channel;\n" +
                "        import io.netty.channel.ChannelFuture;\n" +
                "        import io.netty.channel.ChannelFutureListener;\n" +
                "        import io.netty.channel.ChannelInitializer;\n" +
                "        import io.netty.channel.nio.NioEventLoopGroup;\n" +
                "        import io.netty.channel.socket.nio.NioSocketChannel;\n" +
                "        import io.netty.handler.codec.string.StringEncoder;\n" +
                "        \n" +
                "        import java.net.InetSocketAddress;\n" +
                "        import java.util.Scanner;\n" +
                "        \n" +
                "        public class Client {\n" +
                "            public static void main(String[] args) throws InterruptedException {\n" +
                "                NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();\n" +
                "        \n" +
                "                ChannelFuture channelFuture = new Bootstrap()\n" +
                "                        .group(eventLoopGroup)\n" +
                "                        .channel(NioSocketChannel.class)\n" +
                "                        .handler(new ChannelInitializer<NioSocketChannel>() {\n" +
                "                            @Override\n" +
                "                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {\n" +
                "                                nioSocketChannel.pipeline().addLast(new StringEncoder());\n" +
                "                            }\n" +
                "                        })\n" +
                "                        .connect(new InetSocketAddress(\"localhost\", 9000));\n" +
                "        \n" +
                "                // 同步阻塞\n" +
                "                channelFuture.sync();\n" +
                "                Channel channel = channelFuture.channel();\n" +
                "                new Thread(() -> {\n" +
                "                    Scanner scanner = new Scanner(System.in);\n" +
                "                    while (true) {\n" +
                "                        String input = scanner.nextLine();\n" +
                "                        if (\"bye\".equals(input)) {\n" +
                "                            channel.close();\n" +
                "                            break;\n" +
                "                        }\n" +
                "                        channel.writeAndFlush(input);\n" +
                "                    }\n" +
                "                }).start();\n" +
                "        \n" +
                "                // 停止掉程序\n" +
                "                ChannelFuture closeFuture = channel.closeFuture();\n" +
                "                closeFuture.addListener(new ChannelFutureListener() {\n" +
                "                    @Override\n" +
                "                    public void operationComplete(ChannelFuture channelFuture) throws Exception {\n" +
                "                        eventLoopGroup.shutdownGracefully();\n" +
                "                        System.out.println(\"close eventLoopGroup...\");\n" +
                "                    }\n" +
                "                });\n" +
                "            }\n" +
                "        }\n" +
                "        ```\n" +
                "        \n" +
                "\n" +
                "### Future & Promise 异步结果\n" +
                "\n" +
                "JUC 下提供了一个 Future 类用来获取异步操作的结果，但是这个类功能微弱，只有 cancel()、isCancelled()、isDone()、get() 四种类型的方法，只能进行同步阻塞似的获取异步操作结果，难以满足异步处理，因此 Netty 也提供了一个 Future 类继承了 Jdk 下的 Future 类，在它的基础上添加了很多方法，例如：\n" +
                "\n" +
                "- `isSuccess()` ：判断异步操作是否成功。\n" +
                "- `isCancellable()`：判断异步操作是否被取消。\n" +
                "- `sync()/syncUninterruptibly()` ：用于同步阻塞。\n" +
                "- `addListener()/removeListener()` ：添加监听器，用于异步回调。\n" +
                "- `getNow()`：非阻塞获取当前操作结果。\n" +
                "\n" +
                "Netty 不满足 Future 【被动】设置异步操作结果功能，又提供了 Promise 类继承 Future 类，在此基础上增加了两个核心方法用于【主动】设置操作结果：\n" +
                "\n" +
                "- `setSuccess()`：设置成功结果。\n" +
                "- `setFailure()`：设置失败结果。\n" +
                "- 【示例 1】Future 的应用。使用 jdk 的 get() 同步阻塞方法获取异常结果。\n" +
                "    \n" +
                "    ```java\n" +
                "    import io.netty.channel.DefaultEventLoop;\n" +
                "    import io.netty.util.concurrent.Future;\n" +
                "    \n" +
                "    import java.util.concurrent.ExecutionException;\n" +
                "    \n" +
                "    public class TestFutureGet {\n" +
                "        public static void main(String[] args) throws ExecutionException, InterruptedException {\n" +
                "            DefaultEventLoop eventLoop = new DefaultEventLoop();\n" +
                "    \n" +
                "            Future<?> future = eventLoop.submit(() -> {\n" +
                "                Thread.sleep(1000);\n" +
                "                return 200;\n" +
                "            });\n" +
                "    \n" +
                "            System.out.println(future.get());\n" +
                "            eventLoop.shutdownGracefully();\n" +
                "        }\n" +
                "    }\n" +
                "    ```\n" +
                "    \n" +
                "- 【示例 2】Future 的应用。使用 Netty 的 addListener() 方法设置监听器，进行异步回调方式输出异步操作结果。\n" +
                "    \n" +
                "    ```java\n" +
                "    import io.netty.channel.DefaultEventLoop;\n" +
                "    import io.netty.util.concurrent.Future;\n" +
                "    import io.netty.util.concurrent.GenericFutureListener;\n" +
                "    \n" +
                "    public class TestFutureListener {\n" +
                "        public static void main(String[] args) {\n" +
                "            DefaultEventLoop eventLoop = new DefaultEventLoop();\n" +
                "    \n" +
                "            Future<Integer> future = eventLoop.submit(() -> {\n" +
                "                Thread.sleep(1000);\n" +
                "                return 200;\n" +
                "            });\n" +
                "    \n" +
                "            future.addListener(new GenericFutureListener<Future<? super Integer>>() {\n" +
                "                @Override\n" +
                "                public void operationComplete(Future<? super Integer> future) throws Exception {\n" +
                "                    System.out.println(future.getNow());\n" +
                "                }\n" +
                "            });\n" +
                "            eventLoop.shutdownGracefully();\n" +
                "        }\n" +
                "    }\n" +
                "    ```\n" +
                "    \n" +
                "- 【示例 3】Promise 的应用。使用一个线程处理一个除法运算，接收两个参数，如果操作失败则返回一个异常结果，否则返回一个成功消息。\n" +
                "    \n" +
                "    ```java\n" +
                "    import io.netty.channel.EventLoop;\n" +
                "    import io.netty.channel.nio.NioEventLoopGroup;\n" +
                "    import io.netty.util.concurrent.DefaultPromise;\n" +
                "    import io.netty.util.concurrent.Promise;\n" +
                "    \n" +
                "    import java.util.Scanner;\n" +
                "    import java.util.concurrent.ExecutionException;\n" +
                "    \n" +
                "    public class TestPromise {\n" +
                "        public static void main(String[] args) throws ExecutionException, InterruptedException {\n" +
                "            Scanner sc = new Scanner(System.in);\n" +
                "    \n" +
                "            EventLoop eventLoop = new NioEventLoopGroup().next();\n" +
                "            Promise<String> promise = new DefaultPromise<String>(eventLoop);\n" +
                "            int a = sc.nextInt(), b = sc.nextInt();\n" +
                "            new Thread(() -> {\n" +
                "                try {\n" +
                "                    Thread.sleep(1000);\n" +
                "                    int c = a / b;\n" +
                "                    promise.setSuccess(\"操作成功！\");\n" +
                "                } catch (Exception e) {\n" +
                "                    promise.setFailure(e);\n" +
                "                }\n" +
                "            }).start();\n" +
                "    \n" +
                "            System.out.println(promise.get());\n" +
                "        }\n" +
                "    }\n" +
                "    ```\n" +
                "    \n" +
                "\n" +
                "### ChannelPipeline & ChannelHandler 流水工人\n" +
                "\n" +
                "如果说数据是一个流水线上的一个原材料，那么 ChannelPipeline 指的就是这条流水线，而 ChannelHandler 指的就是工人了，一条流水线上具有多个工人来处理同一个数据，每个工人既可以传给后面的工人进行进一步处理数据，也可以不传入。最终会得到一个产品。\n" +
                "\n" +
                "- ChannelPipeline：负责添加、删除工人，也负责处理 Channel 上的数据。\n" +
                "- ChannelHandler：负责对数据进行处理，也能够将数据给其它 ChannelHandler 进行处理。\n" +
                "\n" +
                "其中 Pipeline 和 Handler 配合的工作示意图如下所示：\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/60f176ff-f0a1-42c6-97da-69b2cf50cc02/Untitled.png)\n" +
                "\n" +
                "其中 Read() 会触发 Inbound 的执行且传播是从前往后开始，而 Write() 会触发 Outbound 的执行且传播是从后往前开始。\n" +
                "\n" +
                "对于 write() 方法，需要注意：\n" +
                "\n" +
                "- `NioSocketChannel` 的 write() 方法会调用最后一个 Inbound 执行\n" +
                "- `ChannelHandlerContext` 的 write() 方法会调用当前 Handler 之前的 Outbound 执行。\n" +
                "\n" +
                "`ChannelDuplexHandler` 是 Netty 中的一个特殊类型的 ChannelHandler，它可以同时处理 inbound（入站）和 outbound（出站）事件。\n" +
                "\n" +
                "`ChannelDuplexHandler` 继承了 `ChannelInboundHandlerAdapter` 和 `ChannelOutboundHandlerAdapter`，因此它既可以作为入站处理器处理 inbound 事件，也可以作为出站处理器处理 outbound 事件。\n" +
                "\n" +
                "当你需要同时处理入站和出站事件时，可以使用 `ChannelDuplexHandler`，而无需单独编写两个处理器。\n" +
                "\n" +
                "- 【示例】观察 Pipeline 中 Inbound 和 Outbound 的执行顺序。\n" +
                "    - Server.java\n" +
                "        \n" +
                "        ```java\n" +
                "        import io.netty.bootstrap.ServerBootstrap;\n" +
                "        import io.netty.channel.*;\n" +
                "        import io.netty.channel.nio.NioEventLoopGroup;\n" +
                "        import io.netty.channel.socket.nio.NioServerSocketChannel;\n" +
                "        import io.netty.channel.socket.nio.NioSocketChannel;\n" +
                "        import io.netty.handler.codec.string.StringDecoder;\n" +
                "        \n" +
                "        public class Server {\n" +
                "            public static void main(String[] args) {\n" +
                "                new ServerBootstrap()\n" +
                "                        .group(new NioEventLoopGroup())\n" +
                "                        .channel(NioServerSocketChannel.class)\n" +
                "                        .childHandler(\n" +
                "                                new ChannelInitializer<NioSocketChannel>() {\n" +
                "                                    @Override\n" +
                "                                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {\n" +
                "                                        // 解编码器\n" +
                "                                        nioSocketChannel.pipeline().addLast(new StringDecoder());\n" +
                "                                        nioSocketChannel.pipeline().addLast(\"handler1\", new ChannelInboundHandlerAdapter() {\n" +
                "                                            @Override\n" +
                "                                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {\n" +
                "                                                System.out.println(\"handler1: \" + msg);\n" +
                "                                                super.channelRead(ctx, msg);\n" +
                "                                            }\n" +
                "                                        });\n" +
                "                                        nioSocketChannel.pipeline().addLast(\"handler2\", new ChannelInboundHandlerAdapter() {\n" +
                "                                            @Override\n" +
                "                                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {\n" +
                "                                                System.out.println(\"handler2: \" + msg);\n" +
                "                                                super.channelRead(ctx, msg);\n" +
                "                                            }\n" +
                "                                        });\n" +
                "                                        nioSocketChannel.pipeline().addLast(\"handler3\", new ChannelInboundHandlerAdapter() {\n" +
                "                                            @Override\n" +
                "                                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {\n" +
                "                                                System.out.println(\"handler3: \" + msg);\n" +
                "                                                super.channelRead(ctx, msg);\n" +
                "                                                nioSocketChannel.writeAndFlush(msg);\n" +
                "                                            }\n" +
                "                                        });\n" +
                "                                        nioSocketChannel.pipeline().addLast(\"handler4\", new ChannelOutboundHandlerAdapter() {\n" +
                "                                            @Override\n" +
                "                                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {\n" +
                "                                                System.out.println(\"handler4: \" + msg);\n" +
                "                                                super.write(ctx, msg, promise);\n" +
                "                                            }\n" +
                "                                        });\n" +
                "                                        nioSocketChannel.pipeline().addLast(\"handler5\", new ChannelOutboundHandlerAdapter() {\n" +
                "                                            @Override\n" +
                "                                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {\n" +
                "                                                System.out.println(\"handler5: \" + msg);\n" +
                "                                                super.write(ctx, msg, promise);\n" +
                "                                            }\n" +
                "                                        });\n" +
                "                                        nioSocketChannel.pipeline().addLast(\"handler6\", new ChannelOutboundHandlerAdapter() {\n" +
                "                                            @Override\n" +
                "                                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {\n" +
                "                                                System.out.println(\"handler6: \" + msg);\n" +
                "                                                super.write(ctx, msg, promise);\n" +
                "                                            }\n" +
                "                                        });\n" +
                "                                        nioSocketChannel.pipeline().addLast(\"handler7\", new ChannelInboundHandlerAdapter() {\n" +
                "                                            @Override\n" +
                "                                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {\n" +
                "                                                System.out.println(\"handler7: \" + msg);\n" +
                "                                                super.channelRead(ctx, msg);\n" +
                "                                            }\n" +
                "                                        });\n" +
                "                                    }\n" +
                "                                })\n" +
                "                        .bind(9000);\n" +
                "            }\n" +
                "        }\n" +
                "        ```\n" +
                "        \n" +
                "    - Client.java\n" +
                "        \n" +
                "        ```java\n" +
                "        package netty.handler_pipeline;\n" +
                "        \n" +
                "        import io.netty.bootstrap.Bootstrap;\n" +
                "        import io.netty.channel.ChannelInitializer;\n" +
                "        import io.netty.channel.nio.NioEventLoopGroup;\n" +
                "        import io.netty.channel.socket.nio.NioSocketChannel;\n" +
                "        import io.netty.handler.codec.string.StringEncoder;\n" +
                "        \n" +
                "        import java.net.InetSocketAddress;\n" +
                "        \n" +
                "        public class Client {\n" +
                "            public static void main(String[] args) throws InterruptedException {\n" +
                "                new Bootstrap()\n" +
                "                        .group(new NioEventLoopGroup())\n" +
                "                        .channel(NioSocketChannel.class)\n" +
                "                        .handler(new ChannelInitializer<NioSocketChannel>() {\n" +
                "                            @Override\n" +
                "                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {\n" +
                "                                nioSocketChannel.pipeline().addLast(new StringEncoder());\n" +
                "                            }\n" +
                "                        })\n" +
                "                        .connect(new InetSocketAddress(\"localhost\", 9000))\n" +
                "                        .sync()\n" +
                "                        .channel()\n" +
                "                        .writeAndFlush(\"hello\");\n" +
                "            }\n" +
                "        }\n" +
                "        ```\n" +
                "        \n" +
                "\n" +
                "### ByteBuf - 缓冲区\n" +
                "\n" +
                "在 jdk 提供的 ByteBuffer 中，它表示一个字节缓冲区，而 Netty 也提供了一个类似的 ByteBuf，也是一个字节缓冲区。\n" +
                "\n" +
                "由于 ByteBuf 是一个字节缓冲区，经常被使用，因此对于它的内存管理是一个重要的方面，Netty 采用了引用计数法、池化、零拷贝、直接内存等技术来管理和优化 ByteBuf 的内存使用。\n" +
                "\n" +
                "- **引用计数**：ByteBuf 实现了 `ReferenceCounted` 接口，该接口提供了两个方法 release()、retain() 用于记录 ByteBuf 对象被引用的次数，初始化时 ByteBuf 的次数为 1，调用 release() 方法时减 1，调用 retain() 方法时加 1，一旦 ByteBuf 的引用次数为 0，则该内存会被回收。\n" +
                "- **池化**：ByteBuf 有一个 `PooledByteBuf` 子抽象类，该抽象类中维护了一系列属性帮组我们进行池化。而且ByteBuf 通过 ByteBufAllocator 对象来进行管理内存，其中，池化的 ByteBuf 使用 PooledByteBufAllocator 进行分配，而非池化的 ByteBuf 使用 UnpooledByteBufAllocator 进行分配。\n" +
                "    - 4.1 之前，默认使用非池化技术\n" +
                "    - 4.1 及其之后，安卓平台采用非池化技术，其它平台采用池化技术。\n" +
                "- **零拷贝**：通过对数据的引用或共享方式来传递数据，而不是直接进行物理内存上的数据复制。具体有下列几种：\n" +
                "    - 切片（Slice）：ByteBuf 具有一个 `slice()` 方法可以将原始 ByteBuf 进行切片，它们具有共同的内存引用，这样可以避免数据的物理复制，而只是创建了一个新的 ByteBuf 视图。\n" +
                "    - 组合（Compsoite）：Netty 同时提供了 `CompositeByteBuf` 将多个 ByteBuf 进行合并从而共享内存引用。\n" +
                "- **直接内存**：Netty 提供了 `DirectByteBuf` 类型的 ByteBuf，直接内存可以直接从磁盘、网络等中读取或写入，而不需要在 java 堆和直接内存中进行数据拷贝，减少了拷贝开销，提高 IO、GC 效率。\n" +
                "    - 直接内存不在堆内存中，无法受 GC 管理，而我们可以调用 release() 方法手动管理这部分内存，减少内存消耗。\n" +
                "- 【示例 1】ByteBuf 的引用计数使用示例。\n" +
                "    \n" +
                "    ```java\n" +
                "    import io.netty.buffer.ByteBuf;\n" +
                "    import io.netty.buffer.ByteBufAllocator;\n" +
                "    \n" +
                "    public class TestByteBufRefCnt {\n" +
                "        public static void main(String[] args) {\n" +
                "            // 初始时，buffer's refCnt = 1\n" +
                "            ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();\n" +
                "            // 减一引用\n" +
                "            buffer.release();\n" +
                "            System.out.println(buffer.refCnt() == 0);\n" +
                "        }\n" +
                "    }\n" +
                "    ```\n" +
                "    \n" +
                "- 【示例 2】ByteBuf 的零拷贝 slice() 使用示例。", "# 1 指令\n" +
                "\n" +
                "## 1.1 指令语法\n" +
                "\n" +
                "> 指令是指 HTML 标签中带有 `v-` 前缀的特殊属性。\n" +
                "> \n" +
                "\n" +
                "指令的语法格式如下：\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/4fbc5c65-70cd-4688-8df1-871fcd1889ec/Untitled.png)\n" +
                "\n" +
                "各参数说明：\n" +
                "\n" +
                "- Name：指令的名称。如 v-if，v-show 指令等。\n" +
                "- Argument：指令的参数。\n" +
                "- Modifiers：指令的修饰符。\n" +
                "- Value：指令的值，通常是一个 JS 表达式。\n" +
                "\n" +
                "## 1.2 指令参数\n" +
                "\n" +
                "根据指令参数是否可变，可以将指令的参数分为：\n" +
                "\n" +
                "- 静态参数\n" +
                "- 动态参数\n" +
                "\n" +
                "### 1.2.1 静态参数\n" +
                "\n" +
                "静态参数是指令名称后面使用 `:` 分隔的无括号参数。\n" +
                "\n" +
                "```html\n" +
                "<a v-bind:href=\"url\"> ... </a>\n" +
                "\n" +
                "<!-- v-bind指令的简写形式 -->\n" +
                "<a :href=\"url\"> ... </a>\n" +
                "```\n" +
                "\n" +
                "这里的 href 参数将 url 的值绑定在 v-bind 指令的 href 参数上。\n" +
                "\n" +
                "---\n" +
                "\n" +
                "```html\n" +
                "<a v-on:click=\"doSomething\"> ... </a>\n" +
                "<!-- v-on指令的简写方式为@，因此下面这种形式可以 -->\n" +
                "<a @click=\"doSomething\">...</a>\n" +
                "```\n" +
                "\n" +
                "这里的 click 参数将 doSomething 的值绑定在 v-on 指令的 click 参数上。\n" +
                "\n" +
                "### 1.2.2 动态参数\n" +
                "\n" +
                "动态参数是指令名称后面使用 : 分隔的带 `[]` 参数。\n" +
                "\n" +
                "```html\n" +
                "<a v-bind:[arg]=\"url\"> ... </a>\n" +
                "<!-- 这里的arg参数是一个动态的JS表达式，其最终的值会随着JS表达式计算的值而变化 -->\n" +
                "```\n" +
                "\n" +
                "注意：\n" +
                "\n" +
                "1. 动态参数取值是字符串或者 null，当为 null 时，意为显式移除该绑定。\n" +
                "2. 动态参数不能取空格，即 `[]` 中不能取空格。\n" +
                "3. 动态参数避免使用大写，如 argTest\n" +
                "    1. 当使用嵌入式 DOM 模板时会造成代码无用，因为浏览器会将其强制转化为小写。\n" +
                "    2. 当使用单文件组件内的模板时则可以使用大写。\n" +
                "\n" +
                "## 1.3 指令修饰符\n" +
                "\n" +
                "修饰符是以 `.` 开头的特殊后缀，表明指令需要以一些特殊的方式被绑定。例如 `.prevent` 修饰符会告知 `v-on` 指令对触发的事件调用 `event.preventDefault()`：\n" +
                "\n" +
                "```html\n" +
                "<a @submit.prevent=\"onSubmit\">...</a>\n" +
                "```\n" +
                "\n" +
                "# 2 数据绑定\n" +
                "\n" +
                "## 2.1 纯文本绑定\n" +
                "\n" +
                "纯文本绑定是使用“Mustache”语法 (即双大括号 {{}} )：\n" +
                "\n" +
                "```html\n" +
                "<span>Message: {{ msg }}</span>\n" +
                "```\n" +
                "\n" +
                "{{ msg }} 表示的是属性 msg 的值，当属性 msg 的值发生变化时，{{ msg }} 也随之发生改变。  \n" +
                "\n" +
                "## 2.2 纯 HTML 绑定\n" +
                "\n" +
                "可以使用 v-html 指令将纯 HTML 绑定在某标签内部：\n" +
                "\n" +
                "```html\n" +
                "<template> \t\n" +
                "  <p>使用纯文本形式: {{ html }}</p><br/>\n" +
                "\t<!-- 等价于 -->\n" +
                "\t<p>使用纯文本形式：<span style=\"color: red\">文本</span></p>\n" +
                "\t\n" +
                "  <p>使用纯HTML形式：<span v-html=\"html\"></span></p>\n" +
                "\t<!-- 等价于 -->\n" +
                "\t<p>使用纯HTML形式：\n" +
                "\t\t<span>\n" +
                "\t\t\t<span style=\"color: red\">文本</span>\n" +
                "\t\t</span>\n" +
                "\t</p> \n" +
                "</template>\n" +
                "\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      html: '<span style=\\\"color: red\\\">文本</span>'\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "## 2.3 属性绑定\n" +
                "\n" +
                "### 2.3.1 v-bind 指令\n" +
                "\n" +
                "v-bind 指令可以响应式的绑定一个属性。\n" +
                "\n" +
                "```html\n" +
                "<template> \n" +
                "  <p v-bind:id=\"myId\">...</p>\n" +
                "\t<!-- 等价于 -->\n" +
                "\t<p v-bind:id=\"1\">...</p>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      myId: 1\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "### 2.3.2 布尔型属性\n" +
                "\n" +
                "布尔型属性根据其值 true 或 false 来决定属性是否存在该元素上。\n" +
                "\n" +
                "```html\n" +
                "<button :disabled=\"isButtonDisabled\">Button</button>\n" +
                "```\n" +
                "\n" +
                "当 isButtonDisabled 属性为 true 或 “” 时，元素会包含 disabled 这个属性，否则，元素会忽略 disabled 这个属性。\n" +
                "\n" +
                "### 2.3.3 多值绑定\n" +
                "\n" +
                "可以使用无参数的 v-bind 指令将多个属性绑定在单个元素上。\n" +
                "\n" +
                "```html\n" +
                "<template> \n" +
                "  <p v-bind=\"obj\">...</p>\n" +
                "\t<!-- 等价于 -->\n" +
                "\t<p id=\"1\" name=\"Object\">...</p>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      obj: {\n" +
                "        id: 1,\n" +
                "        name: 'Object'\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "# 3 JS 表达式\n" +
                "\n" +
                "## 3.1 单一表达式\n" +
                "\n" +
                "Vue 支持所有 JS 表达式，可以使用在下面两种场景中：\n" +
                "\n" +
                "- 纯文本绑定\n" +
                "- 指令的值\n" +
                "\n" +
                "```jsx\n" +
                "{{ number + 1 }}\n" +
                "\n" +
                "{{ ok ? 'YES' : 'NO' }}\n" +
                "\n" +
                "{{ message.split('').reverse().join('') }}\n" +
                "\n" +
                "<div :id=\"`list-${id}`\"></div>\n" +
                "```\n" +
                "\n" +
                "## 3.2 函数调用\n" +
                "\n" +
                "```html\n" +
                "<template> \n" +
                "  <p :id=\"idNum(num)\">{{ addNum(num) }}</p>\n" +
                "\t<!-- 结果 -->\n" +
                "\t<p id=\"1\">2</p>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      num: 1\n" +
                "    }\n" +
                "  },\n" +
                "  methods: {\n" +
                "    idNum(num) {\n" +
                "      return num\n" +
                "    },\n" +
                "    addNum(num) {\n" +
                "      return 1 + num\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "# 4 响应式\n" +
                "\n" +
                "## 4.1 响应式状态\n" +
                "\n" +
                "在使用选项式 API 时，可以使用 `data` 选项来声明组件的响应式状态，此选项的值为为返回一个对象的函数。Vue 将在创建新组件实例的时候调用此函数，并将函数返回的对象用响应式系统进行包装。此对象的所有顶层属性都会被代理到组件实例 (即 `methods` 选项和 `mounted` 选项中的 ) 的 `this` 上。\n" +
                "\n" +
                "```jsx\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      id: 1,\n" +
                "      user: {\n" +
                "        name: 'zhangsan',\n" +
                "        age: 19,\n" +
                "        sex: '男'\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  mounted() {\n" +
                "    console.log(this.user.name) // => \"zhangsan\"\n" +
                "    this.id = this.id + 3 // => id = 4\n" +
                "    console.log(this.id) // => 4\n" +
                "  }\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "## 4.2 响应式方法\n" +
                "\n" +
                "在组合式 API 中，可以使用 methods 选项声明方法。\n" +
                "\n" +
                "```jsx\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      count: 0\n" +
                "    }\n" +
                "  },\n" +
                "  methods: {\n" +
                "    increase() {\n" +
                "      this.count++\n" +
                "    },\n" +
                "    decrease() {\n" +
                "      this.count--\n" +
                "    }\n" +
                "  },\n" +
                "  mounted() {\n" +
                "    console.log(this.count) // => 0\n" +
                "    this.increase() // => count = 1\n" +
                "    console.log(this.count) // => 1\n" +
                "    this.decrease() // => count = 0\n" +
                "    console.log(this.count) // => 0\n" +
                "  }\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "Vue 自动为 `methods` 选项中的方法绑定了指向组件实例的 `this` ，确保方法在作为事件监听器或回调函数时始终保持正确的 `this`。\n" +
                "\n" +
                "注意：不能在定义 `methods` 时使用箭头函数，因为箭头函数没有 `this` 上下文。\n" +
                "\n" +
                "```jsx\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      count: 5\n" +
                "    }\n" +
                "  },\n" +
                "  methods: {\n" +
                "    reset: () => {\n" +
                "      // error: Cannot set properties of undefined (setting 'count')\n" +
                "      this.count = 0\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "## 4.3 响应式生命周期\n" +
                "\n" +
                "每个 Vue 组件实例在创建时都需要经历一系列的初始化步骤，比如设置好数据侦听，编译模板，挂载实例到 DOM，以及在数据改变时更新 DOM。在此过程中，它也会运行被称为生命周期钩子的函数，让开发者有机会在特定阶段运行自己的代码。\n" +
                "\n" +
                "`mounted`钩子可以用来在组件完成初始渲染并创建 DOM 节点后运行代码：\n" +
                "\n" +
                "```jsx\n" +
                "export default {\n" +
                "  mounted() {\n" +
                "    console.log(`the component is now mounted.`)\n" +
                "  }\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "Vue 实例生命周期的不同阶段会调用不同的钩子函数，最常用的是 **`mounted、updated、unmounted`**。\n" +
                "\n" +
                "所有生命周期钩子函数的 `this` 上下文都会自动指向当前调用它的组件实例。注意：避免用箭头函数来定义生命周期钩子，因为如果这样的话你将无法在函数中通过 `this` 获取组件实例。", "# 计算属性\n" +
                "\n" +
                "---\n" +
                "\n" +
                "---\n" +
                "\n" +
                "# 1 计算属性\n" +
                "\n" +
                "## 1.1 计算属性简介\n" +
                "\n" +
                "> 计算属性指的是描述依赖响应式状态的复杂逻辑。\n" +
                "> \n" +
                "\n" +
                "在 Vue 中，使用  `computed` 选项来定义计算属性。\n" +
                "\n" +
                "## 1.2 computed 选项\n" +
                "\n" +
                "computed 选项如下所示：\n" +
                "\n" +
                "```jsx\n" +
                "interface ComponentOptions {\n" +
                "  computed?: {\n" +
                "    [key: string]: ComputedGetter<any> | WritableComputedOptions<any>\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "type ComputedGetter<T> = (\n" +
                "  this: ComponentPublicInstance,\n" +
                "  vm: ComponentPublicInstance\n" +
                ") => T\n" +
                "\n" +
                "type ComputedSetter<T> = (\n" +
                "  this: ComponentPublicInstance,\n" +
                "  value: T\n" +
                ") => void\n" +
                "\n" +
                "type WritableComputedOptions<T> = {\n" +
                "  get: ComputedGetter<T>\n" +
                "  set: ComputedSetter<T>\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "# 2 计算属性的使用\n" +
                "\n" +
                "## 2.1 计算属性的类型\n" +
                "\n" +
                "根据 computed 选项 API，可以将计算属性分成两种：\n" +
                "\n" +
                "- 只读计算属性，一个带有 get 方法的对象。\n" +
                "- 可写计算属性，一个带有 get 和 set 方法的对象。\n" +
                "\n" +
                "### 2.1.1 只读计算属性\n" +
                "\n" +
                "只读计算属性是一个带有 get 方法的对象。\n" +
                "\n" +
                "```jsx\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      firstName: 'vue',\n" +
                "      lastName: 'springboot'\n" +
                "    }\n" +
                "  },\n" +
                "  computed: {\n" +
                "    // 声明只读计算属性的两种方式：\n" +
                "    // 1. 隐式声明只读计算属性，其本质上为一个get方法\n" +
                "    getterComputed() {\n" +
                "      return this.firstName + \" \" + this.lastName\n" +
                "    },\n" +
                "    // 2. 显式声明只读计算属性，其get方法命名必须为get\n" +
                "    getObj: {\n" +
                "      get() {\n" +
                "        return this.firstName + \" \" + this.lastName\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  mounted() {\n" +
                "    // 使用只读计算属性，本质上是调用get方法\n" +
                "    console.log('getterComputed = ' + this.getterComputed) // => getterComputed = vue springboot\n" +
                "    console.log('getObj = ' + this.getObj) // => getObj = vue springboot\n" +
                "  }\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "### 2.1.2 可写计算属性\n" +
                "\n" +
                "可写计算属性是一个带有 get 和 set 方法的对象。\n" +
                "\n" +
                "```jsx\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      firstName: 'vue',\n" +
                "      lastName: 'springboot'\n" +
                "    }\n" +
                "  },\n" +
                "  computed: {\n" +
                "    // 声明一个可写计算属性\n" +
                "    fullName: {\n" +
                "      // 计算属性的两个方法：\n" +
                "      // 1. get方法，命名必须为get\n" +
                "      get() {\n" +
                "        return this.firstName + ' ' + this.lastName\n" +
                "      },\n" +
                "      // 2. set方法，命名必须为set\n" +
                "      set(newValue) {\n" +
                "        [this.firstName, this.lastName] = newValue.split(' ')\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  mounted() {\n" +
                "    // 计算属性的两种使用方式：\n" +
                "    // 1. 使用可写计算属性时默认调用其中的get方法\n" +
                "    console.log('fullName = ' + this.fullName) // => fullName = vue springboot\n" +
                "    // 2. 使用可写计算属性进行赋值时默认调用set方法\n" +
                "    this.fullName = 'html css'\n" +
                "    console.log('fullName = ' + this.fullName) // => fullName = html css\n" +
                "  }\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "## 2.2 计算属性的缓存特性\n" +
                "\n" +
                "**计算属性**具有缓存特性，指**计算属性值会基于其响应式依赖被缓存**。一个计算属性仅会在其响应式依赖更新时才重新计算。\n" +
                "\n" +
                "相比之下，**方法调用总是会在重渲染发生时再次执行函数**。","# 1 监听属性\n" +
                "\n" +
                "## 1.1 监听属性简介\n" +
                "\n" +
                "> **监听属性**指的是在**状态发生变化时进行其它操作**，如更新 DOM、修改其它状态等。\n" +
                "> \n" +
                "\n" +
                "# 2 监听属性的使用\n" +
                "\n" +
                "## 2.1 监听属性的定义\n" +
                "\n" +
                "监听属性的定义有两种方式：\n" +
                "\n" +
                "- 使用 **watch 选项**定义监听属性\n" +
                "- 使用 **$watch() 实例方法**定义监听属性\n" +
                "\n" +
                "### 2.1.1 watch 选项\n" +
                "\n" +
                "watch 选项如下所示：\n" +
                "\n" +
                "```tsx\n" +
                "interface ComponentOptions {\n" +
                "  watch?: {\n" +
                "    [key: string]: WatchOptionItem | WatchOptionItem[]\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "type WatchOptionItem = string | WatchCallback | ObjectWatchOptionItem\n" +
                "\n" +
                "type WatchCallback<T> = (\n" +
                "  value: T,\n" +
                "  oldValue: T,\n" +
                "  onCleanup: (cleanupFn: () => void) => void\n" +
                ") => void\n" +
                "\n" +
                "type ObjectWatchOptionItem = {\n" +
                "\t// 监听属性的回调函数（必选）\n" +
                "  handler: WatchCallback | string\n" +
                "\t// 监听属性的执行时机（可选）\n" +
                "  immediate?: boolean // default: false\n" +
                "\t// 监听属性的层次深度（可选）\n" +
                "  deep?: boolean // default: false\n" +
                "\t// 监听属性的刷新时机（可选）\n" +
                "  flush?: 'pre' | 'post' | 'sync' // default: 'pre'\n" +
                "\t// 监听属性的调试（可选）\n" +
                "  onTrack?: (event: DebuggerEvent) => void\n" +
                "  onTrigger?: (event: DebuggerEvent) => void\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "### 2.1.2 $watch() 实例方法\n" +
                "\n" +
                "$watch() 实例方法如下所示：\n" +
                "\n" +
                "```tsx\n" +
                "interface ComponentPublicInstance {\n" +
                "  $watch(\n" +
                "    source: string | (() => any),\n" +
                "    callback: WatchCallback,\n" +
                "    options?: WatchOptions\n" +
                "  ): StopHandle\n" +
                "}\n" +
                "\n" +
                "type WatchCallback<T> = (\n" +
                "  value: T,\n" +
                "  oldValue: T,\n" +
                "  onCleanup: (cleanupFn: () => void) => void\n" +
                ") => void\n" +
                "\n" +
                "interface WatchOptions {\n" +
                "  immediate?: boolean // default: false\n" +
                "  deep?: boolean // default: false\n" +
                "  flush?: 'pre' | 'post' | 'sync' // default: 'pre'\n" +
                "  onTrack?: (event: DebuggerEvent) => void\n" +
                "  onTrigger?: (event: DebuggerEvent) => void\n" +
                "}\n" +
                "\n" +
                "type StopHandle = () => void\n" +
                "```\n" +
                "\n" +
                "## 2.2 监听属性的参数\n" +
                "\n" +
                "### 2.2.1 监听属性的回调函数\n" +
                "\n" +
                "通过 watch 选项或者 $watch() 实例方法可以接受一个对象，该对象的键为监听属性，值为回调函数。\n" +
                "\n" +
                "```jsx\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      num: 0\n" +
                "    }\n" +
                "  },\n" +
                "  watch: {\n" +
                "    // 1. 使用watch选项来创建一个监听属性及其回调函数\n" +
                "\t\t// 注意参数，参数名称可任意\n" +
                "\t\t// 第一个参数，新值\n" +
                "\t\t// 第二个参数，旧值\n" +
                "    num(newVal, oldVal) {\n" +
                "      console.log(`num.newVal = ${newVal}, num.oldVal = ${oldVal}`)\n" +
                "    }\n" +
                "    // 几种等价定义形式\n" +
                "    /*\n" +
                "\t\tnum: function (newVal, oldVal) {\n" +
                "      console.log(`num.newVal = ${newVal}, num.oldVal = ${oldVal}`)\n" +
                "    }\n" +
                "\t\tnum: function myHandler(newVal, oldVal) {\n" +
                "      console.log(`num.newVal = ${newVal}, num.oldVal = ${oldVal}`)\n" +
                "    }\t\n" +
                "    num: {\n" +
                "      handler(newVal, oldVal) {\n" +
                "        console.log(`num.newVal = ${newVal}, num.oldVal = ${oldVal}`)\n" +
                "      }\n" +
                "    }\n" +
                "\t\tnum: {\n" +
                "      handler: function myHandler(newVal, oldVal) {\n" +
                "        console.log(`num.newVal = ${newVal}, num.oldVal = ${oldVal}`)\n" +
                "      }\n" +
                "    }\n" +
                "    */    \n" +
                "  },\n" +
                "  created() {\n" +
                "\t\t// 2. 使用$watch()组件实例来创建一个监听属性及其回调\n" +
                "    this.$watch('num', (newVal, oldVal) => {\n" +
                "      console.log(`num.newVal = ${newVal}, num.oldVal = ${oldVal}`)\n" +
                "    }),\n" +
                "\n" +
                "    this.num = 3 // => num.newVal = 3, num.oldVal = 0\n" +
                "  }\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "### 2.2.2 监听属性的执行时机\n" +
                "\n" +
                "在默认情况下，监听属性是**懒加载**机制：**仅当数据值变化时，才会执行回调**。\n" +
                "\n" +
                "然而我们可以使用监听属性的 `immediate: true` 选项，使回调在创建之时立即执行一遍。\n" +
                "\n" +
                "```jsx\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      num: 0\n" +
                "    }\n" +
                "  },\n" +
                "  watch: {\n" +
                "    // 使用watch选项配置监听属性\n" +
                "    num: {\n" +
                "      handler(newVal, oldVal) {\n" +
                "        console.log(`num.newVal = ${newVal}, num.oldVal = ${oldVal}`)\n" +
                "      },\n" +
                "      immediate: true\n" +
                "    } \n" +
                "  },\n" +
                "  created() {\n" +
                "    // 执行之前：num.newVal = 0, num.oldVal = undefined\n" +
                "    this.num = 3 \n" +
                "    // 执行之后：num.newVal = 3, num.oldVal = 0\n" +
                "  }\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "### 2.2.3 监听属性的层次深度\n" +
                "\n" +
                "默认情况下，监听属性是**浅层**的：**监听属性被修改时才会执行回调**，而其**嵌套属性的修改不会执行回调**。\n" +
                "\n" +
                "我们可以使用监听属性的 `deep: true` 选项使嵌套属性在修改时也会执行回调。\n" +
                "\n" +
                "```jsx\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      user1: {\n" +
                "        name: 'java',\n" +
                "        age: 20\n" +
                "      },\n" +
                "      user2: {\n" +
                "        name: 'vue',\n" +
                "        age: 18\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  watch: {\n" +
                "    user1: {\n" +
                "      handler(newVal, oldVal) {\n" +
                "        console.log(`user1.newName = ${newVal.name}, user1.newAge = ${newVal.age}`)\n" +
                "      },\n" +
                "      deep: false //默认选项\n" +
                "    },\n" +
                "    user2: {\n" +
                "      handler(newVal, oldVal) {\n" +
                "        console.log(`user2.newName = ${newVal.name}, user2.newAge = ${newVal.age}`)\n" +
                "      },\n" +
                "      deep: true\n" +
                "    } \n" +
                "  },\n" +
                "  created() {\n" +
                "    // 因为deep: false，不执行回调\n" +
                "    this.user1.name = 'C++'\n" +
                "\n" +
                "    // 因为deep: true，执行回调\n" +
                "    this.user2.name = 'react' // => user2.newName = react, user2.newAge = 18  \n" +
                "  }\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "### 2.2.4 监听属性的刷新时机\n" +
                "\n" +
                "默认情况下，监听属性的回调刷新时机是 `flush: pre` 选项：监听属性的回调在 Vue 组件更新之前被调用，此时在**监听属性的回调中访问的是被 Vue 更新之前的 DOM 元素**。\n" +
                "\n" +
                "然而，我们可以设置刷新时机为 `flush: post` 选项，在**监听属性的回调中可以访问被 Vue 更新之后的 DOM 元素**。另外，还有一个 `flush: sync` 选项，此选项是同时触发 Vue 组件更新和监听属性的回调。\n" +
                "\n" +
                "```jsx\n" +
                "<template>\n" +
                "  <p id=\"p1\">{{ num1 }}</p>\n" +
                "  <p id=\"p2\">{{ num2 }}</p>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      num1: 1,\n" +
                "      num2: 2\n" +
                "    }\n" +
                "  },\n" +
                "  watch: {\n" +
                "    num1: {\n" +
                "      handler(newVal, oldVal) {\n" +
                "\t\t\t\t// 访问的是 DOM 更新之前的状态\n" +
                "        console.log(`num1 = ` + document.getElementById(\"p1\").innerHTML)\n" +
                "\t\t\t\t// => num1 = 1\n" +
                "        console.log(`num1.newVal = ${newVal}, num1.oldVal = ${oldVal}`)\n" +
                "\t\t\t\t// => num1.newVal = 11, num1.oldVal = 1\n" +
                "      },\n" +
                "      flush: 'pre'\n" +
                "    },\n" +
                "    num2: {\n" +
                "      handler(newVal, oldVal) {\n" +
                "\t\t\t\t// 访问的是 DOM 更新之后的状态\n" +
                "        console.log(`num2 = ` + document.getElementById(\"p2\").innerHTML)\n" +
                "\t\t\t\t// => num2 = 22\n" +
                "        console.log(`num2.newVal = ${newVal}, num2.oldVal = ${oldVal}`)\n" +
                "\t\t\t\t// => num2.newVal = 22, num2.oldVal = 2\n" +
                "      },\n" +
                "      flush: 'post'\n" +
                "    } \n" +
                "  },\n" +
                "  mounted() {   \n" +
                "    this.num1 = 11\n" +
                "    this.num2 = 22\t\t\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "### 2.2.5 监听属性的调试\n" +
                "\n" +
                "监听属性有两个选项可以进行调试：\n" +
                "\n" +
                "- `onTrack` 将在响应属性或引用作为依赖项被跟踪时被调用。\n" +
                "- `onTrigger` 将在监听属性回调被依赖项的变更触发时被调用。\n" +
                "\n" +
                "```jsx\n" +
                "export default {\n" +
                "  data() {   \n" +
                "    return {\n" +
                "      num: 0\n" +
                "    }\n" +
                "  },\n" +
                "  watch: {\n" +
                "    num: {\n" +
                "      handler(newVal, oldVal) {\n" +
                "        console.log(`num.newVal = ${newVal}, num.oldVal = ${oldVal}`)\n" +
                "      },\n" +
                "      // onTrack 将在响应属性或引用作为依赖项被跟踪时被调用。\n" +
                "      onTrack(e) {\n" +
                "        debugger\n" +
                "      },\n" +
                "      // onTrigger 将在监听属性回调被依赖项的变更触发时被调用。\n" +
                "      onTrigger(e) {\n" +
                "        debugger\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  created() {\n" +
                "    this.num = 1\n" +
                "  }\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "## 2.3 监听属性的停止\n" +
                "\n" +
                "用 `watch` 选项或者 `$watch()` 实例方法声明的监听属性，会在宿主组件卸载时自动停止。\n" +
                "\n" +
                "然而，我们可以使用 `$watch()` 实例方法的 API 进行手动停止监听属性的使用。\n" +
                "\n" +
                "```jsx\n" +
                "export default {\n" +
                "  data() {   \n" +
                "    return {\n" +
                "      num: 0\n" +
                "    }\n" +
                "  },\n" +
                "  created() {\n" +
                "    const unwatch = this.$watch('num', (newVal, oldVal) => {\n" +
                "      console.log(`num.newVal = ${newVal}, num.oldVal = ${oldVal}`)\n" +
                "    })\n" +
                "\t\t// 停止监听属性\n" +
                "    unwatch()\n" +
                "  }\n" +
                "}\n" +
                "```", "# 1 属性绑定概述\n" +
                "\n" +
                "## 1.1 属性绑定简介\n" +
                "\n" +
                "> 属性绑定指的是使用 v-bind 指令将属性和字符串进行动态绑定。\n" +
                "> \n" +
                "\n" +
                "Vue 中专门为 `class` 属性和 `style` 属性的 `v-bind` 指令用法提供了特殊的功能增强，除了字符串外，表达式的值也可以是对象或数组。\n" +
                "\n" +
                "## 1.2 v-bind 指令\n" +
                "\n" +
                "动态的绑定一个或多个 attribute，也可以是组件的 prop。\n" +
                "\n" +
                "- **缩写：**`:` 或者 `.` (当使用 `.prop` 修饰符)\n" +
                "- **期望：**`any (带参数) | Object (不带参数)`\n" +
                "- **参数：**`attrOrProp (可选的)`\n" +
                "- **修饰符：**\n" +
                "    - `.camel` ——将短横线命名的 attribute 转变为驼峰式命名。\n" +
                "    - `.prop` ——强制绑定为 DOM property。\n" +
                "        \n" +
                "        **3.2+**\n" +
                "        \n" +
                "    - `.attr` ——强制绑定为 DOM attribute。\n" +
                "        \n" +
                "        **3.2+**\n" +
                "        \n" +
                "- **用途：**\n" +
                "    \n" +
                "    当用于绑定 `class` 或 `style` attribute，`v-bind` 支持额外的值类型如数组或对象。详见下方的指南链接。\n" +
                "    \n" +
                "    在处理绑定时，Vue 默认会利用 `in` 操作符来检查该元素上是否定义了和绑定的 key 同名的 DOM property。如果存在同名的 property，则 Vue 会把作为 DOM property 赋值，而不是作为 attribute 设置。这个行为在大多数情况都符合期望的绑定值类型，但是你也可以显式用 `.prop` 和 `.attr` 修饰符来强制绑定方式。有时这是必要的，特别是在和**[自定义元素](https://cn.vuejs.org/guide/extras/web-components.html#passing-dom-properties)**打交道时。\n" +
                "    \n" +
                "    当用于组件 props 绑定时，所绑定的 props 必须在子组件中已被正确声明。\n" +
                "    \n" +
                "    当不带参数使用时，可以用于绑定一个包含了多个 attribute 名称-绑定值对的对象。\n" +
                "    \n" +
                "\n" +
                "# 2 Class 属性绑定\n" +
                "\n" +
                "## 2.1 绑定对象\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <!-- 1. 绑定多个对象 -->\n" +
                "  <div :class=\"{ active: isActive, error: isError}\"></div>\n" +
                "  <!-- 2. 绑定多个对象的组合形式 -->\n" +
                "  <div :class=\"classObj\"></div>\n" +
                "  <!-- 渲染结果：<div class=\"active\"></div>-->\n" +
                "\n" +
                "  <!-- 3. 静态和动态结合绑定多个类属性 -->\n" +
                "  <div class=\"static\" :class=\"{ active: true}\"></div>\n" +
                "  <!-- 渲染结果：<div class=\"static active\"></div> -->\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {   \n" +
                "    return {\n" +
                "      isActive: true,\n" +
                "      isError: false,\n" +
                "      classObj: {\n" +
                "        active: true,\n" +
                "        error: false\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "## 2.2 绑定计算属性\n" +
                "\n" +
                "```jsx\n" +
                "<template>\n" +
                "  <!-- 动态class属性绑定计算属性 -->\n" +
                "  <div :class=\"classObject\"></div>\n" +
                "  <!-- 等价于 -->\n" +
                "  <div class=\"active\"></div>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      isActive: true,\n" +
                "      isError: false\n" +
                "    }\n" +
                "  },\n" +
                "  computed: {\n" +
                "    classObject() {\n" +
                "      return {\n" +
                "        active: this.isActive && !this.isError,\n" +
                "        error: this.isError\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "## 2.3 绑定数组\n" +
                "\n" +
                "```jsx\n" +
                "<template>\n" +
                "  <!-- 动态class属性绑定计算属性 -->\n" +
                "  <div :class=\"[activeClass, errorClass]\"></div>\n" +
                "  <!-- 等价于 -->\n" +
                "  <div class=\"active error\"></div>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      activeClass: 'active',\n" +
                "      errorClass: 'error'\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "## 2.4 绑定组件\n" +
                "\n" +
                "```tsx\n" +
                "\n" +
                "```\n" +
                "\n" +
                "# 3 Style 属性绑定\n" +
                "\n" +
                "由于 CSS 样式总是以 `属性: 值` 的形式存储，因此可以使用两种方式绑定元素的 style 属性：\n" +
                "\n" +
                "- 对象形式\n" +
                "- 数组形式\n" +
                "\n" +
                "## 3.1 绑定对象\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <!-- 1. 支持 kebab-cased 形式的 CSS 属性-->\n" +
                "  <div :style=\"{ 'color': activeColor, 'font-size': fontSize + 'px'}\"></div>\n" +
                "  <!-- 2. 支持驼峰命名法 CSS 属性-->\n" +
                "  <div :style=\"{ color: activeColor, fontSize: fontSize + 'px'}\"></div>\n" +
                "  <!-- 3. 直接封装成对象 -->\n" +
                "  <div :style=\"styleObject\"></div>\n" +
                "  <!-- \n" +
                "\t\t渲染结果：\n" +
                "    <div style=\"color: red; font-size: 30px;\"></div>\n" +
                "  -->\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      activeColor: 'red',\n" +
                "      fontSize: 30,\n" +
                "\n" +
                "      styleObject: {\n" +
                "        color: 'red',\n" +
                "        fontSize: '30px'\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "## 3.2 绑定数组\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <!-- 1.数组形式存放对象 -->\n" +
                "  <div :style=\"[borderStyle, fontStyle]\"></div>\n" +
                "  <!-- 2.数组形式 -->\n" +
                "  <div :style=\"baseStyle\"></div>\n" +
                "  <!-- 渲染结果：\n" +
                "    <div style=\"border-color: black; border-width: 3px; \n" +
                "      font-size: 20px; color: red;\"></div>\n" +
                "  -->\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      borderStyle: {\n" +
                "        borderColor: 'black',\n" +
                "        borderWidth: '3px'\n" +
                "      },\n" +
                "      fontStyle: {\n" +
                "        fontSize: '20px',\n" +
                "        color: 'red'\n" +
                "      },\n" +
                "      baseStyle: [\n" +
                "        {borderColor: 'black',\n" +
                "        borderWidth: '3px'},\n" +
                "        {fontSize: '20px',\n" +
                "        color: 'red'}\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "## 3.3 style 属性特点\n" +
                "\n" +
                "### 3.3.1 自动前缀\n" +
                "\n" +
                "当我们在 `:style` 中使用了需要浏览器特殊前缀的 CSS 属性时，Vue 会自动为它们加上相应的前缀。Vue 是在运行时检查该属性是否支持在当前浏览器中使用。如果浏览器不支持某个属性，那么将测试加上各个浏览器特殊前缀，以找到哪一个是被支持的。\n" +
                "\n" +
                "### 3.3.2 样式多值\n" +
                "\n" +
                "我们可以对一个样式属性提供多个 (不同前缀的) 值，数组仅会渲染浏览器支持的最后一个值。\n" +
                "\n" +
                "```tsx\n" +
                "<div :style=\"{ display: ['-webkit-box', '-ms-flexbox', 'flex'] }\"></div>\n" +
                "```\n" +
                "\n" +
                "在这个例子中，在支持不需要特别前缀的浏览器中都会渲染为 `display: flex`。", "---\n" +
                "\n" +
                "# 1 表单绑定概述\n" +
                "\n" +
                "## 1.1 表单绑定简介\n" +
                "\n" +
                "> **表单绑定**指的是**将表单组件里面的数据同步给 JavaScript 中相应的变量**。\n" +
                "> \n" +
                "\n" +
                "表单绑定通常使用 v-model 指令来完成。\n" +
                "\n" +
                "表单绑定的好处：\n" +
                "\n" +
                "- 同步显示，例如实现一个搜索框，\n" +
                "\n" +
                "## 1.2 v-model 指令\n" +
                "\n" +
                "> 在表单输入元素或组件上创建双向绑定。\n" +
                "> \n" +
                "- **期望的绑定值类型**：根据表单输入元素或组件输出的值而变化\n" +
                "- **仅限：**\n" +
                "    - `<input>`\n" +
                "    - `<select>`\n" +
                "    - `<textarea>`\n" +
                "    - components\n" +
                "- **修饰符：**\n" +
                "    - **`.lazy`** ——监听 `change` 事件而不是 `input`\n" +
                "    - **`.number`** ——将输入的合法符串转为数字\n" +
                "    - **`.trim`** ——移除输入内容两端空格\n" +
                "\n" +
                "v-model 指令会根据锁使用的元素自动使用对应的 DOM 属性和事件进行组合：\n" +
                "\n" +
                "- `<input>` 和 `<textarea>` 元素会绑定 `value` 属性并侦听 `input` 事件；\n" +
                "- `<input type=\"checkbox\">` 和 `<input type=\"radio\">` 会绑定 `checked` 属性并监听 `change` 事件；\n" +
                "- `<select>` 会绑定 `value` 属性并侦听 `change` 事件。\n" +
                "\n" +
                "<aside>\n" +
                "⚠️ **注意**\n" +
                "\n" +
                "`v-model` 会忽略任何表单元素上初始的 `value`、`checked` 或 `selected` 属性。它将始终将当前绑定的 JavaScript 状态视为数据的正确来源。你应该在 JavaScript 中使用 `data` 选项来声明该初始值。\n" +
                "\n" +
                "</aside>\n" +
                "\n" +
                "# 2 使用 v-model 指令的表单元素\n" +
                "\n" +
                "## 2.1 单行文本 text\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <div>message: {{ msg }}</div>\n" +
                "  <input v-model=\"msg\" />\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      msg: ''\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/bb92b8ae-de18-454c-85ee-2e7e67c30185/Untitled.png)\n" +
                "\n" +
                "## 2.2 多行文本 textarea\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <div>message: {{ msg }}</div>\n" +
                "  <textarea v-model=\"msg\" ></textarea>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      msg: ''\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/bbafb3ff-32d5-4d5d-b282-e277adbc5519/Untitled.png)\n" +
                "\n" +
                "## 2.3 单选按钮 radio\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <div>Picked: {{ picked }}</div>\n" +
                "\n" +
                "  <input type=\"radio\" id=\"one\" value=\"One\" v-model=\"picked\" />\n" +
                "  <label for=\"one\">One</label>\n" +
                "\n" +
                "  <input type=\"radio\" id=\"two\" value=\"Two\" v-model=\"picked\" />\n" +
                "  <label for=\"two\">Two</label>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      picked: ''\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/e1ff5434-a8e0-4552-aaa7-62ca5e81003e/Untitled.png)\n" +
                "\n" +
                "## 2.4 复选框 checkbox\n" +
                "\n" +
                "### 2.4.1 单一复选框\n" +
                "\n" +
                "单一复选框，绑定的值为布尔类型。\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <input type=\"checkbox\" id=\"checkbox\" v-model=\"checked\" />\n" +
                "  <label for=\"checkbox\">{{ checked }}</label> \n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      checked: ''\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/0b59ef57-98db-4038-8205-8ae5d4192040/Untitled.png)\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/aacc74b5-59e4-4a0c-adb4-0af68c7e726d/Untitled.png)\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/a0d1831f-5aef-4481-ba9b-5b47ffd9e767/Untitled.png)\n" +
                "\n" +
                "### 2.4.2 多值复选框\n" +
                "\n" +
                "多值复选框可以使用一个数组来接受所有的选项。\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <div>Checked names: {{ checkedNames }}</div>\n" +
                "\n" +
                "  <input type=\"checkbox\" id=\"jack\" value=\"Jack\" v-model=\"checkedNames\">\n" +
                "  <label for=\"jack\">Jack</label>\n" +
                "\n" +
                "  <input type=\"checkbox\" id=\"john\" value=\"John\" v-model=\"checkedNames\">\n" +
                "  <label for=\"john\">John</label>\n" +
                "\n" +
                "  <input type=\"checkbox\" id=\"mike\" value=\"Mike\" v-model=\"checkedNames\">\n" +
                "  <label for=\"mike\">Mike</label>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      checkedNames: []\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/fc2ee130-33df-4a46-a1cf-d7f7c6e6ce92/Untitled.png)\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/ed7d1a83-0ebb-47d3-b221-53470c2d3ba7/Untitled.png)\n" +
                "\n" +
                "## 2.5 选择器 select\n" +
                "\n" +
                "### 2.5.1 单一选择器\n" +
                "\n" +
                "对于单一选择器，可以使用一个值来接受选项的内容。\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <div>selected: {{ selected }}</div>\n" +
                "  <select v-model=\"selected\">\n" +
                "    <option>A</option>\n" +
                "    <option>B</option>\n" +
                "    <option>C</option>\n" +
                "  </select>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      selected: ''\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/323376a5-16fc-41ec-a479-bba6f68002c1/Untitled.png)\n" +
                "\n" +
                "### 2.5.2 多选选择器\n" +
                "\n" +
                "对于带有 `multiple` 属性的多选选择器，可以使用一个数组来接受选项中的内容。\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <div>selected: {{ selected }}</div>\n" +
                "  <select v-model=\"selected\" multiple>\n" +
                "    <option>A</option>\n" +
                "    <option>B</option>\n" +
                "    <option>C</option>\n" +
                "  </select>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      selected: []\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/99528f9b-224d-4466-a893-30582ebe144b/Untitled.png)\n" +
                "\n" +
                "# 3 值绑定的三种方式\n" +
                "\n" +
                "## 3.1 动态绑定\n" +
                "\n" +
                "我们前面介绍的全是静态绑定，即使用的是元素的 value 属性，我们还可以使用 v-bind 指令动态绑定 value 属性，使其值为动态的。\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <div>picked: {{ pick }}</div>\n" +
                "  <input type=\"radio\" v-model=\"pick\" :value=\"first\" />\n" +
                "  <label>数字1</label>\n" +
                "  <input type=\"radio\" v-model=\"pick\" :value=\"second\" />\n" +
                "  <label>数字2</label>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      pick: '',\n" +
                "      first: 123,\n" +
                "      second: 456\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/6bdbf5fc-d23a-437c-8294-da42b00f37b0/Untitled.png)\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/ff48ee1b-0c9c-4585-9747-07bfc77e843b/Untitled.png)\n" +
                "\n" +
                "## 3.2 对象绑定\n" +
                "\n" +
                "v-model 绑定的值还可以是对象形式。\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <div>picked: {{ pick }}</div>\n" +
                "  <input type=\"radio\" v-model=\"pick\" :value=\"first\" />\n" +
                "  <label>用户1</label>\n" +
                "  <input type=\"radio\" v-model=\"pick\" :value=\"second\" />\n" +
                "  <label>用户2</label>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      pick: '',\n" +
                "      first: {\n" +
                "        name: 'zhangsan',\n" +
                "        age: 18,\n" +
                "        sex: '男'\n" +
                "      },\n" +
                "      second:  {\n" +
                "        name: 'lili',\n" +
                "        age: 19,\n" +
                "        sex: '女'\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/1b8a5232-e159-4f98-b185-023ba918a9ba/Untitled.png)\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/3ee6840d-5a87-4a32-97c5-5d7c35386e80/Untitled.png)\n" +
                "\n" +
                "## 3.3 true-value 和 false-value 属性\n" +
                "\n" +
                "true-value 和 false-value 属性是 Vue 中的特有属性，仅支持和 v-model 指令一起使用。\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <div>toggle: {{ toggle }}</div>\n" +
                "  <input type=\"checkbox\" v-model=\"toggle\" \n" +
                "\t\t:true-value=\"trueVal\" \n" +
                "\t\t:false-value=\"falseVal\" />\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      toggle: '',\n" +
                "      trueVal: 'good, true',\n" +
                "      falseVal: 'oh no, false'\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/e23c117f-dd32-46b0-9410-7f39284b607c/Untitled.png)\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/a26e78a9-5ea9-416b-93e8-64f8dec8755f/Untitled.png)\n" +
                "\n" +
                "# 4 v-model 指令的三种修饰符\n" +
                "\n" +
                "## 4.1 .lazy\n" +
                "\n" +
                "默认情况下，`v-model` 指令会在 `input` 事件后更新数据。(**[IME 拼字阶段的状态](https://cn.vuejs.org/guide/essentials/forms.html#vmodel-ime-tip)**例外)\n" +
                "\n" +
                "我们可以添加 `.lazy` 修饰符，使 `v-model` 指令在 `change` 事件后更新数据。\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <div>message: {{ msg }}</div>\n" +
                "  <input v-model.lazy=\"msg\" />\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      msg: ''\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "![当鼠标悬停在输入框时，触发 input 事件](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/fd057b7d-b4b0-4d3b-93d0-9ac80d5787f5/Untitled.png)\n" +
                "\n" +
                "当鼠标悬停在输入框时，触发 input 事件\n" +
                "\n" +
                "![当鼠标移开输入框时，触发 change 事件](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/bc4e5db4-d03d-4161-9cba-7d858481dd51/Untitled.png)\n" +
                "\n" +
                "当鼠标移开输入框时，触发 change 事件\n" +
                "\n" +
                "## 4.2 .number\n" +
                "\n" +
                "默认情况下，`v-model` 指令的值为字符串类型，我们可以使用 `.number` 修饰符来使 `v-model` 指令的值为数字类型。实质上，`.number` 修饰符使用了 `parseFloat()` 方法进行处理数据，如果该数据无法被该方法处理，将返回原来的值。\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <div>message1: {{ msg1 }}</div>\n" +
                "  <input v-model.number=\"msg1\" />\n" +
                "\n" +
                "  <div>message2: {{ msg2 }}</div>\n" +
                "  <input v-model.number=\"msg2\" />\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      msg1: '',\n" +
                "      msg2: ''\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/52ff4138-48e2-4286-8efa-109728d2c6d6/Untitled.png)\n" +
                "\n" +
                "## 4.3 .trim\n" +
                "\n" +
                "默认情况下，`v-model` 指令并不会清除输入框中的首尾空格，我们可以使用 `.trim` 修饰符将输入框首尾空格进行消除。\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "\t\t<!-- pre标签保留文本空格 -->\n" +
                "\n" +
                "    <!-- 保留首尾空格 -->\n" +
                "    <div><pre>message1: {{ msg1 }}</pre></div>\n" +
                "    <input v-model=\"msg1\" />\n" +
                "\n" +
                "    <!-- 使用.lazy修饰符去除首尾空格 -->\n" +
                "    <div><pre>message2: {{ msg2 }}</pre></div>\n" +
                "    <input v-model.trim=\"msg2\" />\n" +
                "</template>\n" +
                "<script>\n" +
                "\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      msg1: '',\n" +
                "      msg2: ''\n" +
                "    }\n" +
                "  },\n" +
                "  methods: {\n" +
                "    result() {\n" +
                "      console.log(this.msg1)\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/04a4d207-5afb-47e7-a687-8d13b1bb5d2e/Untitled.png)\n" +
                "\n" +
                "# 5 表单绑定的应用示例\n" +
                "\n" +
                "## 5.1 简易搜索框\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <input id=\"serach\" v-model=\"content\" />\n" +
                "  <button @click=\"clickSearch(content)\">搜索</button>\n" +
                "  <div :class=\"{ active: isActive }\">\n" +
                "    <ul>\n" +
                "      <li v-for=\"(result, index) in results\" @click=\"\">{{ result.name }}</li>\n" +
                "    </ul>\n" +
                "  </div>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      // 搜索框内容\n" +
                "      content: '',\n" +
                "      // 搜索结果框的class属性，处理界面交互效果\n" +
                "      isActive: false,\n" +
                "      // 搜索结果框中的内容\n" +
                "      results: [],\n" +
                "      // 待搜索的的所有物品\n" +
                "      items: [\n" +
                "        {name: '大白菜', price: 19.9},\n" +
                "        {name: '小白菜', price: 12.9},\n" +
                "        {name: '西红柿', price: 10.9},\n" +
                "        {name: '土豆', price: 11.0},\n" +
                "        {name: '黑梁大米', price: 59.9},\n" +
                "        {name: '东北大米', price: 69.9},\n" +
                "        {name: '黄豆', price: 12.0},\n" +
                "        {name: '绿豆', price: 13.0},\n" +
                "        {name: '麻瓜', price: 3.9},\n" +
                "        {name: '西瓜', price: 4.9}\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  watch: {\n" +
                "    // 监听搜索框内容的变化\n" +
                "    content(newVal, oldVal) {\n" +
                "      // 清空搜索栏\n" +
                "      this.clearSearch()\n" +
                "      // 搜索框内容不为空\n" +
                "      if (newVal != '') {\n" +
                "        // 搜索\n" +
                "        this.searchFor(newVal)\n" +
                "      } \n" +
                "    }\n" +
                "  },\n" +
                "  methods: {\n" +
                "    // 清空搜索栏及其样式\n" +
                "    clearSearch() {\n" +
                "      this.results = []\n" +
                "      this.isActive = false\n" +
                "    },\n" +
                "    // 搜索\n" +
                "    searchFor(keyWord) {\n" +
                "      // 搜索所有物品\n" +
                "      for (var i = 0; i < this.items.length; i++) {\n" +
                "        if (this.items[i].name.indexOf(keyWord) != -1) {\n" +
                "          this.results.push(this.items[i])\n" +
                "        }\n" +
                "      }\n" +
                "      // 激活搜索结果样式\n" +
                "      if (this.results.length !== 0) {\n" +
                "        this.isActive = true\n" +
                "      }\n" +
                "    },\n" +
                "    // 点击搜索栏\n" +
                "    clickSearch(content) {\n" +
                "      // 清空搜索栏\n" +
                "      this.clearSearch()\n" +
                "      // 搜索\n" +
                "      this.searchFor(content)\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "<style>\n" +
                "  input {width: 200px;}\n" +
                "  button {width: 50px;}\n" +
                "  .active {width: 200px;border: 1px solid black;border-radius: 2px;}\n" +
                "  ul {list-style: none;padding-left:0;}\n" +
                "  li:hover {background-color: rgb(233,233,233);}\n" +
                "</style>\n" +
                "```\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/d98e1f63-c1a8-4b05-a12d-f7abaaf100ac/Untitled.png)", "# 1 条件渲染\n" +
                "\n" +
                "## 1.1 v-if 指令\n" +
                "\n" +
                "`v-if` 指令用于条件性地渲染一块内容，这块内容只会在指令的表达式返回真值时才被渲染。\n" +
                "\n" +
                "```jsx\n" +
                "<template>\n" +
                "  <div v-if=\"flag\"></div>\n" +
                "\t<!-- 渲染结果：<div><div> -->\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      flag: true\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "## 1.2 v-else 指令\n" +
                "\n" +
                "`v-else` 指令是为 `v-if` 指令添加的一个 else 块。\n" +
                "\n" +
                "```jsx\n" +
                "<template>\n" +
                "  <div v-if=\"flag\">Hello, Vue.js</div>\n" +
                "  <div v-else>Bye, Vue,js</div>\n" +
                "\t<!-- 渲染结果：<div>Hello, Vue.js</div> -->\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      flag: true\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "## 1.3 v-else-if 指令\n" +
                "\n" +
                "`v-else-if` 提供的是相应于 `v-if` 的 else if 块，可以连续多次重复使用。\n" +
                "\n" +
                "```jsx\n" +
                "<template>\n" +
                "  <div v-if=\"type == 1\">Hello, 1</div>\n" +
                "  <div v-else-if=\"type == 2\">Hello, 2</div>\n" +
                "  <div v-else-if=\"type == 3\">Hello, 3</div>\n" +
                "  <div v-else>Hello, -1</div>\n" +
                "\t<!-- 渲染结果：<div>Hello, 3</div> -->\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      type: 3\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "## 1.4 v-if、v-else-if、v-else 其它使用场景\n" +
                "\n" +
                "### 1.4.1 模板上使用 v-if、v-else-if、v-else\n" +
                "\n" +
                "```jsx\n" +
                "<template v-if=\"templateType == 'user'\">\n" +
                "  ...\n" +
                "</template>\n" +
                "<template v-else-if=\"templateType == 'admin'\">\n" +
                "  ...\n" +
                "</template>\n" +
                "<template v-else=\"templateType == 'visitor'\">\n" +
                "  ...\n" +
                "</template>\n" +
                "```\n" +
                "\n" +
                "## 1.5 v-show 指令\n" +
                "\n" +
                "`v-show` 指令和 `v-if` 指令的用法基本一样。\n" +
                "\n" +
                "```jsx\n" +
                "<template>\n" +
                "  <div v-show=\"ok\">hello, ok</div>\n" +
                "\t<!-- 渲染结果：<div>hello, ok</div> -->\n" +
                "  <div v-show=\"no\">hello, no</div>\n" +
                "\t<!-- 渲染结果：<div style=\"display: none;\">hello, no</div> -->\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      ok: true,\n" +
                "      no: false\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "## 1.6 v-if 和 v-show 对比\n" +
                "\n" +
                "v-if 指令和 v-show 指令的区别：\n" +
                "\n" +
                "1. 渲染差异\n" +
                "    - v-if 指令属于“**真实**”渲染，根据表达式的真或假来决定 DOM 中是否存在该元素。当表达式为真时，DOM 中存在该元素；表达式为假时，DOM 中无该元素。\n" +
                "    - v-show 指令属于“**虚假**”渲染，无论表达式为真或假，DOM 中都会存在该元素，表达式的值决定了 css 中 style 属性的取值。当表达式为真时，该元素的 style 属性取默认值，当表达式为假时，该元素的 style 属性为 “display: none”。\n" +
                "2. 使用差异\n" +
                "    - v-if 指令可以使用在模板元素（<template>）上。\n" +
                "    - v-show 指令不可以使用在模板元素（<template>）上。\n" +
                "\n" +
                "总的来说，v-if 指令的切换开销大，v-show 指令的初始化渲染开销大。\n" +
                "\n" +
                "# 2 列表渲染\n" +
                "\n" +
                "## 2.1 v-for 指令\n" +
                "\n" +
                "> v-for 指令用于基于原始数据多次渲染元素或模板块。\n" +
                "> \n" +
                "\n" +
                "v-for 指令语法格式如下：\n" +
                "\n" +
                "```tsx\n" +
                "<div v-for=\"item in items\">  \n" +
                "\t{{ item.text }}\n" +
                "</div>\n" +
                "```\n" +
                "\n" +
                "其中 items 参数可以为：Array | Object | number | string | Iterable\n" +
                "\n" +
                "## 2.2 v-for 遍历的方式\n" +
                "\n" +
                "### 2.2.1 v-for 遍历 Array\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <div v-for=\"num in nums\">\n" +
                "    {{ num }}\n" +
                "  </div>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      nums: [1, 2, 4, 6, 9]\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "### 2.2.2 v-for 遍历 Object\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <!-- 形式1：遍历对象的所有属性的值 -->\n" +
                "  <div v-for=\"val in obj\">\n" +
                "    {{ val }}\n" +
                "  </div>\n" +
                "\t<!-- 渲染结果：\n" +
                "\t\t\tzhangsan\n" +
                "\t\t\t19\n" +
                "\t\t\t男\n" +
                "\t-->\n" +
                "\n" +
                "  <!-- 形式2：遍历对象的所有属性及其值 -->\n" +
                "  <!-- 参数：值，属性(键) -->\n" +
                "  <div v-for=\"(val, key) in obj\">\n" +
                "    {{ key }} : {{val}}\n" +
                "  </div>\n" +
                "\t<!-- 渲染结果：\n" +
                "\t\t\tname : zhangsan\n" +
                "\t\t\tage : 19\n" +
                "\t\t\tsex : 男\n" +
                "\t-->\n" +
                "\n" +
                "  <!-- 形式3：遍历对象的所有属性及其值及其下标 -->\n" +
                "  <!-- 参数：值，属性(键)，下标 -->\n" +
                "  <div v-for=\"(val, key, index) in obj\">\n" +
                "    {{ index }}, {{ key }} : {{ val }}\n" +
                "  </div>\n" +
                "\t<!-- 渲染结果：\n" +
                "\t\t\t0, name : zhangsan\n" +
                "\t\t\t1, age : 19\n" +
                "\t\t\t2, sex : 男\n" +
                "\t-->\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      obj: {\n" +
                "        name: 'zhangsan',\n" +
                "        age: 19,\n" +
                "        sex: '男'\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "### 2.2.3 v-for 遍历 number\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <!-- 注意：n从1开始 -->\n" +
                "  <span v-for=\"n in 5\">\n" +
                "    {{ n }}\n" +
                "  </span>\n" +
                "\t<!-- 渲染结果：12345 -->\n" +
                "</template>\n" +
                "```\n" +
                "\n" +
                "### 2.2.4 v-for 遍历 string\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <!-- 形式1：遍历字符串单个字符 -->\n" +
                "  <span v-for=\"ch in 'HelloVue'\">\n" +
                "    {{ ch }}\n" +
                "  </span>\n" +
                "\t<!-- 渲染结果：HelloVue -->\n" +
                "\n" +
                "  <!-- 形式2：遍历字符串单个字符及其下标-->\n" +
                "  <span v-for=\"(ch, index) in 'HelloVue'\">\n" +
                "    {{ index }}{{ ch }} {{}}\n" +
                "  </span>\n" +
                "  <!-- 渲染结果：0H 1e 2l 3l 4o 5V 6u 7e-->\n" +
                "</template>\n" +
                "```\n" +
                "\n" +
                "### 2.2.5 v-for 遍历 Iterable\n" +
                "\n" +
                "## 2.3 v-for 其它使用场景\n" +
                "\n" +
                "### 2.3.1 模板上使用 v-for\n" +
                "\n" +
                "```tsx\n" +
                "<template v-for=\"todo in todos\" :key=\"todo.name\">\n" +
                "  <li>{{ todo.name }}</li>\n" +
                "</template>\n" +
                "```\n" +
                "\n" +
                "### 2.3.2 组件上使用 v-for\n" +
                "\n" +
                "```tsx\n" +
                "<MyComponent v-for=\"item in items\" :key=\"item.id\" />\n" +
                "```\n" +
                "\n" +
                "## 2.4 v-if 和 v-for 对比\n" +
                "\n" +
                "v-if 的优先级比 v-for 优先级更高。\n" +
                "\n" +
                "```tsx\n" +
                "<!-- 一个错误的思路，v-if优先级更高，访问不到v-for中的数据 -->\n" +
                "<span v-for=\"n in nums\" v-if=\"n % 2 == 0\">\n" +
                "\t{{ n }}\n" +
                "</span>\n" +
                "```\n" +
                "\n" +
                "将上述更改：\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <span v-for=\"n in nums\">\n" +
                "    <span v-if=\"n % 2 == 0\">\n" +
                "      {{ n }}\n" +
                "    </span>\n" +
                "  </span>\n" +
                "\t<!-- 渲染结果：42 -->\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      nums: [1, 3, 4, 7, 2, 7]\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```", "# 1 事件处理概述\n" +
                "\n" +
                "## 1.1 事件处理简介\n" +
                "\n" +
                "在 Vue 中，可以使用 `v-on` 指令（简写 `@`）来监听 DOM 事件，并在事件触发时执行对应的 JavaScript 方法。\n" +
                "\n" +
                "用法：`v-on:click=”methodName”` 或 `@click=“handler”` 。\n" +
                "\n" +
                "## 1.2 v-on 指令\n" +
                "\n" +
                "给元素绑定事件监听器。\n" +
                "\n" +
                "- **缩写：**`@`\n" +
                "- **期望的绑定值类型：**`Function | Inline Statement | Object (不带参数)`\n" +
                "- **参数：**`event` (使用对象语法则为可选项)\n" +
                "- **修饰符：**\n" +
                "    - `.stop` ——调用 `event.stopPropagation()`。\n" +
                "    - `.prevent` ——调用 `event.preventDefault()`。\n" +
                "    - `.capture` ——在捕获模式添加事件监听器。\n" +
                "    - `.self` ——只有事件从元素本身发出才触发处理函数。\n" +
                "    - `.{keyAlias}` ——只在某些按键下触发处理函数。\n" +
                "    - `.once` ——最多触发一次处理函数。\n" +
                "    - `.left` ——只在鼠标左键事件触发处理函数。\n" +
                "    - `.right` ——只在鼠标右键事件触发处理函数。\n" +
                "    - `.middle` ——只在鼠标中键事件触发处理函数。\n" +
                "    - `.passive` ——通过 `{ passive: true }` 附加一个 DOM 事件。\n" +
                "- **详细信息**\n" +
                "    \n" +
                "    事件类型由参数来指定。表达式可以是一个方法名，一个内联声明，如果有修饰符则可省略。\n" +
                "    \n" +
                "    当用于普通元素，只监听**原生 DOM 事件**。当用于自定义元素组件，则监听子组件触发的**自定义事件**。\n" +
                "    \n" +
                "    当监听原生 DOM 事件时，方法接收原生事件作为唯一参数。如果使用内联声明，声明可以访问一个特殊的 `$event` 变量：`v-on:click=\"handle('ok', $event)\"`。\n" +
                "    \n" +
                "    `v-on` 还支持绑定不带参数的事件/监听器对的对象。请注意，当使用对象语法时，不支持任何修饰符。\n" +
                "    \n" +
                "\n" +
                "# 2 事件处理器\n" +
                "\n" +
                "## 2.1 **内联事件处理器**\n" +
                "\n" +
                "事件被触发时执行的内联 JavaScript 语句 (与 `onclick` 类似)。\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <button @click=\"count++\">点击count+1</button>\n" +
                "  <p>count: {{ count }}</p>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      count: 0\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "## 2.2 **方法事件处理器**\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <button @click=\"greet\">Greet</button>\n" +
                "  <span>count: {{ count }}</span>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      name: 'Vue.js',\n" +
                "      count: 0\n" +
                "    }\n" +
                "  },\n" +
                "  methods: {\n" +
                "    greet(event) {\n" +
                "      this.count++\n" +
                "      alert(`Hello ${this.name}!`)\n" +
                "      if (event) {\n" +
                "        alert(event.target.tagName)\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "# 3 内联事件处理器的应用\n" +
                "\n" +
                "## 3.1 ****在内联****事件****处理器中调用方法****\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <button @click=\"say('hello')\">button: {{ msg }}</button>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "      msg: ''\n" +
                "    }\n" +
                "  },\n" +
                "  methods: {\n" +
                "    say(message) {\n" +
                "      this.msg = message\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "## 3.2 ****在内联事件处理器中访问事件参数****\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <!-- 1. 使用特殊的 $event 变量 -->\n" +
                "  <button @click=\"warn('Form cannot be submitted yet.', $event)\">\n" +
                "    Submit\n" +
                "  </button>\n" +
                "\n" +
                "  <!-- 2. 使用内联箭头函数 -->\n" +
                "  <button @click=\"(event) => warn('Form cannot be submitted yet.', event)\">\n" +
                "    Submit\n" +
                "  </button>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  methods: {\n" +
                "    warn(message, event) {\n" +
                "      // 这里可以访问 DOM 原生事件\n" +
                "      if (event) {\n" +
                "        event.preventDefault()\n" +
                "      }\n" +
                "      alert(message)\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "# 4 事件修饰符\n" +
                "\n" +
                "## 4.1 普通事件修饰符\n" +
                "\n" +
                "Vue 为 `v-on` 提供了**普通事件修饰符**。修饰符是用 `.` 表示的指令后缀，包含以下这些：\n" +
                "\n" +
                "- `.stop` ：阻止事件冒泡，即阻止事件传播。\n" +
                "- `.prevent` ：\n" +
                "- `.self`\n" +
                "- `.capture`\n" +
                "- `.once`\n" +
                "- `.passive`\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <div @click=\"doThis\">\n" +
                "    <button @click.stop=\"doThat\">click</button>\n" +
                "    <!-- 阻止了事件冒泡，=> doThat-->\n" +
                "  </div>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  methods: {\n" +
                "    doThis() {\n" +
                "      console.log('doThis')\n" +
                "    },\n" +
                "    doThat() {\n" +
                "      console.log('toThat')\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "## 4.2 键盘按键修饰符\n" +
                "\n" +
                "在监听键盘按键事件时，常常会用到键盘按键修饰符来检查键盘按键事件。\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <label for=\"button\">按下Enter键发出信息</label>\n" +
                "\t<!-- 当且仅当按下'enter'键时才调用'submit' -->\n" +
                "  <input id=\"button\" @keyup.enter=\"submit\"/>\n" +
                "  <div>message: {{ msg }}</div>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {   \n" +
                "    return {\n" +
                "      msg: ''\n" +
                "    }\n" +
                "  },\n" +
                "  methods: {\n" +
                "    submit() {\n" +
                "      this.msg = document.getElementById(\"button\").value\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/0a10a8df-17c4-4254-9c28-ac8eb66f970e/Untitled.png)\n" +
                "\n" +
                "### 4.2.1 常用键盘按键别名\n" +
                "\n" +
                "Vue 为一些常用的按键提供了别名：\n" +
                "\n" +
                "- `.enter`\n" +
                "- `.tab`\n" +
                "- `.delete` (捕获“Delete”和“Backspace”两个按键)\n" +
                "- `.esc`\n" +
                "- `.space`\n" +
                "- `.up`\n" +
                "- `.down`\n" +
                "- `.left`\n" +
                "- `.right`\n" +
                "\n" +
                "### 4.2.2 系统键盘按键修饰符\n" +
                "\n" +
                "你可以使用以下系统按键修饰符来触发鼠标或键盘事件监听器，只有当按键被按下时才会触发。\n" +
                "\n" +
                "- `.ctrl`\n" +
                "- `.alt`\n" +
                "- `.shift`\n" +
                "- `.meta`\n" +
                "\n" +
                "## 4.3 鼠标按键修饰符\n" +
                "\n" +
                "鼠标按键修饰符是用来监听鼠标按键事件的。\n" +
                "\n" +
                "鼠标按键修饰符有三种：\n" +
                "\n" +
                "- `.left`\n" +
                "- `.right`\n" +
                "- `.middle`\n" +
                "\n" +
                "```tsx\n" +
                "<template>\n" +
                "  <label>单击下方区域，左键变红，右键变蓝</label>\n" +
                "  <div :style=\"baseStyle\" @mousedown.left=\"toRed\" @mousedown.right=\"toBlue\"></div>\n" +
                "</template>\n" +
                "<script>\n" +
                "export default {\n" +
                "  data() {   \n" +
                "    return {\n" +
                "      baseStyle: {\n" +
                "        width: '200px',\n" +
                "        height: '200px',\n" +
                "        backgroundColor: 'black'\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  methods: {\n" +
                "    toRed() {\n" +
                "      this.baseStyle.backgroundColor = 'red'\n" +
                "    },\n" +
                "    toBlue() {\n" +
                "      this.baseStyle.backgroundColor = 'blue'\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "```\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/a2464667-63bf-4230-96e4-f1dffde8831e/Untitled.png)\n" +
                "\n" +
                "![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/4172b89c-2153-40e7-82a6-62f1bdeba80e/Untitled.png)", "## 基本概念\n" +
                "\n" +
                "在  Java 中，NIO 代表非阻塞式 IO（Non-blocking IO），它是 Java 标准库提供的一种处理输入输出的方式。\n" +
                "\n" +
                "传统的 Java IO（或称为阻塞IO）通常涉及到使用 InputStream 和 OutputStream 类进行读写操作。在阻塞 IO 中，当一个线程执行读写操作时，它将一直等待直到完成操作，期间无法执行其他任务。这种阻塞式的 IO 模型在高并发环境下可能导致性能问题，因为线程会被长时间地阻塞。\n" +
                "\n" +
                "相比之下，NIO 引入了一种基于通道（Channel）和缓冲区（Buffer）的 IO 模型。它允许使用单个线程管理多个连接，并使用选择器（Selector）监控这些连接的 IO 事件。\n" +
                "\n" +
                "- Channel：通道，负责数据传输。具有多种类型，例如文件通道 FileChannel，网络通道 DatagramSocketChannel、SocketChannel、ServerSocketChannel。\n" +
                "- Buffer：缓冲区，负责数据存储。具有多种类型，例如 ByteBuffer、ShortBuffer、IntBuffer、FloatBuffer、CharBuffer 等。\n" +
                "- Selector：选择器，负责监控 IO 事件。具有多种事件类型，例如 Accpet、Connect、Write、Read 事件。\n" +
                "\n" +
                "NIO 的主要优势在于：\n" +
                "\n" +
                "- 高性能：非阻塞 IO 模型使得一个线程可以同时管理多个连接，减少了线程切换和上下文切换带来的开销。\n" +
                "- 异步操作：NIO 提供了异步 IO 的支持，通过 Future、CompletionHandler 等机制实现了异步操作，使得 IO 操作不再阻塞线程。\n" +
                "\n" +
                "NIO 在网络编程、服务器开发和高并发系统中广泛应用。例如，可以使用NIO来构建高性能的网络服务器、实现多路复用的网络通信、开发异步的网络客户端等。\n" +
                "\n" +
                "## Channel\n" +
                "\n" +
                "Channel 表示一个数据通道，用于读写数据，常用的有两大类：\n" +
                "\n" +
                "- 文件通道\n" +
                "    - FileChannel\n" +
                "- 网络通道\n" +
                "    - UDP 网络通道：DatagramSocketChannel\n" +
                "    - TCP 网络通道：ServerSocketChannel 和 SocketChannel\n" +
                "\n" +
                "### FileChannel\n" +
                "\n" +
                "FileChannel 用于文件通道。\n" +
                "\n" +
                "- 【示例 1】：将 from.txt 文件的内容传输到 to.txt 文件中。\n" +
                "    \n" +
                "    ```java\n" +
                "    import java.io.FileInputStream;\n" +
                "    import java.io.FileOutputStream;\n" +
                "    import java.io.IOException;\n" +
                "    import java.nio.channels.FileChannel;\n" +
                "    \n" +
                "    public class TestFileChannel {\n" +
                "        public static void main(String[] args) {\n" +
                "            try (FileChannel from = new FileInputStream(\"from.txt\").getChannel();\n" +
                "                 FileChannel to = new FileOutputStream(\"to.txt\").getChannel()\n" +
                "            ) {\n" +
                "                from.transferTo(0, from.size(), to); // 2G 限制\n" +
                "            } catch (IOException e) {\n" +
                "                e.printStackTrace();\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    ```\n" +
                "    \n" +
                "- 【示例 2】：对【示例 1】进行改进，分成多次传输。\n" +
                "    \n" +
                "    ```java\n" +
                "    import java.io.FileInputStream;\n" +
                "    import java.io.FileOutputStream;\n" +
                "    import java.io.IOException;\n" +
                "    import java.nio.channels.FileChannel;\n" +
                "    \n" +
                "    public class TestFileChannel {\n" +
                "        public static void main(String[] args) {\n" +
                "            try (FileChannel from = new FileInputStream(\"from.txt\").getChannel();\n" +
                "                 FileChannel to = new FileOutputStream(\"to.txt\").getChannel()\n" +
                "            ) {\n" +
                "                long size = from.size();\n" +
                "                long left = size;\n" +
                "                while (left > 0) {\n" +
                "                    left -= from.transferTo(size - left, left, to);\n" +
                "                }\n" +
                "            } catch (IOException e) {\n" +
                "                e.printStackTrace();\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    ```\n" +
                "    \n" +
                "\n" +
                "### ServerSocketChannel\n" +
                "\n" +
                "ServerSocketChannel 是用于表示服务端通道的一个类。\n" +
                "\n" +
                "- 【示例】：建立一个基本的服务端数据通道 Server.java\n" +
                "    \n" +
                "    ```java\n" +
                "    import java.io.IOException;\n" +
                "    import java.net.InetSocketAddress;\n" +
                "    import java.nio.channels.ServerSocketChannel;\n" +
                "    import java.nio.channels.SocketChannel;\n" +
                "    \n" +
                "    public class Server {\n" +
                "        public static void main(String[] args) throws IOException {\n" +
                "            // 1. 打开服务端数据通道: ServerSocketChannel.open()\n" +
                "            ServerSocketChannel ssc = ServerSocketChannel.open();\n" +
                "            System.out.println(\"server open channel...\");\n" +
                "    \n" +
                "            // 2. 绑定端口号: serverSocketChannel.bind(InetSocketAddress);\n" +
                "            ssc.bind(new InetSocketAddress(9000));\n" +
                "            System.out.println(\"bind port 9000...\");\n" +
                "    \n" +
                "            // 3. 监听客户端连接: serverSocketChannel.accept()\n" +
                "            SocketChannel sc = ssc.accept();\n" +
                "            System.out.println(\"server accept connect: \" + sc);\n" +
                "    \n" +
                "            // 4. 释放 socket 通道资源\n" +
                "            sc.close();\n" +
                "            System.out.println(\"client socket channel is closed...\");\n" +
                "            ssc.close();\n" +
                "            System.out.println(\"server socket channel is closed...\");\n" +
                "        }\n" +
                "    }\n" +
                "    ```\n" +
                "    \n" +
                "\n" +
                "### SocketChannel\n" +
                "\n" +
                "SocketChannel 表示了客户端的一个 Socket 通道。\n" +
                "\n" +
                "- 【示例】：建立一个基本的客户端数据通道 Client.java\n" +
                "    \n" +
                "    ```java\n" +
                "    import java.io.IOException;\n" +
                "    import java.net.InetSocketAddress;\n" +
                "    import java.nio.channels.SocketChannel;\n" +
                "    \n" +
                "    public class Client {\n" +
                "        public static void main(String[] args) throws IOException {\n" +
                "            // 1. 打开客户端数据通道\n" +
                "            SocketChannel sc = SocketChannel.open();\n" +
                "            System.out.println(\"client open channel...\");\n" +
                "    \n" +
                "            // 2. 连接服务器\n" +
                "            sc.connect(new InetSocketAddress(\"localhost\", 9000));\n" +
                "            System.out.println(\"client connects to server...\");\n" +
                "    \n" +
                "            // 3. 释放资源\n" +
                "            sc.close();\n" +
                "            System.out.println(\"client socket channel is closed...\");\n" +
                "        }\n" +
                "    }\n" +
                "    ```\n" +
                "    \n" +
                "\n" +
                "## Buffer\n" +
                "\n" +
                "### ByteBuffer\n" +
                "\n" +
                "ByteBuffer 是以字节为单位的数据缓冲区，其为一个接口，有三种实现：\n" +
                "\n" +
                "- MappedByteBuffer：映射字节缓冲区。\n" +
                "    - 通过使用 `FileChannel.map()` 方法将文件的一部分或整个文件直接映射到内存中而创建的；\n" +
                "    - 数据存储在文件中，对文件进行读写操作会直接反映到内存中的数据；\n" +
                "    - 适用于处理较大的文件，通过内存映射可以高效地访问和修改文件内容；\n" +
                "    - 不直接受GC的影响，不会占用堆内存。\n" +
                "- DirectByteBuffer：直接字节数据缓冲区。\n" +
                "    - 通过调用 `ByteBuffer.allocateDirect()` 方法来创建的。\n" +
                "    - 数据存储在直接内存中，而不是在JVM的堆上。\n" +
                "    - 相对于HeapByteBuffer，DirectByteBuffer可以更快地与底层IO设备进行交互。\n" +
                "    - 对于需要频繁的IO操作以及需要避免堆内存复制的场景，DirectByteBuffer通常更为高效。\n" +
                "- HeapByteBuffer：堆字节缓冲区。\n" +
                "    - 通过调用 `ByteBuffer.allocate()` 方法来创建的。\n" +
                "    - 数据存储在JVM的堆上，即普通的Java对象，受到GC的管理。\n" +
                "    - 相比于DirectByteBuffer，HeapByteBuffer可能会慢一些，并且涉及数据从堆内存到直接内存的复制。\n" +
                "    - 由于受到GC的影响，HeapByteBuffer可能在进行大量IO操作时会有一些性能损耗。\n" +
                "\n" +
                "ByteBuffer 的使用方式如下：\n" +
                "\n" +
                "1. 向 buffer 写入数据，例如调用 channel.read(buffer)\n" +
                "2. 调用 flip() 切换至读模式\n" +
                "    - flip会使得buffer中的limit变为position，position变为0\n" +
                "3. 从 buffer 读取数据，例如调用 buffer.get()\n" +
                "4. 调用 clear() 或者 compact() 切换至写模式\n" +
                "    - 调用clear() 方法时position=0，limit变为capacity\n" +
                "    - 调用compact()方法时，会将缓冲区中的未读数据压缩到缓冲区前面\n" +
                "5. 重复以上步骤\n" +
                "\n" +
                "ByteBuffer 具有 4 个核心属性：\n" +
                "\n" +
                "```java\n" +
                "private int mark = -1;\n" +
                "private int position = 0;\n" +
                "private int limit;\n" +
                "private int capacity;\n" +
                "```\n" +
                "\n" +
                "分别如下：\n" +
                "\n" +
                "- **capacity**：缓冲区的容量，通过构造函数进行设置，无法动态修改。\n" +
                "- **limit**：缓冲区的界限。对于读模式和写模式有各自的 limit。\n" +
                "- **position**：下一个数据索引。\n" +
                "- **mark**：记录当前 position 的值，用于调用 reset() 方法恢复到 mark 的位置。\n" +
                "\n" +
                "ByteBuffer 具有多个核心方法，共同实现了一个缓冲区下进行读/写数据操作：\n" +
                "\n" +
                "- `put()、get()`：put() 将数据写入 position 位置，并且 position 自增；get() 将数据从 position 位置取出，并且 position 自增；其中 get(i) 不移动 position 位置，仅取出数据。\n" +
                "    \n" +
                "    ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/37e853d1-951f-4729-90d2-51295878ecf0/Untitled.png)\n" +
                "    \n" +
                "- `flip()`：翻转缓冲区，limit = position，position = 0；\n" +
                "    \n" +
                "    ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/4d2f33e3-e3fb-49d7-8a2e-015a79ed4d6f/Untitled.png)\n" +
                "    \n" +
                "- `compact()`：把未读完的数据（position ~ limit - 1）向前压缩，并将 position 切换到 limit - position + 1，limit = capacity。工作示意图如下：\n" +
                "    \n" +
                "    ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/6d4bd010-784c-43a3-8894-bd3bac7f2730/Untitled.png)\n" +
                "    \n" +
                "- `rewind()`：重置缓冲区起始位置，position = 0，mark = -1。\n" +
                "    \n" +
                "    ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/6ab227ba-a3b8-4076-9204-42d89d430a02/Untitled.png)\n" +
                "    \n" +
                "- `clear()`：清空缓冲区（数据不清空，只是 position = 0，limit = capacity）。工作示意图如下：\n" +
                "    \n" +
                "    ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/b9407757-6d41-4cb0-83ff-ed8c377ce8f9/Untitled.png)\n" +
                "    \n" +
                "- `mark()、reset()`：使用 mark() 标记当前 position 的位置，然后继续读数据，之后可以通过 reset() 方法回复到 mark 所标记的 position 位置。工作示意图如下：\n" +
                "    \n" +
                "    ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/8d21aae3-9cfc-4538-a88e-4ba3bb46b73e/Untitled.png)\n" +
                "    \n" +
                "- 【**小技巧**】ByteBuffer 与 String 的相互转换\n" +
                "    \n" +
                "    ```java\n" +
                "    // 1. String 转 ByteBuffer\n" +
                "    ByteBuffer byteBuffer = Charset.defaultCharset().encode(str);\n" +
                "    \n" +
                "    // 2. ByteBuffer 转 String\n" +
                "    String str = Charset.defaultCharset().decode(byteBuffer).toString();\n" +
                "    \n" +
                "    // 3. 使用 new String(byte[])\n" +
                "    byte[] bytes = new byte[byteBuffer.capacity()];\n" +
                "    byteBuffer.get(bytes);\n" +
                "    String str = new String(bytes);\n" +
                "    ```\n" +
                "    \n" +
                "- 【示例 1】：客户端向服务端发送消息 “hello, server, i can peek, how do you?”，服务端每次接收 16 个字节并在控制台输出显示。\n" +
                "    - Server.java\n" +
                "        \n" +
                "        ```java\n" +
                "        import java.io.IOException;\n" +
                "        import java.net.InetSocketAddress;\n" +
                "        import java.net.SocketException;\n" +
                "        import java.nio.ByteBuffer;\n" +
                "        import java.nio.channels.ServerSocketChannel;\n" +
                "        import java.nio.channels.SocketChannel;\n" +
                "        import java.nio.charset.Charset;\n" +
                "        \n" +
                "        public class Server {\n" +
                "            public static void main(String[] args) throws IOException {\n" +
                "                // 1. 打开服务端数据通道\n" +
                "                ServerSocketChannel ssc = ServerSocketChannel.open();\n" +
                "                System.out.println(\"server open channel...\");\n" +
                "        \n" +
                "                // 2. 绑定端口号\n" +
                "                ssc.bind(new InetSocketAddress(9000));\n" +
                "                System.out.println(\"bind port 9000...\");\n" +
                "        \n" +
                "                // 3. 监听客户端连接\n" +
                "                SocketChannel sc = ssc.accept();\n" +
                "                System.out.println(\"server accept connect: \" + sc);\n" +
                "        \n" +
                "                // 4. 创建缓冲区对象\n" +
                "                ByteBuffer byteBuffer = ByteBuffer.allocate(16);\n" +
                "        \n" +
                "                try { // 处理客户端端口连接时的异常\n" +
                "                    // 5. 分成多次缓冲存储数据\n" +
                "                    int read;\n" +
                "                    while ((read = sc.read(byteBuffer)) != -1) {\n" +
                "                        byteBuffer.flip();\n" +
                "                        System.out.println(\"byte count: \" + read + \", content: \" + Charset.defaultCharset().decode(byteBuffer).toString());\n" +
                "                        byteBuffer.clear();\n" +
                "                    }\n" +
                "                } catch (SocketException e) {\n" +
                "                    System.out.println(\"client is closed...\");\n" +
                "                } finally {\n" +
                "                    sc.close();\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        ```\n" +
                "        \n" +
                "    - Client.java\n" +
                "        \n" +
                "        ```java\n" +
                "        import java.io.IOException;\n" +
                "        import java.net.InetSocketAddress;\n" +
                "        import java.nio.ByteBuffer;\n" +
                "        import java.nio.channels.SocketChannel;\n" +
                "        import java.nio.charset.Charset;\n" +
                "        \n" +
                "        public class Client {\n" +
                "            public static void main(String[] args) throws IOException {\n" +
                "                // 1. 创建 socket\n" +
                "                SocketChannel sc = SocketChannel.open();\n" +
                "                // 2. 连接服务端\n" +
                "                sc.connect(new InetSocketAddress(\"localhost\", 9000));\n" +
                "                // 3. 创建 byteBuffer\n" +
                "                ByteBuffer byteBuffer = Charset.defaultCharset().encode(\"hello, server, i can peek, how do you?\");\n" +
                "                // 4. 发送 byteBuffer\n" +
                "                sc.write(byteBuffer);\n" +
                "            }\n" +
                "        }\n" +
                "        ```\n" +
                "        \n" +
                "- 【示例 2】：多个客户端向服务端发送消息 “hello, i speak 你好, how do you?”，服务端每次接收 16 个字节并在控制台输出显示。\n" +
                "    - Server.java\n" +
                "        \n" +
                "        ```java\n" +
                "        import java.io.IOException;\n" +
                "        import java.net.InetSocketAddress;\n" +
                "        import java.net.SocketException;\n" +
                "        import java.nio.ByteBuffer;\n" +
                "        import java.nio.channels.ServerSocketChannel;\n" +
                "        import java.nio.channels.SocketChannel;\n" +
                "        import java.nio.charset.Charset;\n" +
                "        \n" +
                "        public class Server {\n" +
                "            public static void main(String[] args) throws IOException {\n" +
                "                ServerSocketChannel ssc = ServerSocketChannel.open();\n" +
                "                ssc.bind(new InetSocketAddress(9000));\n" +
                "        \n" +
                "                while (true) {\n" +
                "                    // 1. 等待客户端连接\n" +
                "                    System.out.println(\"wait client connect...\");\n" +
                "                    SocketChannel sc = ssc.accept();\n" +
                "                    System.out.println(\"server accept connect: \" + sc);\n" +
                "                    try {\n" +
                "                        // 2. 写入缓冲区\n" +
                "                        ByteBuffer byteBuffer = ByteBuffer.allocate(16);\n" +
                "                        int count;\n" +
                "                        while ((count = sc.read(byteBuffer)) != -1) {\n" +
                "                            byteBuffer.flip();\n" +
                "                            System.out.println(\"byte count: \" + count + \", content: \" + Charset.defaultCharset().decode(byteBuffer).toString());\n" +
                "                            byteBuffer.clear();\n" +
                "                        }\n" +
                "                    } catch (SocketException e) {\n" +
                "                        System.out.println(\"client is closed...\" + sc);\n" +
                "                    } finally {\n" +
                "                        sc.close();\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        ```\n" +
                "        \n" +
                "    - Client.java\n" +
                "        \n" +
                "        ```java\n" +
                "        import java.io.IOException;\n" +
                "        import java.net.InetSocketAddress;\n" +
                "        import java.nio.ByteBuffer;\n" +
                "        import java.nio.channels.SocketChannel;\n" +
                "        import java.nio.charset.Charset;\n" +
                "        \n" +
                "        public class Client {\n" +
                "            public static void main(String[] args) throws IOException {\n" +
                "                SocketChannel sc = SocketChannel.open();\n" +
                "                sc.connect(new InetSocketAddress(\"localhost\", 9000));\n" +
                "                ByteBuffer buffer = Charset.defaultCharset().encode(\"hello, i speak 你好, how do you?\");\n" +
                "                sc.write(buffer);\n" +
                "                System.in.read();\n" +
                "            }\n" +
                "        }\n" +
                "        ```\n" +
                "        \n" +
                "\n" +
                "示例 2 存在两个问题：\n" +
                "\n" +
                "1. 【容量限制问题】：系统默认的 UTF-8 编码下，汉字占用 3 个字节，如果刚好某个汉字卡在输出一个字节上，就会显示如下图所示：\n" +
                "    \n" +
                "    ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/1d3b04be-1e0c-4f99-828a-ca28fc051760/Untitled.png)\n" +
                "    \n" +
                "    <aside>\n" +
                "    \uD83D\uDD14 解决方法：手动扩容。\n" +
                "    \n" +
                "    </aside>\n" +
                "    \n" +
                "2. 【线程阻塞问题】：当客户端 1 连接服务端后，客户端 1 没有发送数据，客户端 2 无法连接上服务端，这是因为 `sc.read(byteBuffer)` 会阻塞程序，导致服务器无法处理多个客户端的连接。\n" +
                "    \n" +
                "    <aside>\n" +
                "    \uD83D\uDD14 解决方法：利用 `sc.configureBlocking(false)` 设置非阻塞模式结合 `selector` 的 `ACCEPT` 事件来实现。\n" +
                "    \n" +
                "    </aside>\n" +
                "    \n" +
                "\n" +
                "# Selector\n" +
                "\n" +
                "默认情况下，serverSocketChannel.accept() 和 socketChannel.read() 方法都是阻塞的：\n" +
                "\n" +
                "- serverSocketChannel.accept() 在无客户端连接时进入阻塞。\n" +
                "- socketChannel.read() 在无数据传输时进入阻塞。\n" +
                "\n" +
                "阻塞情况下，程序几乎无法继续执行其它请求，需要多线程支持。\n" +
                "\n" +
                "尽管可以使用 `ssc.configureBlocking(false)` 以及 `socketChannel.configureBlocking(false)` 设置为非阻塞模型，但是服务端为了处理多个客户端连接请求依然会调用 `while (true)` 使得程序一直运行，耗费资源。\n" +
                "\n" +
                "Selector 可以配合单线程完成对多个 Channel 的事件监控，一旦 Channel 发生特定事件，Selector 就会产生响应，这种模型成为【多路复用】。事件类型包括：\n" +
                "\n" +
                "- `ACCEPT`：服务端接受连接时触发。\n" +
                "- `CONNECT`：客户端连接时触发。\n" +
                "- `READ`：可读数据时触发。\n" +
                "- `WRITE`：可写数据时触发。\n" +
                "\n" +
                "Selector 需要配合非阻塞模式在网络通道下进行使用，这是因为阻塞模式会限制程序运行，并且文件通道没有这些事件类型。\n" +
                "\n" +
                "### ACCEPT 事件\n" +
                "\n" +
                "ACCEPT 事件可以在服务器接收客户端连接时进行事件处理。\n" +
                "\n" +
                "- 【示例】利用 ACCEPT 事件响应多个客户端请求。\n" +
                "    - Server.java\n" +
                "        \n" +
                "        ```java\n" +
                "        import java.io.IOException;\n" +
                "        import java.net.InetSocketAddress;\n" +
                "        import java.nio.channels.*;\n" +
                "        import java.util.Iterator;\n" +
                "        import java.util.Set;\n" +
                "        \n" +
                "        public class Server {\n" +
                "            public static void main(String[] args) throws IOException {\n" +
                "                try (ServerSocketChannel server = ServerSocketChannel.open()) {\n" +
                "                    server.bind(new InetSocketAddress(9000));\n" +
                "                    server.configureBlocking(false);\n" +
                "        \n" +
                "                    // 1. 创建选择器\n" +
                "                    Selector selector = Selector.open();\n" +
                "        \n" +
                "                    // 2. 注册 ACCEPT 事件\n" +
                "                    server.register(selector, SelectionKey.OP_ACCEPT);\n" +
                "        \n" +
                "                    while (true) {\n" +
                "        \n" +
                "                        // 3. 监听多个事件\n" +
                "                        int select = selector.select();\n" +
                "                        System.out.println(\"select count: \" + select);\n" +
                "        \n" +
                "                        // 4. 获取所有事件\n" +
                "                        Set<SelectionKey> selectionKeys = selector.selectedKeys();\n" +
                "        \n" +
                "                        // 5. 遍历事件\n" +
                "                        Iterator<SelectionKey> iterator = selectionKeys.iterator();\n" +
                "                        while (iterator.hasNext()) {\n" +
                "                            SelectionKey key = iterator.next();\n" +
                "        \n" +
                "                            if (key.isAcceptable()) {\n" +
                "                                ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();\n" +
                "                                SocketChannel clientChannel = serverChannel.accept();\n" +
                "                                System.out.println(\"accept: \" + clientChannel);\n" +
                "                            }\n" +
                "        \n" +
                "                            // 6. 删除事件\n" +
                "                            iterator.remove();\n" +
                "                        }\n" +
                "                    }\n" +
                "                } catch (IOException e) {\n" +
                "                    e.printStackTrace();\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        ```\n" +
                "        \n" +
                "    - Client.java\n" +
                "        \n" +
                "        ```java\n" +
                "        import java.io.IOException;\n" +
                "        import java.net.InetSocketAddress;\n" +
                "        import java.nio.ByteBuffer;\n" +
                "        import java.nio.channels.SocketChannel;\n" +
                "        import java.nio.charset.Charset;\n" +
                "        \n" +
                "        public class Client {\n" +
                "            public static void main(String[] args) throws IOException {\n" +
                "                SocketChannel sc = SocketChannel.open();\n" +
                "                sc.connect(new InetSocketAddress(\"localhost\", 9000));\n" +
                "                System.in.read();\n" +
                "            }\n" +
                "        }\n" +
                "        ```\n" +
                "        \n" +
                "    \n" +
                "    示例中需要值得注意的是：在处理完一个事件后，需要删除这个事件，否则在后续如果激发了其它事件，就会出现 clientChannel 为空的异常。例如下列代码：\n" +
                "    \n" +
                "    ```java\n" +
                "    if (key.isAcceptable()) {\n" +
                "        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();\n" +
                "        SocketChannel clientChannel = serverChannel.accept();\n" +
                "        System.out.println(\"accept: \" + clientChannel);\n" +
                "    \n" +
                "        // 将客户端通道注册为可读事件\n" +
                "        clientChannel.configureBlocking(false);\n" +
                "        clientChannel.register(selector, SelectionKey.OP_READ);\n" +
                "    } else if (key.isReadable()) {\n" +
                "        //...\n" +
                "    }\n" +
                "    \n" +
                "    // iterator.remove();\n" +
                "    ```\n" +
                "    \n" +
                "    当客户端再触发出一个 READ 事件时，由于原先的 ACCEPT 事件暂存，导致继续执行 key.isAcceptable() 逻辑，从而出现错误。\n" +
                "    \n" +
                "\n" +
                "### READ 事件\n" +
                "\n" +
                "READ 事件能够让服务端监听到客户端向服务器发送数据，从而触发 READ 事件。\n" +
                "\n" +
                "- 【示例】使用 READ 事件，处理多个客户端请求发来的数据。\n" +
                "    - Server.java\n" +
                "        \n" +
                "        ```java\n" +
                "        import java.io.IOException;\n" +
                "        import java.net.InetSocketAddress;\n" +
                "        import java.nio.ByteBuffer;\n" +
                "        import java.nio.channels.*;\n" +
                "        import java.nio.charset.Charset;\n" +
                "        import java.util.Iterator;\n" +
                "        import java.util.Set;\n" +
                "        \n" +
                "        public class Server {\n" +
                "            public static void main(String[] args) {\n" +
                "                try (ServerSocketChannel server = ServerSocketChannel.open()) {\n" +
                "                    server.bind(new InetSocketAddress(9000));\n" +
                "                    // 1. 注册 Selector\n" +
                "                    Selector selector = Selector.open();\n" +
                "        \n" +
                "                    // 2. 注册 Accept\n" +
                "                    server.configureBlocking(false);\n" +
                "                    server.register(selector, SelectionKey.OP_ACCEPT);\n" +
                "        \n" +
                "                    while (true) {\n" +
                "                        // 3. 监听事件\n" +
                "                        int select = selector.select();\n" +
                "                        System.out.println(\"select : \" + select);\n" +
                "        \n" +
                "                        // 4. 获取事件\n" +
                "                        Set<SelectionKey> selectionKeys = selector.selectedKeys();\n" +
                "        \n" +
                "                        // 5. 遍历事件\n" +
                "                        Iterator<SelectionKey> iterator = selectionKeys.iterator();\n" +
                "                        while (iterator.hasNext()) {\n" +
                "                            SelectionKey next = iterator.next();\n" +
                "        \n" +
                "                            // 6.1 处理 accept 事件\n" +
                "                            if (next.isAcceptable()) {\n" +
                "                                // 6.1.1 获取 client channel\n" +
                "                                ServerSocketChannel serverChannel = (ServerSocketChannel) next.channel();\n" +
                "                                SocketChannel clientChannel = serverChannel.accept();\n" +
                "                                System.out.println(\"accept: \" + clientChannel);\n" +
                "        \n" +
                "                                // 6.1.2 注册 read 事件\n" +
                "                                clientChannel.configureBlocking(false);\n" +
                "                                clientChannel.register(selector, SelectionKey.OP_READ);\n" +
                "                            }\n" +
                "        \n" +
                "                            // 6.2 处理 read 事件\n" +
                "                            if (next.isReadable()) {\n" +
                "                                // 6.2.1 获取 client channel\n" +
                "                                SocketChannel clientChannel = (SocketChannel) next.channel();\n" +
                "                                System.out.println(\"read: \" + clientChannel);\n" +
                "        \n" +
                "                                // 6.2.2 缓冲区暂存\n" +
                "                                ByteBuffer buffer = ByteBuffer.allocate(1024);\n" +
                "        \n" +
                "                                // 6.2.3 接收数据\n" +
                "                                int read = clientChannel.read(buffer);\n" +
                "        \n" +
                "                                // (6.2.4) 特别：客户端关闭也会触发依次 read 事件\n" +
                "                                if (read == -1) {\n" +
                "                                    clientChannel.close();\n" +
                "                                    System.out.println(\"read-closed: \" + clientChannel);\n" +
                "                                } else {\n" +
                "                                    buffer.flip();\n" +
                "                                    System.out.println(\"read-receives: \" + Charset.defaultCharset().decode(buffer));\n" +
                "                                    buffer.clear();\n" +
                "                                }\n" +
                "                            }\n" +
                "        \n" +
                "                            // 7 删除事件\n" +
                "                            iterator.remove();\n" +
                "                        }\n" +
                "                    }\n" +
                "                } catch (IOException e) {\n" +
                "                    e.printStackTrace();\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        ```\n" +
                "        \n" +
                "    - Client.java\n" +
                "        \n" +
                "        ```java\n" +
                "        import java.io.IOException;\n" +
                "        import java.net.InetSocketAddress;\n" +
                "        import java.nio.channels.SocketChannel;\n" +
                "        import java.nio.charset.Charset;\n" +
                "        \n" +
                "        public class Client {\n" +
                "            public static void main(String[] args) {\n" +
                "                try (SocketChannel client = SocketChannel.open()) {\n" +
                "                    client.connect(new InetSocketAddress(\"localhost\", 9000));\n" +
                "                    System.in.read();\n" +
                "                    client.write(Charset.defaultCharset().encode(\"hello\"));\n" +
                "                    System.out.println(\"client is closed...\");\n" +
                "                } catch (IOException e) {\n" +
                "                    throw new RuntimeException(e);\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        ```\n" +
                "        \n" +
                "    \n" +
                "    需要注意：客户端关闭连接时也会触发一次 READ 事件。\n" +
                "    \n" +
                "\n" +
                "### WRITE 事件\n" +
                "\n" +
                "WRITE 事件在当服务器在向客户端发送数据时触发。 \n" +
                "\n" +
                "- 【示例】当客户端连接上服务器后，服务器向客户端发送 `10000000`个字节数据，要求客户端每次最多接收 `1024 * 1024` 字节。", "## 1 概述\n" +
                "\n" +
                "### 单例模式的简介\n" +
                "\n" +
                "**单例模式（Singleton Pattern）**是一种创建型设计模式，它确保一个类只有一个实例，并提供全局访问点。该模式的核心思想是：定义一个类，将其构造方法私有化，让外界无法访问，在类的内部初始化该单例对象，同时提供一个供外部使用的方法，将此单例对象提供给外界访问。\n" +
                "\n" +
                "### 单例模式的实现方式\n" +
                "\n" +
                "单例模式的实现方式有很多种，这里列举 7 种单例模式的实现：\n" +
                "\n" +
                "1. 饿汉式单例模式：类在加载时就已经创建了全局唯一实例，等待被调用，因此称为“饿汉式”。\n" +
                "2. 懒汉式单例模式：类在第一次被使用时才创建实例，因此称为”懒汉式“，也称为“延迟实例化”。\n" +
                "3. 加锁式单例模式：由于懒汉式单例模式在线程上不安全，此方式对其进行了改进，使其线程安全化。\n" +
                "4. 双重检查锁定式单例模式：在加锁式单例模式上进一步进行改进，减少了线程资源的消耗。\n" +
                "5. 静态内部类式单例模式：静态内部类只有在被引用时才会被加载，因此可以实现延迟实例化。此外，由于JVM对类的加载和初始化是线程安全的，因此这种方式也是线程安全的。\n" +
                "6. ThreadLocal 式单例模式：ThreadLocal 可以实现每个线程独立地持有一个对象实例，因此可以实现线程安全的单例模式。\n" +
                "7. 枚举类式单例模式：枚举类型在 Java 中天生就是单例的，因为枚举类型的构造函数只会被执行一次，且枚举类型是线程安全的。\n" +
                "\n" +
                "这 7 种单例模式如下：\n" +
                "\n" +
                "```java\n" +
                "// 1. 饿汉式单例模式\n" +
                "public class Singleton {\n" +
                "    private static Singleton singleton = new Singleton();\n" +
                "    private Singleton() {}\n" +
                "    public static Singleton getInstance() {\n" +
                "        return singleton;\n" +
                "    }\n" +
                "}\n" +
                "// 2. 懒汉式单例模式\n" +
                "public class Singleton {\n" +
                "    private static Singleton singleton;\n" +
                "    private Singleton() {}\n" +
                "    public static Singleton getInstance() {\n" +
                "        if (singleton == null) {\n" +
                "            singleton = new Singleton();\n" +
                "        }\n" +
                "        return singleton;\n" +
                "    }\n" +
                "}\n" +
                "// 3. 加锁式单例模式\n" +
                "public class Singleton {\n" +
                "    private static Singleton singleton;\n" +
                "    private Singleton() {}\n" +
                "\t\t// （1）在类方法上加锁\n" +
                "    public static synchronized Singleton getInstance() {\n" +
                "        if (singleton1 == null) {\n" +
                "            singleton1 = new Singleton();\n" +
                "        }\n" +
                "        return singleton;\n" +
                "    }\n" +
                "\t\t// （2）在代码块上加锁\n" +
                "\t\tpublic static synchronized Singleton getInstance() {\n" +
                "        synchronized (Singleton.class) {\n" +
                "            if (singleton == null) {\n" +
                "                singleton = new Singleton();\n" +
                "            }\n" +
                "        }\n" +
                "        return singleton;\n" +
                "    }\n" +
                "}\n" +
                "// 4. 双重检查锁定式单例模式\n" +
                "public class Singleton {\n" +
                "    private static Singleton singleton;\n" +
                "    private Singleton() {}\n" +
                "    public static Singleton getInstance() {\n" +
                "        if (singleton == null) {\n" +
                "            synchronized (Singleton.class) {\n" +
                "                if (singleton == null) {\n" +
                "                    singleton = new Singleton();\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        return singleton;\n" +
                "    }\n" +
                "}\n" +
                "// 5. 静态内部类式单例模式\n" +
                "public class Singleton {\n" +
                "    private Singleton() {}\n" +
                "    private static class SingletonHolder {\n" +
                "        private static final Singleton singleton = new Singleton();\n" +
                "    }\n" +
                "    public static Singleton getInstance() {\n" +
                "        return SingletonHolder.singleton;\n" +
                "    }\n" +
                "}\n" +
                "// 6. ThreadLocal 式单例模式\n" +
                "public class Singleton {\n" +
                "    private static final ThreadLocal<Singleton> singleton = \n" +
                "\t\t\t\t\t\tThreadLocal.withInitial(Singleton::new);\n" +
                "    private Singleton() {}\n" +
                "    public static Singleton getInstance() {\n" +
                "        return singleton.get();\n" +
                "    }\n" +
                "}\n" +
                "// 7. 枚举类式单例模式\n" +
                "public enum Singleton {\n" +
                "    SINGLETON;\n" +
                "}\n" +
                "```\n" +
                "\n" +
                "### 单例模式的应用场景\n" +
                "\n" +
                "单例模式具有下列好处：\n" +
                "\n" +
                "1. 提高资源的利用率：一个实例重复利用可以减少对象的创建，从而减少内存的消耗，提高系统资源的利用率。\n" +
                "2. 方便管理和维护：一个类全局只有一个对象，因此具有一个单一的入口点，方便对单例对象进行管理和维护。\n" +
                "3. 保证对象的一致性：一个类全局只有一个对象，可以保证单例对象的一致性和完整性，避免了因为多个实例导致数据不一致的问题。\n" +
                "4. 提高系统性能：单例对象可以被延迟初始化，只有在需要时才会被创建，提高了系统的性能。\n" +
                "\n" +
                "因此，根据上述单例模式的优点，单例模式的应用场景如下：\n" +
                "\n" +
                "1. 当一个类只需要存在一个实例时，例如全局配置类、日志记录器类等。\n" +
                "2. 当多个对象需要共享同一个资源时，例如线程池、缓存管理器等。\n" +
                "3. 当对象的创建和销毁需要耗费大量时间和资源时，例如数据库连接池等。\n" +
                "\n" +
                "### 单例模式的相关思考\n" +
                "\n" +
                "Java 中有很多地方使用了单例模式，以下是一些常见的应用场景：\n" +
                "\n" +
                "1. Runtime 类：在 Java 应用程序中，Runtime 类表示运行时环境，它是一个单例类。通过使用单例模式，可以确保整个应用程序只有一个 Runtime 实例，从而避免资源浪费和不必要的开销。\n" +
                "2. Logger 类：Logger 是 Java 日志框架中的一个重要组件，也是一个单例类。通过使用单例模式，可以确保所有的日志信息都被记录在同一个 Logger 实例中，从而方便管理和维护。\n" +
                "3. Configuration 类：在许多应用程序中，配置信息通常是存储在一个配置文件中的。为了避免频繁地读取配置文件，可以使用单例模式将配置信息缓存到内存中。\n" +
                "\n" +
                "许多与 Java 相关的框架中也用到了单例模式：\n" +
                "\n" +
                "1. Spring 框架中的 Bean 默认都是单例的，这样可以保证在整个应用程序中只有一个实例，避免了重复创建和浪费资源。\n" +
                "2. Hibernate 框架中的 SessionFactory 也使用了单例模式，这样可以确保在整个应用程序中只有一个 SessionFactory 实例，从而提高性能并避免出现线程安全问题。\n" +
                "3. Log4j 日志框架中的 LogManager 也使用了单例模式，这样可以确保在整个应用程序中只有一个 LogManager 实例，从而避免了重复创建和浪费资源。"};
        String[] type = new String[]{"architecture", "backend", "frontend", "frontend", "frontend", "frontend", "frontend", "frontend", "frontend", "backend", "design"};
        String[] tags = new String[]{"CAP,分布式", "Netty", "vue,基础语法","vue,基础语法","vue,基础语法","vue,基础语法","vue,基础语法","vue,基础语法","vue,基础语法", "nio", "设计模式,创建者模式,单例模式"};

        insertOneRecordToMySQL(titles, content, type, tags);
    }

    public void insertOneRecordToMySQL(String[] t, String[] c, String[] types, String[] tags) {
        Random random = new Random();
        List<Article> list = new ArrayList<>();
        int n = t.length;
        for (int i = 0; i < n; i++) {
            Article article = new Article();
            article.setUserId(userIds[random.nextInt(3)]);
            article.setTitle(t[i]);
            article.setContent(c[i]);
            article.setCreatedAt(randomDate());
            article.setIsGem(0);
            article.setIsTop(0);
            article.setType(types[i]);
            article.setTags(tags[i]);
            article.setScore(0.0);
            article.setTotalReply(0L);
            list.add(article);
        }
        articleService.saveBatch(list);
    }

    @Test
    public void reSetType() {
        List<Article> list = articleService.list();

        var map = new String[]{
                ArticleType.ALL,
                ArticleType.OTHER
        };
        Random random = new Random();
        for (Article p : list) {
            p.setType(map[random.nextInt(9)]);
        }

        articleService.updateBatchById(list);
    }

    @Test
    public void reSetTime() {
        List<Article> list = articleService.list();
        Random random = new Random();
        for (Article p : list) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime randomTime = now.minusDays(random.nextInt(365)).minusHours(random.nextInt(24))
                    .minusMinutes(random.nextInt(60)).minusSeconds(random.nextInt(60));
            p.setCreatedAt(Date.from(randomTime.atZone(ZoneId.systemDefault()).toInstant()));
        }
        articleService.updateBatchById(list);
    }
    String text = "万物复苏的春天，正是踔厉奋发的时节。\n" +
            "\n" +
            "　　国内生产总值同比增长4.5%，社会消费品零售总额同比增长5.8%，全国固定资产投资同比增长5.1%，货物进出口总额同比增长4.8%……4月18日公布的中国经济2023年首季成绩单，展现中国经济在高质量发展中复苏向好的良好态势。\n" +
            "\n" +
            "　　“牢牢把握高质量发展这个首要任务”“在强国建设、民族复兴的新征程，我们要坚定不移推动高质量发展”——习近平总书记的重要指示坚定明晰。\n" +
            "\n" +
            "　　奋进在全面贯彻党的二十大精神的开局之年，以习近平同志为核心的党中央带领全党全国人民，坚持稳中求进工作总基调，加快构建新发展格局，着力推动高质量发展，更好统筹发展和安全，促进经济运行整体好转，推动中国经济巨轮乘风破浪、稳健前进，为全面推进中国式现代化提供更加强劲的发展动力。";
    @Test
    public void reSetContent() {
        List<Article> list = articleService.list();
        Random random = new Random();
        for (Article p : list) {
            p.setContent(getRandomSentence(text));
        }
        articleService.updateBatchById(list);
    }
    @Test
    public void resetScore() {
        Random random = new Random();
        List<Article> list = articleService.list();
        for (Article p : list ){
            p.setScore(random.nextDouble(100));
        }
        articleService.updateBatchById(list);
    }
    public String getRandomSentence(String text) {
        // 将文本按句号分割为句子数组
        String[] sentences = text.split("\\.");

        // 使用随机数生成器获取随机索引
        Random random = new Random();
        int randomIndex = random.nextInt(sentences.length);

        // 返回随机索引对应的句子
        return sentences[randomIndex].trim();
    }
    @Resource
    private ArticleCommentService articleCommentService;
}
