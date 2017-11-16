package com.lm.rpc.netty.client;


import com.lm.rpc.utils.Response;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultFuture {

    public static ConcurrentHashMap<Long, DefaultFuture> ALLFUTURE = new ConcurrentHashMap<Long, DefaultFuture>(); // all

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private Response response;

    private long timeout = 2 * 60 * 1000;
    private long startTime = System.currentTimeMillis();

    public DefaultFuture(ClientRequest request) {
        ALLFUTURE.put(request.getId(), this);
    }


    public Response get() {
        lock.lock();
        try {
            while (!done()) {
                try {
                    condition.await();// 等待数据返回
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return response;
    }

    public Response get(long time) {
        lock.lock();
        try {
            while (!done()) {
                try {
                    condition.await(time, TimeUnit.SECONDS);// 等待数据返回
                    if ((System.currentTimeMillis() - startTime) > time) {
                        System.out.println("Time out!");
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return response;
    }

    private boolean done() {
        if (response == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 处理服务器返回值
     *
     * @param response
     */
    public static void receive(Response response) {
        DefaultFuture df = DefaultFuture.ALLFUTURE.get(response.getId());
        if (df != null) {
            Lock lk = df.lock;
            lk.lock();
            try {
                df.setResponse(response);
                df.condition.signal();
                DefaultFuture.ALLFUTURE.remove(df);
            } finally {
                lk.unlock();
            }
        }
        df.condition.signal();
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    static class FutureThread extends Thread {
        @Override
        public void run() {
            Set<Long> set = DefaultFuture.ALLFUTURE.keySet();
            for (Long id : set) {
                DefaultFuture df = DefaultFuture.ALLFUTURE.get(id);
                if(df == null){
                    DefaultFuture.ALLFUTURE.remove(df);
                } else {
                    if((System.currentTimeMillis() - df.startTime) > df.timeout){
                        Response resp = new Response();
                        resp.setId(id);
                        resp.setCode("11111");
                        resp.setMsg("TIME OUT😯");
                        receive(resp);
                    }
                }
            }
        }
    }

    static {
        FutureThread ft = new FutureThread();
        ft.setDaemon(true);
        ft.start();
    }
}
