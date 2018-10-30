package cn.qj.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 投标
 * 
 * @author Qiujian
 * @date 2018/10/30
 */
@Setter
@Getter
@ToString
@Entity
public class Bid implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int SUCCEED = 0;
	public static final int FAILD = 1;

	private long id;
	/** 实际年利率 */
	private BigDecimal actualRate;
	private BigDecimal amount;
	private Borrow borrow;
	/** 借款标题 */
	private String borrowTitle;
	/** 投标人 投资人 */
	private LoginInfo createUser;
	private Date createTime;
	private int state = Bid.SUCCEED;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	@ManyToOne
	public Borrow getBorrow() {
		return borrow;
	}

	@OneToOne
	public LoginInfo getCreateUser() {
		return createUser;
	}
}
