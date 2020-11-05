<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div class="page startup-keyword" id="page">
	<!-- page title section -->
	<div class="page-title-box">
		<div class="page-name">창업키워드</div>
		<div class="page-desc">
			창업키워드는 소셜미디어 상에서 유통되고 있는 거대한 정보의 의미를 분석해 <br />
			워드클라우드로 제공해 핵심 이슈, 여론, 고객의 니즈, 트렌드 등 창업에 대한 새로운 인사이트를 제공합니다.
		</div>
	</div>
	
	<div class="content-body cf">
		<!-- search -->
		<form action="" method="post" class="search-group">
			<!-- 검색항목 묶음 - 구역선택 -->
			<div class="search-box">
				<div class="input-box">
					<div class="label-box">키워드</div>
					<input type="text" name="keyword" placeholder="조회할 검색어를 입력하세요"/>
				</div>
				<div class="input-box">
					<div class="label-box">조회기간</div>
					<input type="date" name="from" />
					<input type="date" name="to" />
				</div>
				<div class="input-box">
					<div class="label-box">SNS 종류</div>
					<div class="check-box">
						<input type="checkbox" name="sns" id="checkAll" checked/>
						<label for="checkAll">전체</label>
					</div>
					<div class="check-box">
						<input type="checkbox" name="sns" id="checkInsta"/>
						<label for="checkInsta">인스타그램</label>
					</div>
					<div class="check-box">
						<input type="checkbox" name="sns" id="checkInsta"/>
						<label for="checkInsta">페이스북</label>
					</div>
				</div>
			</div>
					
			<!-- 검색버튼 -->
			<input type="button" onclick="javascript:searchKeyword()" class="btn-full btn01-reverse" value="검색">
		</form>
		<!-- search of end -->
		
		<!-- content -->
		<div class="content">
			<div class="wordcloud" id="wordcloud"><img src="<%=request.getContextPath()%>/images/temp2.png" alt="temp2" style="width:600px;" /></div>
			<table class="tbl">
				<tr>
					<td>1위</td>
					<td>창조경제</td>
					<td>
						<div class="bar-graph">
							<div style="width:70%"></div>
						</div>
					</td>
					<td><span>6000</span>건</td>
				</tr>
				<tr>
					<td>2위</td>
					<td>창조경제</td>
					<td width="120px">
						<div class="bar-graph">
							<div style="width:100%"></div>
						</div>
					</td>
					<td><span>6000</span>건</td>
				</tr>
			</table>
		</div>
	</div>
</div>