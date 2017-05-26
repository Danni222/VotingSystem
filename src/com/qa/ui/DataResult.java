package com.qa.ui;

import java.util.List;

//作为反序列化的实体对象
public class DataResult {
	private String Content;
	
	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public List<Item> getList() {
		return list;
	}

	public void setList(List<Item> list) {
		this.list = list;
	}

	private List<Item> list;
}
