package com.cj.studycirclebackend.event;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FollowUserEvent extends Event {
    private Long userFromId;
    private Long userToId;
    public FollowUserEvent(String topic, int noticeType, Long userFromId, Long userToId) {
        super(topic, noticeType);
        this.userFromId = userFromId;
        this.userToId = userToId;
    }
}
