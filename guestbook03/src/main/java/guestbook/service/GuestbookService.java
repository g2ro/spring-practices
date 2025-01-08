package guestbook.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import guestbook.repository.GuestbookLogRepository;
import guestbook.repository.GuestbookRepository;
import guestbook.vo.GuestbookVo;

@Service
public class GuestbookService {
	@Autowired
	private PlatformTransactionManager transactionManager;
	@Autowired
	private DataSource dataSource;
	
	private final GuestbookRepository guestbookRepository;
	private final GuestbookLogRepository guestbookLogRepository;
	
	public GuestbookService(GuestbookRepository guestbookRepository, GuestbookLogRepository guestbookLogRepository) {
		this.guestbookRepository = guestbookRepository;
		this.guestbookLogRepository = guestbookLogRepository;
	}
	
	public List<GuestbookVo> getContentsList() {
		return guestbookRepository.findAll();
	}
	
	public void deleteContents(Long id, String password) {
		// TX:BEGIN/////////
		TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition()); //트랜잭션 시작
		try {
			GuestbookVo vo = guestbookRepository.findById(id);
			if(vo == null) {
				return;
			}
		
			int count = guestbookRepository.deleteByIdAndPassword(id, password);
			if(count == 1) {
				guestbookLogRepository.update(vo.getRegDate());
			}
			// TX:END(SUCCESS) ///////
			transactionManager.commit(txStatus);
			
		} catch (Throwable e) {
			// TX:END(FAIL)/////////
			transactionManager.rollback(txStatus);
			
			throw new RuntimeException(e);
		}
	}
	
	public void addContents(GuestbookVo vo) {
		//트랜잭션 동기화(Connection) 초기화
		TransactionSynchronizationManager.initSynchronization();
		Connection conn = DataSourceUtils.getConnection(dataSource);
		
		try {
			// TX:BEGIN///////
			conn.setAutoCommit(false);

			int count = guestbookLogRepository.update();

			if (count == 0) {
				guestbookLogRepository.insert();
			}

			guestbookRepository.insert(vo);
			// TX:END(SUCCESS)///////
			conn.commit();
		} catch (Throwable e) {
			try {
				//TX:END(FAIL)///////
				conn.rollback();
			} catch (SQLException ignore) {
			}
			throw new RuntimeException(e);
		} finally {
				DataSourceUtils.releaseConnection(conn, dataSource);
			
		}
	}
}