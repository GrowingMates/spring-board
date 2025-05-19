package com.board.member.controller.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.board.global.resolver.AuthArgumentResolver;
import com.board.member.controller.member.dto.request.MemberRequest;
import com.board.member.domain.member.Member;
import com.board.member.service.member.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @MockBean
    private AuthArgumentResolver authArgumentResolver;

    private Member member;

    @BeforeEach
    void set() throws Exception {
        member = new Member("신짱구", "짱구", "aaa", "password123");
        given(authArgumentResolver.supportsParameter(any())).willReturn(true);
        given(authArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(1L);
    }

    @Test
    @DisplayName("유저 정보를 조회한다.")
    void showMember() throws Exception {
        // given
        Long memberId = 1L;
        given(memberService.findMember(memberId)).willReturn(member);

        // when & then
        mockMvc.perform(get("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("신짱구"));
    }

    @Test
    @DisplayName("유저 정보를 수정한다.")
    void updateMember() throws Exception {
        // given
        MemberRequest request = new MemberRequest("신짱구", "짱구", "newId", "newPassword");
        Member updatedResponse = new Member("신짱구", "짱구", "newId", "newPassword");
        given(memberService.updateMember(1L, request.name(), request.nickName(), request.id(), request.password())).willReturn(updatedResponse);

        // when & then
        mockMvc.perform(patch("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("newId"));
    }

    @Test
    @DisplayName("유저 정보를 삭제한다.")
    void deleteMember() throws Exception {
        // given
        given(memberService.deleteMember(1L)).willReturn(member);

        // when & then
        mockMvc.perform(delete("/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("신짱구"));
    }
}
