package com.cj.codergobackend;

import com.cj.codergobackend.pojo.User;
import com.cj.codergobackend.service.UserService;
import com.cj.codergobackend.util.DataUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@SpringBootTest(classes = {CoderGoBackendApplication.class})
public class UserServiceTests {
    @Resource
    private UserService userService;
    @Test
    public void testActivate() {
        userService.activate("2357808792@qq.com");
    }

    @Test
    public void insertAvatar() {
        String[] avatars = new String[]{
                "https://tse4-mm.cn.bing.net/th/id/OIP-C.nSxgkfEFwY02G0FjanNvJgAAAA?w=196&h=196&c=7&r=0&o=5&dpr=1.1&pid=1.7",
                "https://tse2-mm.cn.bing.net/th/id/OIP-C.fCcDU1opkYvK8KSsF32HNwHaHO?w=209&h=203&c=7&r=0&o=5&dpr=1.1&pid=1.7",
                "https://tse3-mm.cn.bing.net/th/id/OIP-C.7f-otWbJRToU6A6s0gNSOQAAAA?w=203&h=203&c=7&r=0&o=5&dpr=1.1&pid=1.7",
                "https://tse2-mm.cn.bing.net/th/id/OIP-C._HkvJ9TNdHIuAI1ECh3KvwAAAA?w=203&h=203&c=7&r=0&o=5&dpr=1.1&pid=1.7",
                "https://tse2-mm.cn.bing.net/th/id/OIP-C.sE-FduqXEqPWefKGWZashAAAAA?w=204&h=204&c=7&r=0&o=5&dpr=1.1&pid=1.7",
                "https://tse2-mm.cn.bing.net/th/id/OIP-C.sE-FduqXEqPWefKGWZashAAAAA?w=204&h=204&c=7&r=0&o=5&dpr=1.1&pid=1.7",
                "https://tse3-mm.cn.bing.net/th/id/OIP-C.P4lCk011sxGAjLbGKz02RgAAAA?w=204&h=204&c=7&r=0&o=5&dpr=1.1&pid=1.7",
                "https://tse1-mm.cn.bing.net/th/id/OIP-C.bHsqHLnq3P-5eHTWut8dtAAAAA?w=204&h=204&c=7&r=0&o=5&dpr=1.1&pid=1.7",
                "https://tse3-mm.cn.bing.net/th/id/OIP-C.hBt_Gll2I4heAy6DRr9oogAAAA?w=204&h=204&c=7&r=0&o=5&dpr=1.1&pid=1.7",
                "https://tse1-mm.cn.bing.net/th/id/OIP-C.fui8Wfrp0VchXm2ss6unwQAAAA?w=203&h=204&c=7&r=0&o=5&dpr=1.1&pid=1.7",
                "https://tse2-mm.cn.bing.net/th/id/OIP-C.UtwoR3My93II8kXZDoCOzAAAAA?w=204&h=204&c=7&r=0&o=5&dpr=1.1&pid=1.7"
        };
        Random r = new Random();
        List<User> list = userService.list();
        for (User user : list) {
            user.setAvatar(avatars[r.nextInt(11)]);
        }
        userService.updateBatchById(list);
    }
    @Test
    public void generateData() {
        List<User> users = new LinkedList<>();
        for (int i = 0; i < 101; i++) {
            User user = new User();
            user.setEmail("20201301" + String.format("%3d", i) + "qq.com");
            user.setActivationCode("123456");
            user.setType(1);
            user.setIsRegister(0);
            user.setCreatedAt(new Date());
            users.add(user);
        }
        userService.saveBatch(users);
    }

    @Test
    public void register() {
        for (int i = 0; i < 101; i++) {
            userService.register("20201301" + String.format("%3d", i) + "qq.com", "123456", "123456", new HttpServletResponse() {
                @Override
                public void addCookie(Cookie cookie) {

                }

                @Override
                public boolean containsHeader(String s) {
                    return false;
                }

                @Override
                public String encodeURL(String s) {
                    return null;
                }

                @Override
                public String encodeRedirectURL(String s) {
                    return null;
                }

                @Override
                public void sendError(int i, String s) throws IOException {

                }

                @Override
                public void sendError(int i) throws IOException {

                }

                @Override
                public void sendRedirect(String s) throws IOException {

                }

                @Override
                public void setDateHeader(String s, long l) {

                }

                @Override
                public void addDateHeader(String s, long l) {

                }

                @Override
                public void setHeader(String s, String s1) {

                }

                @Override
                public void addHeader(String s, String s1) {

                }

                @Override
                public void setIntHeader(String s, int i) {

                }

                @Override
                public void addIntHeader(String s, int i) {

                }

                @Override
                public void setStatus(int i) {

                }

                @Override
                public int getStatus() {
                    return 0;
                }

                @Override
                public String getHeader(String s) {
                    return null;
                }

                @Override
                public Collection<String> getHeaders(String s) {
                    return null;
                }

                @Override
                public Collection<String> getHeaderNames() {
                    return null;
                }

                @Override
                public String getCharacterEncoding() {
                    return null;
                }

                @Override
                public String getContentType() {
                    return null;
                }

                @Override
                public ServletOutputStream getOutputStream() throws IOException {
                    return null;
                }

                @Override
                public PrintWriter getWriter() throws IOException {
                    return null;
                }

                @Override
                public void setCharacterEncoding(String s) {

                }

                @Override
                public void setContentLength(int i) {

                }

                @Override
                public void setContentLengthLong(long l) {

                }

                @Override
                public void setContentType(String s) {

                }

                @Override
                public void setBufferSize(int i) {

                }

                @Override
                public int getBufferSize() {
                    return 0;
                }

                @Override
                public void flushBuffer() throws IOException {

                }

                @Override
                public void resetBuffer() {

                }

                @Override
                public boolean isCommitted() {
                    return false;
                }

                @Override
                public void reset() {

                }

                @Override
                public void setLocale(Locale locale) {

                }

                @Override
                public Locale getLocale() {
                    return null;
                }
            });
        }
    }

    @Test
    public void register2() {
        List<User> list = userService.list();
        for (User u : list) {
            u.setUsername(DataUtil.generateRandomUsername());
            u.setEmail(u.getEmail().replaceAll(" ","0"));
        }
        userService.updateBatchById(list);
    }
    @Test
    public void register3() {
        Random random = new Random();
        List<User> list = userService.list();
        for (User u : list) {
            u.setType(random.nextInt(2));
        }
        userService.updateBatchById(list);
    }
}
