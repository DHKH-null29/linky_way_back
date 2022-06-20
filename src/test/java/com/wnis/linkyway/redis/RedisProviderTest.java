package com.wnis.linkyway.redis;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedisProviderTest {

    @Autowired
    private RedisProvider redisProvider;

    private final String normalKey = "scott";
    private final String normalValue = "1234";

    private final String objectKey = "김갑환";
    private final SampleObj objectValue = new SampleObj("최번개", 25);

    @BeforeAll
    void setup() {
        redisProvider.setDataWithExpiration(normalKey, normalValue, 60000);
        redisProvider.setDataWithExpiration(objectKey, objectValue, 60000);
    }

    @Test
    @DisplayName("저장된 일반 key-value 데이터를 조회할 수 있다.")
    void findStringValueByKeyShouldReturnStringResult() {
        assertThat(redisProvider.getData(normalKey)).isEqualTo(normalValue);
    }

    @Test
    @DisplayName("저장된 객체 key-value 데이터를 조회할 수 있다.")
    void findObjectValueByKeyAndTypeShouldReturnObjectResult() {
        SampleObj result = redisProvider.getData(objectKey, SampleObj.class);
        assertThat(result)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(objectValue);
    }

    @Test
    @DisplayName("객체 key로 조회 시 부적절한 객체 타입일 경우 null을 반환한다")
    void findObjectValueByKeyAndWrongTypeShouldReturnNull() {
        Object result = redisProvider.getData(objectKey, SampleWrongObj.class);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("만료된 데이터를 조회할 경우 null 을 반환한다.")
    void findExpiredStringValueByKeyShouldReturnNull() throws InterruptedException {
        redisProvider.setDataWithExpiration("key", "value", 500);
        await().pollDelay(Duration.ofMillis(1000))
                .untilAsserted(() -> {
                    assertThat(redisProvider.getData("key")).isNull();
                });
    }

    @AfterAll
    void destroy() {
        redisProvider.deleteData(normalKey);
        assertThat(redisProvider.getData(normalKey)).isNull();

        redisProvider.deleteData(objectKey);
        assertThat(redisProvider.getData(objectKey)).isNull();
    }

    static class SampleObj {

        private String name;
        private int age;

        public SampleObj(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

    }

    static class SampleWrongObj {

        private String name;

        public SampleWrongObj(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

}
