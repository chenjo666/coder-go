package com.cj.studycirclebackend;

import com.cj.studycirclebackend.StudyCircleBackendApplication;
import com.cj.studycirclebackend.enums.PostType;
import com.cj.studycirclebackend.pojo.Post;
import com.cj.studycirclebackend.service.PostService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.suggest.Completion;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootTest(classes = {StudyCircleBackendApplication.class})
public class PostServiceTests {
    @Resource
    PostService postService;

    @Test
    public void insertData() {

    }
    @Test
    public void reSetType() {
        List<Post> list = postService.list();

        var map = new String[]{
                PostType.ALL.getValue(),
                PostType.DISCUSSION.getValue(),
                PostType.HELP.getValue(),
                PostType.SHARING.getValue(),
                PostType.TUTORIAL.getValue(),
                PostType.EMOTIONAL.getValue(),
                PostType.NEWS.getValue(),
                PostType.REVIEW.getValue(),
                PostType.SURVEY.getValue(),
                PostType.OTHER.getValue()
        };
        Random random = new Random();
        for (Post p : list) {
            p.setType(map[random.nextInt(9)]);
        }

        postService.updateBatchById(list);
    }

    @Test
    public void reSetTime() {
        List<Post> list = postService.list();
        Random random = new Random();
        for (Post p : list) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime randomTime = now.minusDays(random.nextInt(365)).minusHours(random.nextInt(24))
                    .minusMinutes(random.nextInt(60)).minusSeconds(random.nextInt(60));
            p.setPublishTime(Date.from(randomTime.atZone(ZoneId.systemDefault()).toInstant()));
        }
        postService.updateBatchById(list);
    }
    String text = "万物复苏的春天，正是踔厉奋发的时节。\n" +
            "\n" +
            "　　国内生产总值同比增长4.5%，社会消费品零售总额同比增长5.8%，全国固定资产投资同比增长5.1%，货物进出口总额同比增长4.8%……4月18日公布的中国经济2023年首季成绩单，展现中国经济在高质量发展中复苏向好的良好态势。\n" +
            "\n" +
            "　　“牢牢把握高质量发展这个首要任务”“在强国建设、民族复兴的新征程，我们要坚定不移推动高质量发展”——习近平总书记的重要指示坚定明晰。\n" +
            "\n" +
            "　　奋进在全面贯彻党的二十大精神的开局之年，以习近平同志为核心的党中央带领全党全国人民，坚持稳中求进工作总基调，加快构建新发展格局，着力推动高质量发展，更好统筹发展和安全，促进经济运行整体好转，推动中国经济巨轮乘风破浪、稳健前进，为全面推进中国式现代化提供更加强劲的发展动力。";
    @Test
    public void reSetContent() {
        List<Post> list = postService.list();
        Random random = new Random();
        for (Post p : list) {
            p.setContent(getRandomSentence(text));
        }
        postService.updateBatchById(list);
    }
    @Test
    public void resetScore() {
        Random random = new Random();
        List<Post> list = postService.list();
        for (Post p : list ){
            p.setScore(random.nextDouble(100));
        }
        postService.updateBatchById(list);
    }
    public String getRandomSentence(String text) {
        // 将文本按句号分割为句子数组
        String[] sentences = text.split("\\.");

        // 使用随机数生成器获取随机索引
        Random random = new Random();
        int randomIndex = random.nextInt(sentences.length);

        // 返回随机索引对应的句子
        return sentences[randomIndex].trim();
    }
}
