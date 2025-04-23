package com.deer.agent;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.InetSocketAddress;

/**
 * 提供一个简单版本的http服务器，便于和外部数据交互，该代码可以放到agent启动完毕后
 */
public class SimpleHttpServer {
    public static void main(String[] args) throws IOException {
        // 创建HTTP服务器，绑定到8080端口
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        // 创建上下文，处理GET请求
        server.createContext("/api/get", new GetHandler());
        // 创建上下文，处理POST请求
        server.createContext("/api/post", new PostHandler());
        // 启动服务器
        server.setExecutor(null); // 创建默认的执行器
        server.start();
        System.out.println("服务器已启动，监听端口8080");
    }
    // 处理GET请求的Handler
    static class GetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 设置响应类型为JSON
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            String response = "{\"message\": \"这是一个GET请求的响应!\"}";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    // 处理POST请求的Handler
    static class PostHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStream inputStream = exchange.getRequestBody();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder requestBodyBuilder = new StringBuilder();
                String line;
                // 逐行读取输入流
                while ((line = reader.readLine()) != null) {
                    requestBodyBuilder.append(line).append("\n");
                }
                // 设置响应类型为JSON
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                String response = "{\"message\": \"收到POST请求，内容是: " + requestBodyBuilder.toString().trim() + "\"}";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                // 只支持POST请求的响应
                String response = "{\"error\": \"只支持POST请求!\"}";
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(405, response.length()); // 405 Method Not Allowed
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}