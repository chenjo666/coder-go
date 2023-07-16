package com.example.studycirclebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class Event {
    private String topic;
    private Long userFromId;
    private Long userToId;
    private Long objectId;
    private String objectType;
    private Long postId;
    public static Builder builder() {
        return new Builder();
    }
    public static class Builder {
        private String topic;
        private Long userFromId;
        private Long userToId;
        private Long objectId;
        private String objectType;
        private Long postId;

        public Builder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder userFromId(Long userFromId) {
            this.userFromId = userFromId;
            return this;
        }

        public Builder userToId(Long userToId) {
            this.userToId = userToId;
            return this;
        }

        public Builder objectId(Long objectId) {
            this.objectId = objectId;
            return this;
        }

        public Builder objectType(String objectType) {
            this.objectType = objectType;
            return this;
        }

        public Builder postId(Long postId) {
            this.postId = postId;
            return this;
        }

        public Event build() {
            return new Event(topic, userFromId, userToId, objectId, objectType, postId);
        }

    }
}
