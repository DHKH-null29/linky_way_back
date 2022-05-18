package com.wnis.linkyway.security.jwt;

import com.wnis.linkyway.config.PropertiesConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = {PropertiesConfiguration.class})
class JwtPropertiesTest {

    @Autowired
    private JwtProperties jwtProperties;

    private final JwtProperties.AccessToken accessToken =
            new JwtProperties.AccessToken("access_token", 6L);

    @Test
    @DisplayName("Jwt 설정 정보 로드 테스트")
    void getJwtPropertiesTest() {
        assertThat(jwtProperties).isNotNull()
                .isInstanceOfSatisfying(JwtProperties.class, jwt -> {
                    assertThat(jwt.getSecretKey()).isNotNull();
                    assertThat(jwt.getAccessToken()).isNotNull()
                            .usingRecursiveComparison().isEqualTo(accessToken);
                });
    }

}
