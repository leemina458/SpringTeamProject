package controller;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import area.AreaDTO;
import board.BoardDTO;
import board.BoardLikeDTO;
import comment.CommentDTO;
import service.AreaDAO;
import service.AreaLikeDAO;
import service.AreaNoticeDAO;
import service.BoardDAO;
import service.BoardLikeDAO;
import service.CommentDAO;
import service.IndustryDAO;
import service.MemberDAO;

@Controller
@RequestMapping("/board/")
public class BoardController {
	// boardid, userid, dong_code 는 view 에서 값 받아야함
	public int boardid = 0;
	public String userid = "";
	public String name = "";
	public String dong_code = "";

	public String remoteId = "";
	public ModelAndView mv = new ModelAndView();

	@Autowired
	MemberDAO memberDB;

	@Autowired
	BoardDAO boardDB;

	@Autowired
	AreaDAO areaDB;

	@Autowired
	IndustryDAO industryDB;

	@Autowired
	CommentDAO commentDB;

	@Autowired
	BoardLikeDAO boardlikeDB;

	@Autowired
	AreaNoticeDAO areanoticeDB;

	@Autowired
	AreaLikeDAO arealikeDB;

	@ModelAttribute
	public void headProcess(HttpServletRequest request, HttpServletResponse res) {
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpSession session = request.getSession();
		remoteId = request.getRemoteAddr();
		if (request.getParameter("userid") != null) {
			session.setAttribute("userid", request.getParameter("userid"));
		}
		userid = (String) session.getAttribute("userid");
	}

	@RequestMapping(value = "/boardList/{code}", produces = "application/text; charset=utf8")
	@ResponseBody
	public ModelAndView boardList(@PathVariable("code") String code, BoardDTO article, Model model) throws Exception {
		ModelAndView mav = new ModelAndView();

		AreaDTO dongList = areaDB.dong(code);
		mav.addObject("dong", dongList);
		String dong_code = "";

		dong_code = dongList.getCode();

		int count = 0;
		List<BoardDTO> articles = null;
		// board 개수 count
		count = boardDB.getBoardCount(dong_code);
		if (count == 0) {
			mav.addObject("count", count);
			mav.addObject("userid", userid);
			mav.setViewName("jsp_nohead/boardList");
		}

		Map<Integer, Integer> boardLikeCount = new HashMap<Integer, Integer>();
		Map<Integer, Integer> userBoardLike = new HashMap<Integer, Integer>();
		if (count > 0) {
			// board list 뿌려주기
			articles = boardDB.getArticles(dong_code);
			mav.addObject("articleList", articles);
			// board 좋아요
			for (BoardDTO board : articles) {
				boardLikeCount.put(board.getBoardid(), boardlikeDB.getBoardLikeCount(board.getBoardid()));

				// user가 좋아요한 게시물
				List<BoardLikeDTO> ubl = boardlikeDB.checkBoardLike(userid);
				for (BoardLikeDTO bld : ubl) {
					if (bld.getBoardid() == board.getBoardid()) {
						userBoardLike.put(bld.getBoardid(), 1);
					}
				}
			}
		}
		mav.addObject("boardLikeCount", boardLikeCount);
		mav.addObject("userBoardLike", userBoardLike);
		// board 게시물 수
		mav.addObject("count", count);

		// =================comment list========================================
		int boardid = 0;
		int cnt = 0;
		int boardLikecnt = 0;
		String regdate = null;
		List<CommentDTO> comment = null;
		// key 값: Boardid , value 값 : boardid 에 달린 댓글 list
		Map<Integer, List<CommentDTO>> map = new HashMap<Integer, List<CommentDTO>>();
		Map<Integer, Integer> boardLike = new HashMap<Integer, Integer>();
		Map<Integer, String> regDatemap = new HashMap<Integer, String>();

		// 댓글 list
		if (count != 0) {
			for (BoardDTO b : articles) {

				boardid = b.getBoardid();
				// 날짜 계산 --------------
				regdate = b.getRegDate();
				regDatemap.put(boardid, regDate(regdate));
				// 날짜 계산 --------------
				cnt = commentDB.getCommentCount(boardid);
				boardLikecnt = boardlikeDB.getBoardLikeCount(boardid);
				// 댓글 개수
				mav.addObject("cnt", cnt);
				// 댓글 list
				comment = commentDB.getComments(boardid);

				map.put(boardid, comment);
				boardLike.put(boardid, boardLikecnt);
			}
			System.out.println("map:" + map);
			// 댓글 리스트
			mav.addObject("map", map);
			// 좋아요 수
			mav.addObject("boardLike", boardLike);
			mav.addObject("regDate", regDatemap);
		}
		mav.addObject("userid", userid);
		mav.setViewName("jsp_nohead/boardList");

		return mav;
	}

