package com.alpha.healthmobile.wxpay;

/**
 * @author SongJian E-mail:1129574214@qq.com
 * @date 2016-1-5 ионГ11:22:32
 * @version 1.0
 * @parameter
 * @return
 */
public class OrderDetail {
	private String subject;
	private String price;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "OrderDetail [subject=" + subject + ", price=" + price + "]";
	}
}
