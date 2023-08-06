package com.cj.codergobackend.util;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
@Component
public class TextUtil {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(TextUtil.class);
    // 准备的敏感词文件名称
    private static final String SENSITIVE_WORDS_NAME = "sensitive-words.txt";
    // 准备替换敏感词的内容
    private static final String SENSITIVE_WORDS_REPLACE = "***";
    // 前缀树
    private static final Trie trie = new Trie();

    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream(SENSITIVE_WORDS_NAME);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String sensitiveWord = "";
            while ((sensitiveWord = reader.readLine()) != null) {
                // 添加到前缀树
                trie.addSensitiveWord(sensitiveWord);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败：" + e.getMessage());
        }
    }

    public static String filter(String text) {
        return trie.filter(text);
    }

    private static class Trie {
        private Node root;

        public Trie() {
            this.root = new Node();
        }

        public void addSensitiveWord(String word) {
            // 根节点
            Node node = this.root;
            for (int i = 0; i < word.length(); i++) {
                Character ch = word.charAt(i);
                Map<Character, Node> children = node.getChildren();
                if (!children.containsKey(ch)) {
                    children.put(ch, new Node());
                }
                // 指向敏感词节点
                node = children.get(ch);
            }
            // 敏感词末尾
            node.setEnd(true);
        }

        public String filter(String text) {
            if (StringUtils.isBlank(text)) {
                return null;
            }
            // 转换后的文本
            StringBuilder ans = new StringBuilder();
            Node node = this.root;
            for (int i = 0; i < text.length();) {
                char ch = text.charAt(i);
                if (!node.getChildren().containsKey(ch)) {
                    i++;
                    ans.append(ch);
                    continue;
                }
                Node endNode = node;
                // 说明是敏感词开头字符[i, ... end] 表示一个敏感词，pos 是偏移量
                int end = -1, pos = i;
                while (pos < text.length()) {
                    if (node.getChildren().containsKey(text.charAt(pos))) {
                        node = node.getChildren().get(text.charAt(pos));
                        if (node.isEnd()) {
                            end = pos;
                            endNode = node;
                        }
                        pos++;
                    } else {
                        break;
                    }
                }
                if (end >= i) {
                    ans.append(SENSITIVE_WORDS_REPLACE);
                    i = end + 1;
                } else {
                    ans.append(ch);
                    i++;
                }
                node = endNode;
            }
            return ans.toString();
        }

        private static class Node {
            // 子节点
            private Map<Character, Node> children;
            // 是否为敏感词末尾
            private boolean isEnd;
            public Node() {
                this.children = new HashMap<>();
            }
            // 是否为敏感词末尾节点
            public boolean isEnd() {
                return this.isEnd;
            }
            public void setEnd(boolean isEnd) {
                this.isEnd = isEnd;
            }
            public Map<Character, Node> getChildren() {
                return this.children;
            }
        }
    }
}

