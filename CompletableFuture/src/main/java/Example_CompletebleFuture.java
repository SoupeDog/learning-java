import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 描述信息：<br/>
 *
 * @author Xavier
 * @version 1.0
 * @date 2018.09.07
 * @since Jdk 1.8
 */
public class Example_CompletebleFuture {
    // completebleFuture.join()  和 completebleFuture.get() 区别 https://stackoverflow.com/questions/45490316/completablefuture-join-vs-get
    // completebleFuture 详解 https://www.jianshu.com/p/807e6822292a

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
//        Example_CompletebleFuture.自己构造CompletebleFuture对象();
//        Example_CompletebleFuture.将其他异步方法用CompletebleFuture控制();
//        Example_CompletebleFuture.任意一个方法完成就返回();
        Example_CompletebleFuture.组合多个CompletableFuture();
    }

    public static void 自己构造CompletebleFuture对象() throws IOException {
        CompletableFuture completableFuture = CompletableFuture.completedFuture("默认值").thenApplyAsync(x -> {
            System.out.println("上一个老哥给我返回了: " + x);
            System.out.println(1 / 0);
            return "上面的老哥别出错，不然我完不成我的任务了";
        }).thenApplyAsync((y) -> {
            System.out.println("上一个老哥给我返回了: " + y);
            return "哈哈，我完成了";
        }).exceptionally((Throwable ex) -> {// 这里的 ex 和上面的 s 采用了不同的写法，s 是省略返回值类型的，我只想说：“不写返回值类型的话，我想屎！”“妈爷贼~简写是真的写的快，真香~”
            System.out.println(ex.getMessage());
            return "我出错了";
        });
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(completableFuture.getNow("我还没完成"));
        System.in.read();
    }

    public static void 将其他异步方法用CompletebleFuture控制() {
        CompletableFuture completableFuture = new CompletableFuture();
        AtomicReference<Long> startTs = new AtomicReference();
        new Thread(() -> {
            try {
                startTs.set(System.currentTimeMillis());
                Thread.sleep(2000L);
                completableFuture.complete("我完成了");// 设置 completableFuture 结果并将状态设置为已完成
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        while (!completableFuture.isDone()) {// 返回 completableFuture 对象是否已完成
            System.out.println(completableFuture.getNow("我还没完成"));// 获取异步结果，如果当前未执行完成则返回入参字符串"我还没完成"(迷：甚至有时候只会返回"没完成")
        }

        System.out.println((System.currentTimeMillis() - startTs.get()) + " ms");
        System.out.println(completableFuture.getNow("我还没完成"));
    }

    public static void 任意一个方法完成就返回() throws IOException {
        CompletableFuture completableFuture1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000L);
                System.out.println("completableFuture1");
                return "1";
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "出错了";
            }
        });

        CompletableFuture completableFuture2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(500L);
                System.out.println("completableFuture2");
                return "2";
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "出错了";
            }
        });

        CompletableFuture completableFuture3 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100L);
                System.out.println("completableFuture3");
                return "3";
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "出错了";
            }
        });

        CompletableFuture completableFuture4 = completableFuture1.applyToEither(completableFuture2, (x) -> {
            System.out.println("1、2 间任意一个完成了");
            return x;
        });

        CompletableFuture completableFuture5 = completableFuture4.applyToEither(completableFuture3, (x) -> {
            System.out.println("1、2、3 间任意一个完成了");
            return x;
        });
        System.out.println("completableFuture5 得到的结果(new)：" + completableFuture5.getNow("未完成"));
        System.out.println("completableFuture5 得到的结果：" + completableFuture5.join());

//        CompletableFuture[] array = new CompletableFuture[3];
//        array[0] = completableFuture1;
//        array[1] = completableFuture2;
//        array[2] = completableFuture3;
//
//        CompletableFuture fastestOne = CompletableFuture.anyOf(array);// 另一种写法,把 CompletableFuture 对象放进数组，anyOf 为，阻塞到 数组中任意一个 CompletableFuture 已完成
//        System.out.println("fastestOne 得到的结果：" + fastestOne.join());
//        CompletableFuture.allOf(array);// 扩展，allOf 为，阻塞到 array 中所有 CompletableFuture 已完成

        System.in.read();
    }

    public static void 组合多个CompletableFuture() {
        CompletableFuture completableFuture1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(500L);
                System.out.println("completableFuture1");
                return "1";
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "出错了";
            }
        });

        CompletableFuture completableFuture2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000L);
                System.out.println("completableFuture2");
                return "2";
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "出错了";
            }
        });

        CompletableFuture completableFuture3 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(600L);
                System.out.println("completableFuture3");
                return "3";
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "出错了";
            }
        });

        CompletableFuture completableFuture4 = completableFuture1.thenCombineAsync(completableFuture2, (x, y) -> {
            return x.toString() + y.toString();
        });

        CompletableFuture completableFuture5 = completableFuture3.thenComposeAsync((x) -> CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(600L);
                return x + "5";
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "出错了";
            }
        }));

        while (!completableFuture5.isDone() || !completableFuture4.isDone()) {
            if (completableFuture5.isDone()) {
                System.out.println(completableFuture5.join());
            }
            if (completableFuture4.isDone()) {
                System.out.println(completableFuture4.join());
            }
        }
        System.out.println(completableFuture5.getNow("失败"));
    }
}
