package codesquad.javacafe.common.session;

import java.util.Objects;

import codesquad.javacafe.common.exception.ClientErrorCode;
import jakarta.servlet.http.HttpServletRequest;

public class SessionManager {
	private static final SessionManager instance = new SessionManager();
	private SessionManager(){};
	public static SessionManager getInstance(){
		return instance;
	}

	public MemberInfo loginCheck(HttpServletRequest req, String key) {
		var memberInfo = (MemberInfo)req.getSession().getAttribute(key);

		if (memberInfo == null) {
			throw ClientErrorCode.UNAUTHORIZED_USER.customException("Unauthorized user info = "+memberInfo);
		}
		return memberInfo;
	}

	public String getMemberName(HttpServletRequest req, String key) {
		var memberInfo = (MemberInfo)req.getSession().getAttribute(key);
		return memberInfo.getName();
	}

	public long getMemberId(HttpServletRequest req, String key) {
		var memberInfo = (MemberInfo)req.getSession().getAttribute(key);
		return memberInfo.getId();
	}
}
