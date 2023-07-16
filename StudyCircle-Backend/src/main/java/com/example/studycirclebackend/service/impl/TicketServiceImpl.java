package com.example.studycirclebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studycirclebackend.dao.TicketMapper;
import com.example.studycirclebackend.pojo.Ticket;
import com.example.studycirclebackend.service.TicketService;
import org.springframework.stereotype.Service;

@Service
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket> implements TicketService {

}
