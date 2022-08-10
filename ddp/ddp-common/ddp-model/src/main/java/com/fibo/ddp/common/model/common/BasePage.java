package com.fibo.ddp.common.model.common;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * 
 * @ClassName: BasePageVo <br/>
 * @Description: 分页公共基础bean. <br/>
 */
@Data
public class BasePage {
	
	/**
	 * 当前页数
	 */
	@TableField(exist = false)
	private int page;
	
	/**
	 * 每页显示的行数
	 */
	@TableField(exist = false)
	private int rows;

	/**
	 * 开始行数
	 */
	@TableField(exist = false)
	private Integer curRow;

	/**
	 * 结束行数
	 */
	@TableField(exist = false)
	private Integer endRow;
	
	/**
	 * 总行数
	 */
	@TableField(exist = false)
	private Integer total;
}
