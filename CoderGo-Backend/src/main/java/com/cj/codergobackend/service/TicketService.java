package com.cj.codergobackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.codergobackend.pojo.Ticket;
import jakarta.servlet.http.Cookie;

public interface TicketService extends IService<Ticket> {

    Cookie createTicket(String token, Long userId);

    Ticket getTicket(String token);
}
