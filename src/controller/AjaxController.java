package controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import area.AreaDTO;
import industry.IndustryDTO;
import service.AreaDAO;
import service.IndustryDAO;
import service.MemberDAO;

@Controller
@RequestMapping("/request/")
public class AjaxController {

	@Autowired
	AreaDAO areaDB;
	@Autowired
	IndustryDAO industryDB;
	@Autowired
	MemberDAO memberDB;

	// produces -> encoding문제 해결/안해주면 한글깨짐
	@RequestMapping(value = "/areaOption", method = RequestMethod.POST, produces = "application/text; charset=utf8")
	@ResponseBody
	// main화면에서 지역 선택 시 다음 옵션 동적으로 받아오기.
	public String mainOption(@RequestParam("area") String requestArea, @RequestParam("code") String requestCode)
			throws Throwable {

		String resultOption = "<option value=\"no\" disabled selected>선택</option>";
		List<AreaDTO> areaList = null;

		if (requestArea.contains("sido")) {
			areaList = areaDB.sigunguList(requestCode);
		}
		if (requestArea.contains("sigungu")) {
			areaList = areaDB.dongList(requestCode);
		}

		System.out.println("지역리스트------>" + areaList);
		for (AreaDTO area : areaList) {
			resultOption += "<option value=\"" + area.getCode() + "\">" + area.getName() + "</option>\n";
		}

		return resultOption;
	}

	@RequestMapping(value = "/categoryOption", method = RequestMethod.POST, produces = "application/text; charset=utf8")
	@ResponseBody
	// 카테고리 selectBox에서 옵션을 받기위한 bean
	public String categoryOption(@RequestParam("category") String requestCategory,
			@RequestParam("code") String requestCode) throws Throwable {
		List<IndustryDTO> industryList = null;
		String resultOption = "<option value=\"no\" disabled selected>선택</option>";

		if (requestCategory.contains("main")) {
			industryList = industryDB.category_middleList(requestCode);
		}
		if (requestCategory.contains("middle")) {
			industryList = industryDB.category_smallList(requestCode);
		}

		System.out.println("상업리스트------>" + industryList);
		for (IndustryDTO industry : industryList) {
			resultOption += "<option value=\"" + industry.getCode() + "\">" + industry.getName() + "</option>\n";
		}

		return resultOption;
	}

	@RequestMapping(value = "/loginCheck", method = RequestMethod.POST, produces = "application/text; charset=utf8")
	@ResponseBody
	public String loginCheck(@RequestParam("userid") String requestId, @RequestParam("pwd") String requestPwd)
			throws Throwable {
		boolean loginCheck = false;
		String result = null;
		loginCheck = memberDB.loginMember(requestId, requestPwd);
		if (loginCheck == true) {
			result = "ok";
		} else {
			result = "아이디나 비밀번호가 올바르지 않습니다.";
		}
		return result;
	}

	@RequestMapping(value = "/checkId", method = RequestMethod.POST, produces = "application/text; charset=utf8")
	@ResponseBody
	public String checkId(@RequestParam("userid") String requestId) throws Throwable {
		boolean checkId = memberDB.checkId(requestId);
		String result = null;

		if (checkId) {
			result = "이미 존재하는 아이디입니다.";
		} else {
			result = "사용가능합니다.";
		}
		return result;
	}
}
