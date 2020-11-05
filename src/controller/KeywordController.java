package controller;

import org.rosuda.REngine.Rserve.RConnection;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class KeywordController {

	@RequestMapping("/view/startupKeyword")
	public String startupKeyword() throws Throwable {
		return "view/startupKeyword";
	}

	@RequestMapping("/view/searchKeyword")
	@ResponseBody
	public String searchKeyword(@RequestParam("keyword") String keyword) {

		RConnection connection = null;
		try {
			connection = new RConnection();
			// 라이브러리 내용들
			connection.eval("library(httr)");
			connection.eval("library(rvest)");
			connection.eval("library(jsonlite)");
			connection.eval("library(stringr)");
			connection.eval("library(wordcloud2)");
			connection.eval("library(KoNLP)");
			// 블로그에서 가져온 내용을 담을 변수
			connection.eval("Nblog <- c()");
			// 네이버 ajax를 이용하여, 페이지 200개를 불러온다.
			connection.eval("for (i in 1:10) {"
					+ "  httr::GET(url = \"https://section.blog.naver.com/ajax/SearchList.nhn\","
					+ "            query = list(\"countPerPage\" = \"20\","
					+ "                         \"currentPage\"  = i,"
					+ "                         \"endDate\"      = \"2020-11-01\","
					+ "                         \"keyword\"      = " + keyword + ","
					+ "                         \"orderBy\"      = \"sim\","
					+ "                         \"startDate\"    = \"2020-10-22\","
					+ "                         \"type\"         = \"post\"),"
					+ "            add_headers(\"referer\" = \"https://section.blog.naver.com/Search/Post.nhn\")) %>%"
					+ "    httr::content(as = \"text\") %>%" + "    str_remove(pattern = '\\\\)\\\\]\\\\}\\',') %>%"
					+ "    jsonlite::fromJSON() -> naverBlog" + "   " + "  data <- naverBlog$result$searchList  "
					+ "  Nblog <- dplyr::bind_rows(Nblog, data) }");
			// 불러온 데이터 확인
			connection.eval("dplyr::glimpse(Nblog)");
			// 여기서 필요한 내용만 select 해준다. (id, no, title, content, url)
			connection.eval("Nblog <- Nblog %>%");
			connection.eval("dplyr::select(1, 2, 6, 7) %>%");
			connection.eval("dplyr::rename(id = blogId, no = logNo, title = noTagTitle) %>% ");
			connection.eval(
					"dplyr::mutate(url = stringr::str_glue(\"http://blog.naver.com/PostView.nhn?blogId={id}&logNo={no}\"), contents = NA)");
//			// 필요한 내용만 불러온 내용을 확인해준다.
//			connection.eval("dplyr::glimpse(Nblog)");
			// 받은 데이터에서 na가 된 content를 url에 들어가 찾아와 넣어준다.
			connection.eval("for(i in 1:nrow(Nblog)){ " 
					+ "  tryCatch({ "
					+ "    Nblog$contents[i] <- httr::GET(url = Nblog$url[i]) %>%  " 
					+ "      xml2::read_html() %>%  "
					+ "      rvest::html_nodes(css = \"div.se-main-container\")%>% "
					+ "      html_text(trim = TRUE) })}");
			// 에러 뜨는 내용을 다시 css로 돌려준다.
			connection.eval("which(is.na(Nblog$contents)) -> naData");
			// 에러 뜨는 내용들 다시 찾아서 넣어주기
			connection.eval("for(i in naData){ " 
					+ "  tryCatch({ "
					+ "    Nblog$contents[i] <- httr::GET(url = Nblog$url[i]) %>%  " 
					+ "      xml2::read_html() %>%  "
					+ "      rvest::html_nodes(css = \"div#postViewArea\")%>%  " 
					+ "      html_text(trim = TRUE) }}");
			// 에러내용은 지운다.			
			connection.eval("Nblog <- na.omit(Nblog)");
			
			// 키워드를 담을 저장공간
			connection.eval("words <- c()");
			
			// 불러온 블로그의 title들을 전처리해준다.
			// 모든 특수문자 제거
			connection.eval("nt <- gsub(\"[[:punct:]]\", \"\", Nblog$title)");
			// 빈공간 없애기
			connection.eval("nt <- gsub(\"(^ +| +$)\", \"\", nt)");
			// 제목에 끼어있는 amp 문자 제거
			connection.eval("nt <- gsub(\"amp\", \" \", nt)");
			// 숫자 제거
			connection.eval("nt<-gsub(\"[0-9]\", \"\",nt)");
			
			// KoNLP로 제목 한 줄로 부터 형태소 단위로 단어들을 추출해서 words에 넣어준다.
			connection.eval("for(i in 1:length(nt)){ " 
							+ "words <-c(words, KoNLP::extractNoun(nt[i])) "
							+ "}");
			  
			// 블로그의 content도 title과 마찬가지로 전처리와 추출한 형태소 단위 단어를 words에 추가한다.
			connection.eval("nc <- gsub(\"[[:punct:]]\", \"\", Nblog$contents)");
			connection.eval("nc <- gsub(\"(^ +| +$)\", \"\", nc)");
			connection.eval("nc<-gsub(\"\\n\", \"\",nc)");
			connection.eval("nc<-gsub(\"[0-9]\", \"\",nc)");
			connection.eval("for(i in 1:length(nc)){ " 
							+ "words <-c(words, KoNLP::extractNoun(nc[i]))" 
							+ "}");
			// 한 글자 단어를 없애주는 함수를 만들고 words에 다시 넣어준다.->한 단어 제거
			connection.eval("words <- Filter(function(x){nchar(x)>=2}, words)");
			// dataFrame 타입으로 변환해준다.
			connection.eval("df <- data_frame(line=1:length(words), text=words)");
			// dataFrame df의 text 컬럼 기준으로 숫자를 세준다. -> DB group by와 유사
			connection.eval("wordcnt <- count(df, text, sort=T)");
			// 전체에서 빈칸, 키워드 제외한 3번째 부터 203번째까지 200개 데이터를 정비한다.
			connection.eval("wordsresult <- wordcnt[c(3:203),]");
			connection.eval("wordcloud2(wordsresult, " + 
					"           fontFamily=\"Malgun Gothic\", " + 
					"           size = 0.5, " + 
					"           minRotation=0, " + 
					"           maxRotation=0, " + 
					")");
			connection.close();
			
		} catch (Exception e) {
			// TODO: handle exception
		}

		return "";
	}
	
	@RequestMapping("/view/searchKeyword2")
	@ResponseBody
	public String searchKeyword2() {
		return "/images/Rplot.png";
	}
}
