package area;

import java.io.Serializable;

public class AreaLikeDTO implements Serializable {
	private String userid;
	private String  code;
	private String readed;
	
	public String getReaded() {
		return readed;
	}
	public void setReaded(String readed) {
		this.readed = readed;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Override
	public String toString() {
		return "AreaLikeDTO [userid=" + userid + ", code=" + code + ", readed=" + readed + "]";
	}
	
	
}