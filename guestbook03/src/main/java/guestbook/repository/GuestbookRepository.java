package guestbook.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import guestbook.repository.template.JdbcContext;
import guestbook.vo.GuestbookVo;

@Repository
public class GuestbookRepository {
	private JdbcContext jdbcContext;
	
	public GuestbookRepository(JdbcContext jdbcContext) {
		this.jdbcContext = jdbcContext;
	}
	
	public GuestbookVo findById(Long id) {
		return jdbcContext.queryForObject(
				"select id, name, contents, date_format(reg_date, '%Y-%m-%d %h:%i:%s') as regDate from guestbook where id = ?",
				new Object[] {id},
				new BeanPropertyRowMapper<>(GuestbookVo.class));
	}	
	public List<GuestbookVo> findAll() {
		return jdbcContext.query(
				"select id, name, contents, date_format(reg_date, '%Y-%m-%d %h:%i:%s') as regDate from guestbook order by reg_date desc"
				, new BeanPropertyRowMapper<>(GuestbookVo.class));
	}
//	new RowMapper<GuestbookVo>(){
//		@Override
//		public GuestbookVo mapRow(ResultSet rs, int rowNum) throws SQLException{
//			GuestbookVo vo = new GuestbookVo();
//			vo.setId(rs.getLong(1));
//			vo.setName(rs.getString(2));
//			vo.setContents(rs.getString(3));
//			vo.setRegDate(rs.getString(4));
//			
//			return vo;
//		}
//	} == new BeanPropertyRowMapper<>(GuestbookVo.class) 동일한 역할
	
	public int insert(GuestbookVo vo) {
		return jdbcContext.update(
				"insert into guestbook values(null, ?, ?, ?, now())",
				vo.getName(), vo.getPassword(), vo.getContents());
	}

	public int deleteByIdAndPassword(Long id, String password) {
		return jdbcContext.update(
				"delete from guestbook where id=? and password=?",
				id, password);
	}


}