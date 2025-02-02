package com.codestates.azitserver.domain.auth;

import static com.codestates.azitserver.global.utils.AsciiDocsUtils.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.codestates.azitserver.domain.auth.dto.AuthDto;
import com.codestates.azitserver.domain.fileInfo.entity.FileInfo;
import com.codestates.azitserver.domain.member.entity.Member;
import com.codestates.azitserver.domain.member.repository.MemberRepository;
import com.google.gson.Gson;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class authLoginTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private Gson gson;

	@BeforeEach
	@DisplayName("테스트용 멤버 생성")
	public void init() {
		// 파일 생성
		FileInfo fileInfo = new FileInfo();
		fileInfo.setFileName("이미지파일이름.png");
		fileInfo.setFileUrl("/folder1/folder2");
		fileInfo.setCreatedAt(null);
		fileInfo.setLastModifiedAt(null);

		// 인코딩된 패스워드
		String encodedPassword = passwordEncoder.encode("123456@asdf");

		// 테스트용 멤버 생성
		Member member = Member.builder()
			.fileInfo(fileInfo)
			.email("stubmember01@naver.com")
			.nickname("김스텁01")
			.password(encodedPassword)
			.gender(Member.Gender.MALE)
			.birthYear("2001")
			.aboutMe("김스텁의 자기소개")
			.reputation(10)
			.memberStatus(Member.MemberStatus.ACTIVE)
			.build();

		// 테스트용 멤버 저장
		memberRepository.save(member);
	}

	// 로그인 요청이 잘 이루어지는지 확인 /api/auth/login
	@Test
	@DisplayName("로그인하면 토큰과 회원정보를 보내준다.")
	public void loginTest() throws Exception {
		// given - LoginDto로 요청값 받으면 json 형식으로
		AuthDto.Post loginDto = new AuthDto.Post();
		loginDto.setEmail("stubmember01@naver.com");
		loginDto.setPassword("123456@asdf");
		// loginDto.setEmail("admin_test@hello.com");
		// loginDto.setPassword("1234qwer!@#$");

		String content = gson.toJson(loginDto);

		// when - 로그인 api에 요청
		ResultActions actions =
			mockMvc.perform(
				post("/api/auth/login")
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.with(csrf())
					.content(content)
			);

		// then - 로그인되는지 확인
		actions
			.andExpect(status().isOk())
			.andExpect(header().exists("Authorization"))
			.andExpect(header().exists("Refresh"))
			.andDo(document("login",
				getRequestPreProcessor(),
				getResponsePreProcessor(),
				requestFields(List.of(
					fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"))),
				responseHeaders(
					headerWithName("Authorization").description("액세스 토큰"),
					headerWithName("Refresh").description("리프레시 토큰")),
				responseFields(List.of(
					fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("멤버아이디"),
					fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
					fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
					fieldWithPath("profileUrl").type(JsonFieldType.STRING).description("프로필 이미지 주소"),
					fieldWithPath("profileImageName").type(JsonFieldType.STRING).description("프로필 이미지 이름")))
			));
	}
}