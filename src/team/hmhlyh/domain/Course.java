package team.hmhlyh.domain;

import java.sql.Time;
/**
 * Model模型层，Course实体类(JavaBean)
 * @author 123
 *
 */
public class Course {
	
	private Integer courseNo;
	private Integer id;
	private String courseName;
	private String attribute;
	private String majorScope;
	private Byte credit;
	private String studySemester;
	private Long teaId;
	private Byte limitNum;
	private Byte startWeeks;
	private Byte endWeeks;
	private Integer location;
	private String intro;
	private String week;
	private Time startTime;
	private Time endTime;
	
	public Integer getCourseNo() {
		return courseNo;
	}
	public void setCourseNo(Integer courseNo) {
		this.courseNo = courseNo;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getMajorScope() {
		return majorScope;
	}
	public void setMajorScope(String majorScope) {
		this.majorScope = majorScope;
	}
	public Byte getCredit() {
		return credit;
	}
	public void setCredit(Byte credit) {
		this.credit = credit;
	}
	public String getStudySemester() {
		return studySemester;
	}
	public void setStudySemester(String studySemester) {
		this.studySemester = studySemester;
	}
	public Long getTeaId() {
		return teaId;
	}
	public void setTeaId(Long teaId) {
		this.teaId = teaId;
	}
	public Byte getLimitNum() {
		return limitNum;
	}
	public void setLimitNum(Byte limitNum) {
		this.limitNum = limitNum;
	}
	public Byte getStartWeeks() {
		return startWeeks;
	}
	public void setStartWeeks(Byte startWeeks) {
		this.startWeeks = startWeeks;
	}
	public Byte getEndWeeks() {
		return endWeeks;
	}
	public void setEndWeeks(Byte endWeeks) {
		this.endWeeks = endWeeks;
	}
	public Integer getLocation() {
		return location;
	}
	public void setLocation(Integer location) {
		this.location = location;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public Time getStartTime() {
		return startTime;
	}
	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}
	public Time getEndTime() {
		return endTime;
	}
	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}

}
