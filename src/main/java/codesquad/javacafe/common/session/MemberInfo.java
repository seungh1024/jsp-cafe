package codesquad.javacafe.common.session;

public class MemberInfo {
	private long id;
	private String userId;
	private String name;

	public MemberInfo(long id, String userId, String name) {
		this.id = id;
		this.userId = userId;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public String getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "MemberInfo{" +
				"id=" + id +
				", userId='" + userId + '\'' +
				", name='" + name + '\'' +
				'}';
	}
}
