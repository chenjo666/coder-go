package com.cj.codergobackend.event;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserFollowEvent extends Event {
    private Long userFromId;
    private Long userToId;
    public UserFollowEvent(String noticeTopic, int noticeType, Long userFromId, Long userToId) {
        super(noticeTopic, noticeType);
        this.userFromId = userFromId;
        this.userToId = userToId;
    }
}
