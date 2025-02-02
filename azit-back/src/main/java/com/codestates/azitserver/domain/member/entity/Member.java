package com.codestates.azitserver.domain.member.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.transaction.Transactional;

import com.codestates.azitserver.domain.club.entity.ClubMember;
import com.codestates.azitserver.domain.common.Auditable;
import com.codestates.azitserver.domain.fileInfo.entity.FileInfo;
import com.codestates.azitserver.domain.follow.entity.Follow;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Member extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MEMBER_ID")
	private Long memberId;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "FILE_INFO_ID")
	private FileInfo fileInfo;

	@Column(nullable = false, unique = true, length = 128, updatable = false)
	private String email;

	@Column(nullable = false, unique = true, length = 16)
	private String nickname;

	@Column(nullable = false)
	private String password;

	@Enumerated(value = EnumType.STRING)
	private Gender gender;

	@Column(name = "BIRTH_YEAR", length = 4)
	private String birthYear;

	@Column(name = "ABOUT_ME", length = 128)
	private String aboutMe;

	private Integer reputation = 10;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "MEMBER_STATUS")
	private MemberStatus memberStatus;

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	@JsonManagedReference
	@Column(name = "MEMBER_CATEGORY_LIST")
	private List<MemberCategory> memberCategoryList = new ArrayList<>();

	@OneToMany(mappedBy = "reportee")
	@JsonManagedReference
	@Column(name = "MEMBER_REPORT_LIST")
	private List<MemberMemberReport> memberMemberReportList = new ArrayList<>();

	@OneToMany
	@JsonManagedReference
	@Column(name = "CLUB_MEMBER_LIST")
	private List<ClubMember> clubMemberList = new ArrayList<>();

	@OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
	private List<Follow> followerList = new ArrayList<>();

	@OneToMany(mappedBy = "followee", cascade = CascadeType.ALL)
	private List<Follow> followingList = new ArrayList<>();

	public enum Gender {
		MALE("남자"),
		FEMALE("여자");

		private String gender;

		Gender(String gender) {
			this.gender = gender;
		}
	}

	public enum MemberStatus {
		ACTIVE("활성"),
		DELETED("탈퇴");

		private String memberStatus;

		MemberStatus(String memberStatus) {
			this.memberStatus = memberStatus;
		}
	}

	@Builder
	public Member(Long memberId, FileInfo fileInfo, String email, String nickname,
		String password, Gender gender, String birthYear, String aboutMe,
		Integer reputation, MemberStatus memberStatus, List<MemberCategory> memberCategoryList) {

		this.memberId = memberId;
		this.fileInfo = fileInfo;
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.gender = gender;
		this.birthYear = birthYear;
		this.aboutMe = aboutMe;
		this.reputation = reputation;
		this.memberStatus = memberStatus;
		this.memberCategoryList = memberCategoryList;
	}

	public void addMemberCategorySmallList(List<MemberCategory> memberCategoryList) {
		this.memberCategoryList = memberCategoryList;
	}

}
