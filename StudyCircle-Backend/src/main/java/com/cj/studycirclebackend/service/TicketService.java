package com.cj.studycirclebackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.studycirclebackend.pojo.Ticket;
import jakarta.servlet.http.Cookie;

public interface TicketService extends IService<Ticket> {

    Cookie createTicket(String token, Long userId);

    Ticket getTicket(String token);
}
