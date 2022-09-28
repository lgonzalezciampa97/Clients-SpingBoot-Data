package com.bolsadeideas.springboot.app.util.paginator;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.domain.Page;

public class PageRender<T> {
	
	private String URL;
	private Page<T> page;
	
	private int totalPages;
	private int numElementsByPage;
	public int actualPage;
	
	private List<PageItem> pages;
	
	public PageRender(String uRL, Page<T> page) {
		URL = uRL;
		this.page = page;
		this.pages = new LinkedList<PageItem>();
		
		numElementsByPage = page.getSize();
		totalPages = page.getTotalPages();
		actualPage = page.getNumber() + 1;
		
		int start, end;
		if(totalPages <= numElementsByPage) {
			start = 1;
			end = totalPages;
		}
		else {
			if(actualPage <= numElementsByPage/2) {
				start = 1;
				end = numElementsByPage;
			}
			else if(actualPage >= totalPages - numElementsByPage/2) {
				start = totalPages - numElementsByPage + 1;
				end = numElementsByPage;
			}
			else {
				start = actualPage - numElementsByPage/2;
				end = numElementsByPage;
			}
		}
		
		for(int i = 0; i < end; i++) {
			pages.add(new PageItem(start + i, actualPage == start+i));
		}
		
	}

	public String getURL() {
		return URL;
	}


	public int getTotalPages() {
		return totalPages;
	}


	public int getActualPage() {
		return actualPage;
	}


	public List<PageItem> getPages() {
		return pages;
	}


	public boolean isFirst() {
		return page.isFirst();
	}
	
	public boolean isLast() {
		return page.isLast();
	}
	
	public boolean isHasNext() {
		return page.hasNext();
	}
	
	public boolean isHasPrevious() {
		return page.hasPrevious();
	}
	
}
