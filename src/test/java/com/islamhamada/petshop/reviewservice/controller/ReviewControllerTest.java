package com.islamhamada.petshop.reviewservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.islamhamada.petshop.contracts.model.RestExceptionResponse;
import com.islamhamada.petshop.reviewservice.entity.Review;
import com.islamhamada.petshop.reviewservice.model.PostReviewRequest;
import com.islamhamada.petshop.reviewservice.model.SummarizeReivewsResponse;
import com.islamhamada.petshop.reviewservice.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest({"server.port=0"})
@EnableConfigurationProperties
@AutoConfigureMockMvc
class ReviewControllerTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper
            = new ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    SimpleGrantedAuthority adminRole = new SimpleGrantedAuthority("ROLE_Admin");
    SimpleGrantedAuthority customerRole = new SimpleGrantedAuthority("ROLE_Customer");

    @BeforeEach
    public void setup() {
        reviewRepository.deleteAll();
    }

    @Nested
    public class getReviewById {

        SimpleGrantedAuthority neededRole = adminRole;
        SimpleGrantedAuthority notNeededRole = customerRole;

        @Test
        public void success() throws Exception {
            Review dbReview = reviewRepository.save(Review.builder()
                    .text("text")
                    .rating(3)
                    .productId(1)
                    .userId(2)
                    .build());
            long reviewId = dbReview.getId();
            MvcResult mvcResult
                    = mockMvc.perform(get("/review/protected/" + reviewId)
                    .with(jwt().authorities(neededRole))
            ).andExpect(status().isOk())
                .andReturn();

            String mvcResponse = mvcResult.getResponse().getContentAsString();
            Review responseReview = objectMapper.readValue(mvcResponse, Review.class);
            assertEquals(reviewId, responseReview.getId());
            assertEquals(dbReview.getProductId(), responseReview.getProductId());
            assertEquals(dbReview.getUserId(), responseReview.getUserId());
            assertEquals(dbReview.getText(), responseReview.getText());
            assertEquals(dbReview.getRating(), responseReview.getRating());
        }

        @Test
        public void failure_missing_role() throws Exception {
            mockMvc.perform(get("/review/protected/1")
                    .with(jwt().authorities(notNeededRole))
            ).andExpect(status().isForbidden())
                .andReturn();
        }

        @Test
        public void failure_not_found() throws Exception {
            long reviewId = 1;
            MvcResult mvcResult = mockMvc.perform(get("/review/protected/" + reviewId)
                                        .with(jwt().authorities(neededRole))
                                ).andExpect(status().isNotFound())
                                .andReturn();
            String mvcResponse = mvcResult.getResponse().getContentAsString();
            RestExceptionResponse exceptionResponse = objectMapper.readValue(mvcResponse, RestExceptionResponse.class);
            assertEquals("REVIEW_NOT_FOUND", exceptionResponse.getError_code());
            assertEquals("Review with id: " + reviewId + " not found", exceptionResponse.getError_message());
        }

        @ParameterizedTest
        @MethodSource("bad_inputs")
        public void failure_bad_input(Object review_id) throws Exception {
            MvcResult mvcResult = mockMvc.perform(get("/review/protected/" + review_id)
                    .with(jwt().authorities(neededRole))
            ).andExpect(status().isBadRequest())
                    .andReturn();
        }

        public static Stream<Object> bad_inputs(){
            return Stream.of(-1, null);
        }
    }

    @Nested
    public class postProductReview {
        SimpleGrantedAuthority neededRole = customerRole;
        SimpleGrantedAuthority unneededRole = adminRole;

        @Test
        public void success() throws Exception {
            PostReviewRequest postReviewRequest = getMockPostReviewRequest();
            MvcResult mvcResult = mockMvc.perform(post("/review/protected")
                    .with(jwt().authorities(neededRole))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(postReviewRequest))
            ).andExpect(status().isOk())
                    .andReturn();

            String mvcResponse = mvcResult.getResponse().getContentAsString();
            Review review = objectMapper.readValue(mvcResponse, Review.class);

            assertEquals(review.getProductId(), postReviewRequest.getProductId());
            assertEquals(review.getUserId(), postReviewRequest.getUserId());
            assertEquals(review.getText(), postReviewRequest.getText());
            assertEquals(review.getRating(), postReviewRequest.getRating());
        }

        @Test
        public void failure_missing_role() throws Exception {
            PostReviewRequest request = getMockPostReviewRequest();
            mockMvc.perform(post("/review/protected")
                    .with(jwt().authorities(unneededRole))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isForbidden())
                    .andReturn();
        }

        @ParameterizedTest
        @MethodSource("bad_input")
        public void failure_bad_input(PostReviewRequest request) throws Exception {
            mockMvc.perform(post("/review/protected")
                    .with(jwt().authorities(neededRole))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest())
                    .andReturn();
        }

        public static List<PostReviewRequest> bad_input() {
            List<PostReviewRequest> rv = new ArrayList<PostReviewRequest>();
            rv.add(null);
            rv.add(PostReviewRequest.builder()
                    .productId(-1)
                    .userId(2)
                    .text("text")
                    .rating(1)
                    .build());
            rv.add(PostReviewRequest.builder()
                    .productId(1)
                    .userId(-2)
                    .text("text")
                    .rating(1)
                    .build());
            rv.add(PostReviewRequest.builder()
                    .productId(1)
                    .userId(2)
                    .text("a".repeat(256))
                    .rating(0)
                    .build());
            rv.add(PostReviewRequest.builder()
                    .productId(1)
                    .userId(2)
                    .text("text")
                    .rating(6)
                    .build());
            return rv;
        }

        private PostReviewRequest getMockPostReviewRequest() {
            PostReviewRequest postReviewRequest = PostReviewRequest.builder()
                    .productId(1)
                    .userId(2)
                    .text("text")
                    .rating(3)
                    .build();
            return postReviewRequest;
        }
    }

    @Nested
    public class getReviewsByProductId {

        @Test
        public void success() throws Exception {
            long product_id = 1;
            Review review = reviewRepository.save(
                    Review.builder()
                            .text("text")
                            .rating(3)
                            .productId(product_id)
                            .userId(2)
                            .build()
            );
            Review review2 = reviewRepository.save(
                    Review.builder()
                            .text("text2")
                            .rating(1)
                            .productId(product_id)
                            .userId(3)
                            .build()
            );
            Review review3 = reviewRepository.save(
                    Review.builder()
                            .text("text3")
                            .rating(4)
                            .productId(product_id + 1)
                            .userId(4)
                            .build()
            );
            MvcResult mvcResult = mockMvc.perform(get("/review/public/product/" + product_id))
                    .andExpect(status().isOk())
                    .andReturn();
            String response = mvcResult.getResponse().getContentAsString();
            List<Review> productReviews = objectMapper.readValue(response, new TypeReference<List<Review>>(){});
            assertEquals(2, productReviews.size());
            productReviews.forEach(r -> assertEquals(product_id, r.getProductId()));
            assertNotEquals(productReviews.get(0), productReviews.get(1));
        }

        @Test
        public void failure_bad_input() throws Exception {
            long product_id = -1;
            mockMvc.perform(get("/review/public/product/" + product_id))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }
    }

    @Nested
    public class summarizeReviewsByProductId {
        @Test
        void success_no_reviews() throws Exception {
            long product_id = 1;
            MvcResult mvcResult = mockMvc.perform(get("/review/public/product/summary/" + product_id))
                    .andExpect(status().isOk())
                    .andReturn();
            String response = mvcResult.getResponse().getContentAsString();
            SummarizeReivewsResponse summary = objectMapper.readValue(response, SummarizeReivewsResponse.class);
            assertEquals(0, summary.getCount());
            assertEquals(0, summary.getRating());
        }

        @Test
        void success_with_reviews() throws Exception {
            long product_id = 1;
            Review review = getMockReview();
            assertEquals(product_id, review.getProductId());
            reviewRepository.save(review);
            Review review2 = Review.builder()
                    .productId(product_id)
                    .userId(2)
                    .rating(2)
                    .text("text2")
                    .build();
            reviewRepository.save(review2);
            MvcResult mvcResult = mockMvc.perform(get("/review/public/product/summary/" + product_id))
                    .andExpect(status().isOk())
                    .andReturn();
            String response = mvcResult.getResponse().getContentAsString();
            SummarizeReivewsResponse summary = objectMapper.readValue(response, SummarizeReivewsResponse.class);
            assertEquals(2, summary.getCount());
            double avg = (review.getRating() + review2.getRating()) / 2.0;
            assertEquals(avg, summary.getRating());
        }

        @Test
        void failure_bad_input() throws Exception {
            long product_id = -1;
            mockMvc.perform(get("/review/public/product/summary/" + product_id))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }
    }

    @Nested
    public class getReviewByProductIdAndUserId {
        SimpleGrantedAuthority neededRole = customerRole;
        SimpleGrantedAuthority notNeededRole = adminRole;

        @Test
        void success_with_review() throws Exception {
            Review review = getMockReview();
            reviewRepository.save(review);
            MvcResult result = mockMvc.perform(get("/review/protected/product/user/" + review.getProductId() + "/" + review.getUserId())
                    .with(jwt().authorities(neededRole)))
                    .andExpect(status().isOk())
                    .andReturn();
            String response = result.getResponse().getContentAsString();
            Review review2 = objectMapper.readValue(response, Review.class);
            assertThat(review)
                    .usingRecursiveComparison()
                    .isEqualTo(review2);
        }

        @Test
        void success_no_review() throws Exception {
            MvcResult result = mockMvc.perform(get("/review/protected/product/user/1/1")
                            .with(jwt().authorities(neededRole)))
                    .andExpect(status().isOk())
                    .andReturn();
            String response = result.getResponse().getContentAsString();
            assertEquals("", response);
        }

        @Test
        void failure_no_permission() throws Exception {
            mockMvc.perform(get("/review/protected/product/user/1/1")
                    .with(jwt().authorities(notNeededRole)))
                    .andExpect(status().isForbidden())
                    .andReturn();
        }

        @ParameterizedTest
        @MethodSource("bad_input")
        void failure_bad_input(long productId, long userId) throws Exception {
            mockMvc.perform(get("/review/protected/product/user/" + productId + "/" + userId)
                            .with(jwt().authorities(neededRole)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        static List<Arguments> bad_input() {
            List<Arguments> list = new ArrayList<>();
            list.add(Arguments.of(-1, 1));
            list.add(Arguments.of(1, -1));
            return list;
        }
    }

    private Review getMockReview() {
        return Review.builder()
                .productId(1)
                .userId(1)
                .rating(4)
                .text("text")
                .build();
    }
}