	// 날짜 변환 메소드
	public String regDate(String regdate) throws ParseException {

		String DateDays = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		Calendar calendar = Calendar.getInstance();
		Date date = new Date(calendar.getTimeInMillis());
		String todayDate = sdf.format(date);
		long todayTimestamp = sdf.parse(todayDate).getTime();
		// 오늘 날짜
		// 등록된 날짜 타임스탬프
		long nextdayTimestamp = sdf.parse(regdate).getTime();
		// 일수 차 (타임스탬프 기준)
		long days = (todayTimestamp - nextdayTimestamp) / (24 * 60 * 60 * 1000 * 365);
		// 시간 차 (타임스탬프 기준)
		long hour = (todayTimestamp - nextdayTimestamp) / (60000 * 60);
		// 분 차 (타임스탬프 기준)
		long minute = (todayTimestamp - nextdayTimestamp) / 60000;

		System.out.println("일수차" + days);
		System.out.println(("시간차" + hour));
		System.out.println("분 차" + minute);

		if (minute < 60) {
			if (minute < 1) {
				DateDays = "방금 전";
			} else
				DateDays = minute + "분 전";
		} else if (minute >= 60 && hour < 24) {
			if (hour == 1)
				DateDays = "한시간 전";
			if (hour == 2)
				DateDays = "두시간 전";
			if (hour == 3)
				DateDays = "세시간 전";
			if (hour == 4)
				DateDays = "네시간 전";
			if (hour == 5)
				DateDays = "다섯시간 전";
			if (hour == 6)
				DateDays = "여섯시간 전";
			if (hour == 7)
				DateDays = "일곱시간 전";
		} else if (hour >= 24 && days == 0) {
			DateDays = "하루 전";
			// 일수 차 ( 날짜 기준)
		} else if ((days / 30) == 1) {
			DateDays = "한달 전";
		} else if ((days / 7) == 1) {
			DateDays = "일주일 전";
		} else if ((days / 60) == 1) {
			DateDays = "두달 전";
		} else if (days == 0) {
			DateDays = "7시간 전";
		} else {
			DateDays = (days) + "일 전";
		}

		return DateDays;
	}

	@SuppressWarnings("deprecation")
	@RequestMapping(value = "writeUploadPro", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	@ResponseBody
	public void writeUploadPro(MultipartHttpServletRequest multipart, BoardDTO article) throws Exception {
		String today = null;
		String userid = multipart.getParameter("userid");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		today = sdf.format(new java.util.Date());

		MultipartFile multi = multipart.getFile("uploadfile");
		String filename = multi.getOriginalFilename();
		dong_code = multipart.getParameter("dongCode");

		if (filename != null && !filename.equals("")) {
			String uploadpath = multipart.getRealPath("/") + "/uploadFile";
			FileCopyUtils.copy(multi.getInputStream(),
					new FileOutputStream(uploadpath + "/" + multi.getOriginalFilename()));
			article.setFilename(filename);
		} else {
			article.setFilename("");
		}
		article.setDong_code(dong_code);
		article.setRegDate(today);
		article.setUserid(userid);
		int num = boardDB.insertArticle(article);
		System.out.println(num);

		// 최신 boardid 값
		boardid = boardDB.getnewBoardid(dong_code);
		System.out.println("boardid 값=========" + boardid);

		// AreaLike에 있는 userlist
		List<String> userList = arealikeDB.getUserid(dong_code);
		for (String user : userList) {
			System.out.println("user" + user + "dong_code" + dong_code + "boardid" + boardid + "today" + today);
			areanoticeDB.insertNotice(user, dong_code, boardid, today);
		}
	}

	@RequestMapping(value = "/commentUploadPro", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	@ResponseBody
	public String commentUploadPro(MultipartHttpServletRequest request, CommentDTO comment) throws Exception {
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		String regDate = sdf.format(today);

		comment.setBoardid(Integer.valueOf(request.getParameter("boardid")));
		comment.setContent(request.getParameter("content"));
		comment.setName(request.getParameter("name"));
		comment.setUserid(userid);
		comment.setRegDate(regDate);
		commentDB.insertComment(comment);

		return userid;
	}

	@RequestMapping("deletePro")
	public String deletePro(String userid, String dong_code, String boardid, Model m) throws Exception {
		int delete_ok = boardDB.deleteArticle(userid, dong_code, boardid);
		m.addAttribute("delete_ok", delete_ok);
		return "board/deletePro";
	}

}