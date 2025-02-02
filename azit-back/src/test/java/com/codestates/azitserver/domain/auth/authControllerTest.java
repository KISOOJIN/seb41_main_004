package com.codestates.azitserver.domain.auth;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.codestates.azitserver.domain.auth.controller.AuthController;
import com.codestates.azitserver.domain.auth.dto.AuthDto;
import com.codestates.azitserver.domain.auth.dto.response.AuthResponseDto;
import com.codestates.azitserver.domain.auth.service.AuthService;
import com.codestates.azitserver.domain.member.entity.Member;
import com.codestates.azitserver.domain.stub.MemberStubData;
import com.google.gson.Gson;

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class authControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private Gson gson;

	@MockBean
	private AuthService authService;

	Member member;

	@BeforeEach
	@DisplayName("테스트용 멤버 생성")
	public void init() {
		member = MemberStubData.stubMember();
	}

	// 비밀번호 인증 과정 테스트
	@Test
	@DisplayName("matchPassword 테스트")
	public void matchTrueTest() throws Exception {
		// given
		Long memberId = 1L;

		AuthDto.MatchPassword matchDto = new AuthDto.MatchPassword();
		matchDto.setPassword("123456@asdf");

		String content = gson.toJson(matchDto);

		doNothing().when(authService).passwordMatcher(Mockito.anyLong(), Mockito.any(AuthDto.MatchPassword.class));

		// when
		ResultActions actions =
			mockMvc.perform(
				post("/api/auth/{member-id:[0-9]+}/passwords/matchers", memberId)
					.header("Authorization", "Required JWT access token")
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.with(csrf())
					.content(content)
			);

		// then
		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("match-password",
				requestHeaders(headerWithName("Authorization").description("Jwt Access Token")),
				pathParameters(List.of(
					parameterWithName("member-id").description("회원 고유 식별자"))),
				requestFields(List.of(
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")))
			));
	}

	// 비밀번호 변경 테스트 작성
	@Test
	@DisplayName("patchPassword")
	public void updatePasswordTest() throws Exception {
		// given
		Long memberId = 1L;

		AuthDto.PatchPassword patchDto = new AuthDto.PatchPassword();
		patchDto.setNewPassword("new123456@asdf");
		patchDto.setNewPasswordCheck("new123456@asdf");

		String content = gson.toJson(patchDto);

		doNothing().when(authService)
			.updatePassword(Mockito.anyLong(), Mockito.any(AuthDto.PatchPassword.class), eq(member));

		// when
		ResultActions actions =
			mockMvc.perform(
				patch("/api/auth/{member-id:[0-9]+}/passwords", memberId)
					.header("Authorization", "Required JWT access token")
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.with(csrf())
					.content(content)
			);

		// then
		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("patch-password",
				requestHeaders(headerWithName("Authorization").description("Jwt Access Token")),
				pathParameters(List.of(
					parameterWithName("member-id").description("회원 고유 식별자"))),
				requestFields(List.of(
					fieldWithPath("newPassword").type(JsonFieldType.STRING).description("새 비밀번호"),
					fieldWithPath("newPasswordCheck").type(JsonFieldType.STRING).description("새 비밀번호 확인")))
			));
	}

	@Test
	@DisplayName("sendAuthNum")
	public void sendAuthNumTest() throws Exception {
		// given
		AuthDto.SendEmail dto = new AuthDto.SendEmail();
		dto.setEmail("stubmember@naver.com");

		String content = gson.toJson(dto);

		doNothing().when(authService)
			.sendAuthEmail(Mockito.any(AuthDto.SendEmail.class));

		// when
		ResultActions actions =
			mockMvc.perform(
				post("/api/auth/refresh/passwords/email")
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.with(csrf())
					.content(content)
			);

		// then
		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("send-authNum",
				requestFields(List.of(
					fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")))
			));
	}

	@Test
	@DisplayName("sendPassword")
	public void sendPasswordTest() throws Exception {
		// given
		AuthDto.SendPWEmail dto = new AuthDto.SendPWEmail();
		dto.setEmail("stubmember@naver.com");
		dto.setAuthNum("123456");

		String content = gson.toJson(dto);

		doNothing().when(authService)
			.resetPassword(Mockito.any(AuthDto.SendPWEmail.class));

		// when
		ResultActions actions =
			mockMvc.perform(
				post("/api/auth/refresh/passwords")
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.with(csrf())
					.content(content)
			);

		// then
		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("send-randomPW",
				requestFields(List.of(
					fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
					fieldWithPath("authNum").type(JsonFieldType.STRING).description("인증번호")))
			));
	}

	@Test
	@DisplayName("re-issueToken")
	public void reIssueTokenTest() throws Exception {
		// given
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Refresh", "Required JWT refresh token");
		String memberEmail = "testMemberEmail@hello.com";

		AuthResponseDto.TokenResponse tokenResponse = new AuthResponseDto.TokenResponse();
		tokenResponse.setAccessToken("New JWT access token");
		tokenResponse.setRefreshToken("JWT Refresh Token");

		given(authService.reIssueToken(Mockito.any(), Mockito.any())).willReturn(tokenResponse);

		// when
		ResultActions actions =
			mockMvc.perform(
				get("/api/auth/re-issue/{email:.+}", memberEmail)
					.header("Refresh", "Required JWT refresh token")
					.with(csrf())
			);

		// then
		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("token-reIssue",
				requestHeaders(
					headerWithName("Refresh").description("Jwt Refresh Token")),
				pathParameters(List.of(
					parameterWithName("email").description("Member Email"))),
				responseHeaders(
					headerWithName("Authorization").description("Jwt Access Token"),
					headerWithName("Refresh").description("Jwt Refresh Token")
				)));
	}

	@Test
	@DisplayName("logout")
	public void logoutTest() throws Exception {
		// given
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Required JWT access token");

		doNothing().when(authService).logout(Mockito.any());

		// when
		ResultActions actions =
			mockMvc.perform(
				post("/api/auth/logout")
					.header("Authorization", "Required JWT access token")
					.with(csrf())
			);

		// then
		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("logout",
				requestHeaders(
					headerWithName("Authorization").description("Jwt Access Token"))));
	}
}
