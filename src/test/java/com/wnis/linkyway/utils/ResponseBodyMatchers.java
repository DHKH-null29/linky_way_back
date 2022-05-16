package com.wnis.linkyway.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.test.web.servlet.ResultMatcher;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * ResponseBodyMatcher is very useful when using mvcMock.
 *
 * <p>This can be used to verify the contents of the body for the response when testing the controller.</p>
 *<p>example</p>
 * <pre> {@code
 *                 MvcResult mvcResult = mockMvc.perform(post("/api/tags")
 *                         .contentType("application/json")
 *                         .content(objectMapper.writeValueAsString(tagRequest)))
 *                         .andExpect(status().is(400))
 *                         .andExpect(responseBody() // create ResponseBodyMatcher
 *                                 .containsPropertiesAsJson(ErrorResponse.class)) // method
 *                         .andReturn();
 *
 * }
 *
 * </pre>
 *
 * @author Hyeongyu
 * @version 1.0.0
 */
public class ResponseBodyMatchers {
    private ObjectMapper objectMapper = new ObjectMapper();
    /**
     * <p>Validate that the field name and value of the ResponseBody object are the same as expected</p>
     * <p>You do not need to create Object for test. just use *.class</p>
     * @param expectedObject Object created for test
     * @param targetClass Expected class structure (*.class)
     */
    public <T>ResultMatcher containsObjectAsJson(
            Object expectedObject,
            Class<T> targetClass) {
        return mvcResult -> {
            String json =mvcResult.getResponse().getContentAsString();
            T actualObject = objectMapper.readValue(json, targetClass);
            assertThat(actualObject).extracting("errors").isEqualToComparingFieldByField(expectedObject);
        };
    }

    /**
     * <p>Verify that the field name of the ResponseBody object matches the field name of the expected class.</p>
     * @param targetClass Expected class structure (*.class)
     * @throws UnrecognizedPropertyException If the format of the target class and the response object does not match,
     * an exception is generated.
     */
    public <T>ResultMatcher containsPropertiesAsJson(
            Class<T> targetClass) {
        return mvcResult -> {
            String json = mvcResult.getResponse().getContentAsString();
            T actualObject = objectMapper.readValue(json, targetClass);
            for (Field field : targetClass.getFields()) {
                assertThat(actualObject).hasFieldOrProperty(field.getName());
            }
        };
    }

    public static ResponseBodyMatchers responseBody() {
        return new ResponseBodyMatchers();
    }


}

