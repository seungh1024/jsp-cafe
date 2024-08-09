package codesquad.javacafe.member.repository;


import codesquad.javacafe.member.dto.request.MemberUpdateRequestDto;
import codesquad.javacafe.member.entity.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static codesquad.javacafe.common.db.DBConnection.close;
import static codesquad.javacafe.common.db.DBConnection.getConnection;

public class MemberRepository {
    private static final Logger log = LoggerFactory.getLogger(MemberRepository.class);
    private static final MemberRepository instance = new MemberRepository();
    public static MemberRepository getInstance() {
        return instance;
    }


    public void save(Member member) {
        log.debug("[Member] {}",member);

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();

            String sql;
            if (isH2Database(con)) {
                sql = "MERGE INTO member (member_id, member_password, member_name) " +
                        "KEY (member_id) VALUES (?, ?, ?)";
            } else {
                sql = "INSERT INTO member (member_id, member_password, member_name) " +
                        "SELECT ?, ?, ? FROM DUAL " +
                        "WHERE NOT EXISTS (SELECT * FROM member WHERE member_id = ?)";
            }

            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, member.getUserId());
            ps.setString(2, member.getPassword());
            ps.setString(3, member.getName());
            if (!isH2Database(con)) {
                ps.setString(4, member.getUserId());
            }
            ps.executeUpdate();

            var rs = ps.getGeneratedKeys();
            long pk = 0;
            if (rs.next()) {
                pk = rs.getLong(1);
            }
            member.setId(pk);

            log.debug("[Member Save] pk = {}",pk);

        } catch (SQLException exception) {
            log.error("[SQLException] throw error when member save, Class Info = {}", MemberRepository.class);
            throw new RuntimeException(exception);
        }finally {
            close(con, ps, null);
        }
    }

    private boolean isH2Database(Connection connection) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            String databaseProductName = metaData.getDatabaseProductName();
            return "H2".equalsIgnoreCase(databaseProductName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to detect database type", e);
        }
    }

    public List<Member> findAll() {
        var sql = " select * from member";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            ps = con.prepareStatement(sql);

            rs = ps.executeQuery();
            if (rs.next()) {
                var memberList = new ArrayList<Member>();
                do{
                    Member member = new Member();
                    member.setId(rs.getLong("id"));
                    member.setUserId(rs.getString("member_id"));
                    member.setPassword(rs.getString("member_password"));
                    member.setName(rs.getString("member_name"));
                    memberList.add(member);
                }while(rs.next());

                return memberList;
            } else {
                log.info("[MemberRepository] 사용자 정보가 없습니다.");
                return null;
            }

        } catch (SQLException exception) {
            log.error("[SQLException] throw error when findById, Class info = {}", MemberRepository.class);
            throw new RuntimeException(exception);
        }finally {
            close(con,ps,rs);
        }
    }

    public Member findByUserId(String userId) {
        var sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, userId);

            rs = ps.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setUserId(rs.getString("member_id"));
                member.setPassword(rs.getString("member_password"));
                member.setName(rs.getString("member_name"));

                return member;
            } else {
                log.info("[MemberRepository] 사용자 정보가 없습니다.");
                return null;
            }

        } catch (SQLException exception) {
            log.error("[SQLException] throw error when findById, Class info = {}", MemberRepository.class);
            throw new RuntimeException(exception);
        }finally {
            close(con, ps, null);
        }
    }

    public int update(MemberUpdateRequestDto memberDto) {
        var sql = "update member set member_password = ?, member_name = ?\n" +
                "where member_id = ? and member_password = ?";

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, memberDto.getPassword());
            ps.setString(2, memberDto.getName());
            ps.setString(3, memberDto.getUserId());
            ps.setString(4, memberDto.getOldPassword());

            return ps.executeUpdate();

        } catch (SQLException exception) {
            log.error("[SQLException] throw error when findById, Class info = {}", MemberRepository.class);
            throw new RuntimeException(exception);
        }finally {
            close(con, ps, null);
        }
    }

    public Member findById(long id) {
        var sql = "select * from member where id = ?";

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setLong(1, id);

            rs = ps.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setUserId(rs.getString("member_id"));
                member.setPassword(rs.getString("member_password"));
                member.setName(rs.getString("member_name"));

                return member;
            } else {
                log.info("[MemberRepository] 사용자 정보가 없습니다.");
                return null;
            }

        } catch (SQLException exception) {
            log.error("[SQLException] throw error when findById, Class info = {}", MemberRepository.class);
            throw new RuntimeException(exception);
        }finally {
            close(con, ps, null);
        }
    }
}
